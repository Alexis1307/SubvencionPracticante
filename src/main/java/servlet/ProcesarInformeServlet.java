package servlet;

import dao.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.*;
import util.JpaUtil;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

@WebServlet("/procesarInforme")
public class ProcesarInformeServlet extends HttpServlet {

    private InformeFlujoDAO flujoDAO = new InformeFlujoDAO();
    private InformeDAO informeDAO = new InformeDAO();
    private NotificacionDAO notificacionDAO = new NotificacionDAO();

    private EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();
    private UsuarioDAO usuarioDAO = new UsuarioDAO(emf);
    private FirmaDigitalDAO firmaDigitalDAO = new FirmaDigitalDAO(emf);

    private static final String RUTA_FIRMA = "C:\\SubvencionPracticante\\ProyectoSubvencionPDF\\firmasDigitales\\firma.png";
    private static final String RUTA_DOCS_JEFE = "C:\\SubvencionPracticante\\ProyectoSubvencionPDF\\jefeUnidad\\";
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuarioLogueado") : null;

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String idStr = request.getParameter("informeId");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El ID del informe es obligatorio.");
            return;
        }

        int informeId = Integer.parseInt(idStr);
        String accion = request.getParameter("accion");
        String comentario = request.getParameter("comentario");
        String nombreUsuario = usuario.getNombreUsuario().toLowerCase();

        // Si el Jefe de Unidad aprueba, requiere autenticación
        if ("aprobar".equalsIgnoreCase(accion) && "jefeunidad".equals(nombreUsuario)) {
            String usuarioInput = request.getParameter("usuario");
            String contrasenaInput = request.getParameter("contrasena");

            if (usuarioInput == null || contrasenaInput == null ||
                !usuarioInput.equals("jefeUnidad") || !contrasenaInput.equals("123")) {

            	request.setAttribute("mensajeErrorFirma", "Usuario o Contraseña incorrectas.");
                request.setAttribute("informeIdModal", informeId); // Para saber qué informe abrir
                request.setAttribute("abrirModal", true);

                // JefeUnidad ve todos los informes
                List<Informe> informes = informeDAO.obtenerTodosLosInformes();
                request.setAttribute("informes", informes);

                request.getRequestDispatcher("/jefeUnidad.jsp").forward(request, response);
                return;
            }

            // Insertar firma
            try {
                String nuevaRuta = insertarFirmaEnInforme(informeId, usuario.getUsuarioId());
                guardarFirmaDigital(informeId, usuario.getUsuarioId(), nuevaRuta);
                informeDAO.actualizarRutaInforme(informeId, nuevaRuta);
            } catch (IOException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Error: No se pudo insertar la firma digital.");
                return;
            }
        }

        String rolOrigen = nombreUsuario;
        String rolDestino = determinarDestino(accion, rolOrigen);

        Informe informe = new Informe();
        informe.setInformeID(informeId);

        // Registrar flujo solo si aprueba o rechaza
        InformeFlujo flujo = new InformeFlujo();
        flujo.setInforme(informe);
        flujo.setUsuario(usuario);
        flujo.setRolOrigen(rolOrigen);
        flujo.setRolDestino(rolDestino);
        flujo.setEstado(accion.equalsIgnoreCase("aprobar") ? "En revision" : "Rechazado");
        flujo.setComentario(comentario);
        flujo.setFecha(LocalDate.now());
        flujoDAO.registrarFlujo(flujo);

        // Actualiza el flujo anterior (el activo)
        String nuevoEstadoFlujo = accion.equalsIgnoreCase("aprobar") ? "Aprobado" : "Rechazado";
        flujoDAO.actualizarEstadoFlujoPorInforme(informeId, nuevoEstadoFlujo);

        // Actualizar estado e insertar notificaciones
        if ("aprobar".equalsIgnoreCase(accion)) {
            if ("jefeunidad".equals(nombreUsuario)) {
                Usuario rrhh = usuarioDAO.buscarPorNombre("rrhh");
                if (rrhh != null) {
                    Notificacion noti = new Notificacion();
                    noti.setUsuario(rrhh);
                    noti.setMensaje("Nuevo informe aprobado por el Jefe de Unidad. Revisión pendiente.");
                    noti.setFecha(LocalDate.now());
                    notificacionDAO.crearNotificacion(noti);
                }
                informeDAO.actualizarEstadoInforme(informeId, "En revision");
            } else if ("rrhh".equals(nombreUsuario)) {
                informeDAO.actualizarEstadoInforme(informeId, "Aprobado");

                Usuario practicante = obtenerUsuarioPorInforme(informeId);
                if (practicante != null) {
                    Notificacion noti = new Notificacion();
                    noti.setUsuario(practicante);
                    noti.setMensaje("Tu informe ha sido aprobado por RRHH.");
                    noti.setFecha(LocalDate.now());
                    notificacionDAO.crearNotificacion(noti);
                }
                
                Usuario especialista = usuarioDAO.buscarPorNombre("especialista");
                if (especialista != null) {
                    Notificacion notiEsp = new Notificacion();
                    notiEsp.setUsuario(especialista);
                    notiEsp.setMensaje("Nuevo informe recibido desde RRHH.");
                    notiEsp.setFecha(LocalDate.now());
                    notificacionDAO.crearNotificacion(notiEsp);
                }
            }
        } else if ("rechazar".equalsIgnoreCase(accion)) {
            informeDAO.actualizarEstadoInforme(informeId, "Rechazado");

            if (rolDestino != null) {
                Usuario destino = usuarioDAO.buscarPorNombre(rolDestino);
                if (destino != null) {
                    Notificacion noti = new Notificacion();
                    noti.setUsuario(destino);
                    noti.setMensaje("Informe rechazado. Observaciones: " + comentario);
                    noti.setFecha(LocalDate.now());
                    notificacionDAO.crearNotificacion(noti);
                }
            }
        }

        // Mensaje de éxito
        request.setAttribute("mensaje", "Informe procesado correctamente.");
        request.setAttribute("tipoMensaje", "success");
        request.setAttribute("informeIdProcesado", informeId);

        // Mostrar los informes dependiendo del rol
        List<Informe> informes;
        if ("jefeunidad".equals(nombreUsuario)) {
            informes = informeDAO.obtenerTodosLosInformes();
        } else if ("rrhh".equals(nombreUsuario)) {
            informes = informeDAO.obtenerInformesPorEstados(Arrays.asList("En revision", "Aprobado", "Rechazado"));
        } else {
            informes = List.of(); // Otros roles no tienen informes visibles
        }

        request.setAttribute("informes", informes);
        
        if ("jefeunidad".equals(nombreUsuario)) {
            response.sendRedirect(request.getContextPath() + "/jefeUnidad");

        } else if ("rrhh".equals(nombreUsuario)) {
        	session.setAttribute("mensaje", "Informe procesado correctamente.");
        	session.setAttribute("tipoMensaje", "success");
            response.sendRedirect(request.getContextPath() + "/rrhh");
        }    
    }

    private String determinarDestino(String accion, String rolOrigen) {
        if ("aprobar".equalsIgnoreCase(accion)) {
            if ("jefeunidad".equalsIgnoreCase(rolOrigen)) return "rrhh";
            if ("rrhh".equalsIgnoreCase(rolOrigen)) return null;
        } else {
            if ("rrhh".equalsIgnoreCase(rolOrigen)) return "jefeunidad";
            if ("jefeunidad".equalsIgnoreCase(rolOrigen)) return "practicante";
        }
        return null;
    }

    private String insertarFirmaEnInforme(int informeId, int usuarioId) throws IOException {
        Informe informe = informeDAO.obtenerInformePorId(informeId);
        if (informe == null) throw new IOException("Informe no encontrado ID: " + informeId);

        String rutaPDFOriginal = informe.getRutaDocumento();
        if (rutaPDFOriginal == null || rutaPDFOriginal.isEmpty())
            throw new IOException("Ruta del informe vacía para ID: " + informeId);

        String nombreArchivo = new File(rutaPDFOriginal).getName();
        String nuevaRutaPDF = RUTA_DOCS_JEFE + nombreArchivo.replace(".pdf", "_firmado.pdf");

        File archivoOriginal = new File(rutaPDFOriginal);
        if (!archivoOriginal.exists()) throw new IOException("Archivo no encontrado: " + rutaPDFOriginal);

        File carpetaDestino = new File(RUTA_DOCS_JEFE);
        if (!carpetaDestino.exists() && !carpetaDestino.mkdirs())
            throw new IOException("No se pudo crear la carpeta: " + RUTA_DOCS_JEFE);

        PDDocument documento = PDDocument.load(archivoOriginal);
        try {
            var pagina = documento.getPage(0);
            PDImageXObject imagenFirma = PDImageXObject.createFromFile(RUTA_FIRMA, documento);
            PDPageContentStream contenido = new PDPageContentStream(
                documento, pagina, PDPageContentStream.AppendMode.APPEND, true);

            float x = 400, y = 100, ancho = 150, alto = 50;
            contenido.drawImage(imagenFirma, x, y, ancho, alto);
            contenido.close();

            documento.save(nuevaRutaPDF);
        } finally {
            documento.close();
        }

        return nuevaRutaPDF;
    }

    private void guardarFirmaDigital(int informeId, int usuarioId, String rutaFirma) {
        FirmaDigital firma = new FirmaDigital();
        Informe informe = new Informe();
        informe.setInformeID(informeId);
        Usuario user = new Usuario();
        user.setUsuarioId(usuarioId);

        firma.setInforme(informe);
        firma.setUsuario(user);
        firma.setRutaImagenFirma(rutaFirma);
        firma.setFecha(LocalDate.now());

        if (!firmaDigitalDAO.guardarFirma(firma)) {
            System.out.println("Error guardando la firma digital en BD.");
        }
    }

    private Usuario obtenerUsuarioPorInforme(int informeId) {
        return usuarioDAO.buscarPorNombre("practicante");
    }
}