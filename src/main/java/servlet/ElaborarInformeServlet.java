package servlet;

import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Informe;
import model.Notificacion;
import model.Rol;
import model.Usuario;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import dao.InformeDAO;
import dao.NotificacionDAO;
import dao.RolDAO;
import dao.UsuarioDAO;


@WebServlet("/elaborarInforme")
public class ElaborarInformeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Ruta base donde se guardarán los PDFs
    private static final String PDF_BASE_PATH = "C:\\SubvencionPracticante\\ProyectoSubvencionPDF\\practicante";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Elaborar Informe");
    	// Obtener el usuario de la sesión
        HttpSession session = request.getSession();
        Usuario practicante = (Usuario) session.getAttribute("usuarioLogueado");

        if (practicante == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Leer datos del formulario
        String asunto = request.getParameter("asunto");
        int periodo = Integer.parseInt(request.getParameter("periodo"));
        String actividades = request.getParameter("actividades");

        // Obtener fecha actual
        LocalDate fechaEnvio = LocalDate.now();

        // Obtener nombre del área
        RolDAO rolDAO = new RolDAO();
        String nombreArea = rolDAO.obtenerNombreRol(practicante.getRolId());

        // Crear nombre de archivo
        String nombreArchivo = "Informe_" + practicante.getNombreUsuario() + "_" + System.currentTimeMillis() + ".pdf";
        String rutaCompleta = PDF_BASE_PATH + File.separator + nombreArchivo;


        try {
            Document documento = new Document();
            File carpeta = new File(PDF_BASE_PATH);
            if (!carpeta.exists()) carpeta.mkdirs(); // Crear carpeta si no existe

            PdfWriter.getInstance(documento, new java.io.FileOutputStream(rutaCompleta));
            documento.open();

            // Estructura del informe
            documento.add(new Paragraph("INFORME PRACTICANTE\n\n"));
            documento.add(new Paragraph("\n--------------------------------------------------------------------------------------------------------------------"));
            documento.add(new Paragraph("\nNombre del practicante: " + practicante.getNombreUsuario()));
            documento.add(new Paragraph("Destinatario: Jefe Unidad - Área de " + nombreArea));
            documento.add(new Paragraph("Asunto: " + asunto));
            documento.add(new Paragraph("Área de trabajo: " + nombreArea));
            documento.add(new Paragraph("Fecha de envío: " + fechaEnvio));
            documento.add(new Paragraph("Periodo de prácticas: " + periodo + " meses"));
            documento.add(new Paragraph("\n\n--------------------------------------------------------------------------------------------------------------------"));
            documento.add(new Paragraph("\n\nActividades: \n" + actividades));
            documento.close();
        } catch (DocumentException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al generar el PDF.");
            return;
        }
        
        Rol rol = new Rol();

        Informe informe = new Informe();
        informe.setPracticante(practicante);
        informe.setAsunto(asunto);
        rol.setRolId(practicante.getRolId());
        informe.setRol(rol);
        informe.setPeriodoPracticas(periodo);
        informe.setActividades(actividades);
        informe.setRutaDocumento(rutaCompleta); 
        informe.setNombreDocumento(nombreArchivo);
        informe.setFechaEnvio(fechaEnvio);
        informe.setEstado("Pendiente");

        InformeDAO informeDAO = new InformeDAO();
        informeDAO.guardarInforme(informe);
        
        UsuarioDAO usuarioDAO = new UsuarioDAO(Persistence.createEntityManagerFactory("sqlserver"));
        Usuario jefeUnidad = usuarioDAO.obtenerJefeUnidad();

        if (jefeUnidad != null) {
            Notificacion notificacion = new Notificacion();
            notificacion.setUsuario(jefeUnidad);
            notificacion.setMensaje("Nuevo informe recibido del área de " + practicante.getRol().getNombreRol() + " - Está pendiente a revisión");
            notificacion.setFecha(LocalDate.now());
            notificacion.setVisto(false);

            NotificacionDAO notificacionDAO = new NotificacionDAO();
            notificacionDAO.crearNotificacion(notificacion);
        }
        
        request.setAttribute("mensajeExito", "Informe enviado correctamente.");
        request.setAttribute("mensajeError", "Hubo un error al enviar el informe. Inténtalo nuevamente.");
        request.getRequestDispatcher("views/elaborar_informe.jsp").forward(request, response);
    }
    
}
