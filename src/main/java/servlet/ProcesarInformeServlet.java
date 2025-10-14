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
import java.time.LocalDateTime;

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

    private static final String RUTA_FIRMA = "C:\\ProyectoSubvencionPDF\\firmasDigitales\\firma.png";
    private static final String RUTA_DOCS_JEFE = "C:\\ProyectoSubvencionPDF\\jefeUnidad\\";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuarioLogueado") : null;

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        int informeId = Integer.parseInt(request.getParameter("informeId"));
        String accion = request.getParameter("accion");
        String comentario = request.getParameter("comentario");

        String nombreUsuario = usuario.getNombreUsuario().toLowerCase();

        // Validar credenciales si el jefeUnidad aprueba
        if ("aprobar".equalsIgnoreCase(accion) && "jefeunidad".equals(nombreUsuario)) {
            String usuarioInput = request.getParameter("usuario");
            String contrasenaInput = request.getParameter("contrasena");

            if (usuarioInput == null || contrasenaInput == null ||
                !usuarioInput.equals("jefeUnidad") || !contrasenaInput.equals("123")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Error: Credenciales inválidas. No se pudo insertar la firma digital.");
                return;
            }

            // Intentar insertar firma
            try {
                String nuevaRuta = insertarFirmaEnInforme(informeId, usuario.getUsuarioId());
                guardarFirmaDigital(informeId, usuario.getUsuarioId(), nuevaRuta);
                informeDAO.actualizarRutaInforme(informeId, nuevaRuta);
            } catch (IOException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Error: No se pudo insertar la firma digital. Operación cancelada.");
                return;
            }
        }

        // Solo si llegamos aquí, continuamos con el flujo normal

        String rolOrigen = nombreUsuario;
        String rolDestino = determinarDestino(accion, rolOrigen);

        Informe informe = new Informe();
        informe.setInformeID(informeId);

        // Registrar flujo del informe
        InformeFlujo flujo = new InformeFlujo();
        flujo.setInforme(informe);
        flujo.setUsuario(usuario);
        flujo.setRolOrigen(rolOrigen);
        flujo.setRolDestino(rolDestino);
        flujo.setEstado(accion.equalsIgnoreCase("aprobar") ? "Enviado" : "Rechazado");
        flujo.setComentario(comentario);
        flujo.setFecha(LocalDateTime.now());
        flujoDAO.registrarFlujo(flujo);

        if ("aprobar".equalsIgnoreCase(accion)) {
            if ("jefeunidad".equals(nombreUsuario)) {
                Usuario rrhh = usuarioDAO.buscarPorNombre("rrhh");
                if (rrhh != null) {
                    Notificacion noti = new Notificacion();
                    noti.setUsuario(rrhh);
                    noti.setMensaje("Nuevo informe aprobado por el Jefe de Unidad. Revisión pendiente.");
                    noti.setFecha(LocalDateTime.now());
                    notificacionDAO.crearNotificacion(noti);
                }
                informeDAO.actualizarEstadoInforme(informeId, "En revisión");

            } else if ("rrhh".equals(nombreUsuario)) {
                informeDAO.actualizarEstadoInforme(informeId, "Aprobado");

                Usuario practicante = obtenerUsuarioPorInforme(informeId);
                if (practicante != null) {
                    Notificacion noti = new Notificacion();
                    noti.setUsuario(practicante);
                    noti.setMensaje("Tu informe ha sido aprobado por RRHH.");
                    noti.setFecha(LocalDateTime.now());
                    notificacionDAO.crearNotificacion(noti);
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
                    noti.setFecha(LocalDateTime.now());
                    notificacionDAO.crearNotificacion(noti);
                }
            }
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Informe procesado correctamente.");
    }

    private String determinarDestino(String accion, String rolOrigen) {
        if ("aprobar".equalsIgnoreCase(accion)) {
            if ("jefeunidad".equalsIgnoreCase(rolOrigen)) {
                return "rrhh";
            } else if ("rrhh".equalsIgnoreCase(rolOrigen)) {
                return null; // no hay siguiente destino
            }
        } else {
            if ("rrhh".equalsIgnoreCase(rolOrigen)) {
                return "jefeunidad";
            } else if ("jefeunidad".equalsIgnoreCase(rolOrigen)) {
                return "practicante";
            }
        }
        return null;
    }

    private String insertarFirmaEnInforme(int informeId, int usuarioId) throws IOException {
        // Obtener la ruta del informe desde la base de datos
        Informe informe = informeDAO.obtenerInformePorId(informeId);
        if (informe == null) {
            throw new IOException("No se encontró el informe en la base de datos con ID: " + informeId);
        }

        String rutaPDFOriginal = informe.getRutaDocumento();
        if (rutaPDFOriginal == null || rutaPDFOriginal.isEmpty()) {
            throw new IOException("La ruta del informe está vacía para el ID: " + informeId);
        }

        // Crear nueva ruta para el documento firmado en la carpeta del jefe de unidad
        String nombreArchivo = new File(rutaPDFOriginal).getName(); // Mantener nombre original
        String nuevaRutaPDF = RUTA_DOCS_JEFE + nombreArchivo.replace(".pdf", "_firmado.pdf");

        // Verificar que el archivo original exista
        File archivoOriginal = new File(rutaPDFOriginal);
        if (!archivoOriginal.exists()) {
            throw new IOException("Archivo original no encontrado: " + rutaPDFOriginal);
        }

        // Crear carpeta de destino si no existe
        File carpetaDestino = new File(RUTA_DOCS_JEFE);
        if (!carpetaDestino.exists()) {
            boolean creada = carpetaDestino.mkdirs();
            if (!creada) {
                throw new IOException("No se pudo crear la carpeta de destino: " + RUTA_DOCS_JEFE);
            }
        }

        // Cargar y agregar firma al documento
        PDDocument documento = PDDocument.load(archivoOriginal);
        try {
            var pagina = documento.getPage(0);
            PDImageXObject imagenFirma = PDImageXObject.createFromFile(RUTA_FIRMA, documento);

            PDPageContentStream contenido = new PDPageContentStream(
                documento,
                pagina,
                PDPageContentStream.AppendMode.APPEND,
                true
            );

            float x = 400;
            float y = 100;
            float ancho = 150;
            float alto = 50;

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

        Usuario usuario = new Usuario();
        usuario.setUsuarioId(usuarioId);

        firma.setInforme(informe);
        firma.setUsuario(usuario);
        firma.setRutaImagenFirma(rutaFirma);
        firma.setFecha(LocalDateTime.now());

        boolean guardado = firmaDigitalDAO.guardarFirma(firma);
        if (!guardado) {
            System.err.println("Error guardando la firma digital en BD.");
        }
    }

    private Usuario obtenerUsuarioPorInforme(int informeId) {
        return usuarioDAO.buscarPorNombre("practicante");
    }
}
