package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import dao.InformeDAO;
import dao.NotificacionDAO;
import model.Informe;
import model.Notificacion;
import model.Usuario;

import java.io.IOException;
import java.util.List;

@WebServlet("/especialista")
public class EspecialistaServlet extends HttpServlet {
    private InformeDAO informeDAO = new InformeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null || !usuario.esEspecialista()) {
            System.out.println("usuario: " + usuario);
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        List<Informe> informes = informeDAO.obtenerTodosLosInformes();
        
        List<Notificacion> notificaciones = NotificacionDAO.obtenerPorUsuario(usuario.getUsuarioId());
        request.setAttribute("notificaciones", notificaciones);
        
        request.setAttribute("informes", informes);

        request.getRequestDispatcher("views/especialista.jsp").forward(request, response);
    }
}
