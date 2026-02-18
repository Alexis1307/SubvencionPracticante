package servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

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

    private final InformeDAO informeDAO = new InformeDAO();
    private final NotificacionDAO notificacionDAO = new NotificacionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (!usuario.esRrhh()) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        List<String> estadosDeseados = List.of("En revision", "Aprobado", "Rechazado");
        List<Informe> informes = informeDAO.obtenerInformesPorEstados(estadosDeseados);
        List<Notificacion> notificaciones = notificacionDAO.obtenerPorUsuario(usuario.getUsuarioId());

        System.out.println("Cantidad de informes traídos: " + informes.size());
        for (Informe i : informes) {
            System.out.println(i.getInformeID() + " - " + i.getEstado());
        }

        Collections.reverse(informes);

        request.setAttribute("informes", informes);
        request.setAttribute("notificaciones", notificaciones);
        request.getRequestDispatcher("views/rrhh.jsp").forward(request, response);
        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
