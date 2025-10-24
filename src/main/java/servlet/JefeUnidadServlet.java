package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Informe;
import model.Notificacion;
import model.Usuario;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.InformeDAO;
import dao.NotificacionDAO;
import dao.RolDAO;


@WebServlet("/jefeUnidad")
public class JefeUnidadServlet extends HttpServlet {

    private InformeDAO informeDAO = new InformeDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("Jefe de unidad Ejecutado");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null || !usuario.esJefeUnidad()) {
            System.out.println("usuario: " + usuario);
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        List<Informe> informes = informeDAO.obtenerTodosLosInformes();
        Collections.reverse(informes);

        for (Informe inf : informes) {
            System.out.println("Informe ID " + inf.getInformeID() + " - Estado: " + inf.getEstado());
        }
        
        List<Notificacion> notificaciones = NotificacionDAO.obtenerPorUsuario(usuario.getUsuarioId());
        request.setAttribute("notificaciones", notificaciones);

        request.setAttribute("informes", informes);
        request.getRequestDispatcher("views/jefeUnidad.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}


