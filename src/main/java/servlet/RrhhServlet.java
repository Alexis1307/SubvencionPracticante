package servlet;

import java.io.IOException;
import java.time.LocalDate;

import dao.InformeDAO;
import dao.InformeFlujoDAO;
import dao.NotificacionDAO;
import dao.UsuarioDAO;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Informe;
import model.InformeFlujo;
import model.Notificacion;
import model.Usuario;

@WebServlet("/rrhh")
public class RrhhServlet extends HttpServlet {

    private InformeDAO informeDAO = new InformeDAO();
    private InformeFlujoDAO flujoDAO = new InformeFlujoDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO(Persistence.createEntityManagerFactory("sqlserver"));
    private NotificacionDAO notificacionDAO = new NotificacionDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuarioLogueado") : null;

        if (usuario == null || !"rrhh".equalsIgnoreCase(usuario.getNombreUsuario())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
            return;
        }

        int informeId = Integer.parseInt(request.getParameter("informeId"));
        String accion = request.getParameter("accion"); 
        String comentario = request.getParameter("comentario");

        Informe informe = informeDAO.obtenerInformePorId(informeId);
        if (informe == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Informe no encontrado");
            return;
        }

        try {
            if ("aprobar".equalsIgnoreCase(accion)) {
                // Cambiar estado informe a aprobado
                informeDAO.actualizarEstadoInforme(informeId, "Aprobado");

                // Registrar flujo de aprobación
                InformeFlujo flujo = new InformeFlujo();
                flujo.setInforme(informe);
                flujo.setUsuario(usuario);
                flujo.setRolOrigen("rrhh");
                flujo.setRolDestino(null); 
                flujo.setEstado("Aprobado");
                flujo.setComentario(comentario);
                flujo.setFecha(LocalDate.now());
                flujoDAO.registrarFlujo(flujo);

                // Notificar a practicante
                Usuario practicante = informe.getPracticante();
                if (practicante != null) {
                    Notificacion noti = new Notificacion();
                    noti.setUsuario(practicante);
                    noti.setMensaje("Tu informe ha sido aprobado por RRHH.");
                    noti.setFecha(LocalDate.now());
                    notificacionDAO.crearNotificacion(noti);
                }

            } else if ("rechazar".equalsIgnoreCase(accion)) {
                // Cambiar estado informe a rechazado
                informeDAO.actualizarEstadoInforme(informeId, "Rechazado");

                // Registrar flujo de rechazo a jefeUnidad con comentario
                Usuario jefeUnidad = usuarioDAO.buscarPorNombre("jefeunidad");

                InformeFlujo flujo = new InformeFlujo();
                flujo.setInforme(informe);
                flujo.setUsuario(usuario);
                flujo.setRolOrigen("rrhh");
                flujo.setRolDestino("jefeunidad");
                flujo.setEstado("Rechazado");
                flujo.setComentario(comentario);
                flujo.setFecha(LocalDate.now());
                flujoDAO.registrarFlujo(flujo);

                // Notificar a jefeUnidad para revisión
                if (jefeUnidad != null) {
                    Notificacion noti = new Notificacion();
                    noti.setUsuario(jefeUnidad);
                    noti.setMensaje("Informe rechazado por RRHH. Observaciones: " + comentario);
                    noti.setFecha(LocalDate.now());
                    notificacionDAO.crearNotificacion(noti);
                }

            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción inválida");
                return;
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Informe procesado correctamente.");

        } catch (Exception e) {
        	System.out.println("ERROR PE CUASA ARREGLA");
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al procesar el informe");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("views/rrhh.jsp").forward(request, response);
    }
}
