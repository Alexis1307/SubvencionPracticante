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


@WebServlet("/practicante")
public class PracticanteServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            response.sendRedirect("../login.jsp");
            return;
        }

        InformeDAO informeDAO = new InformeDAO();
        RolDAO rolDAO = new RolDAO();
        
        List<Informe> listaInformes = informeDAO.obtenerInformes(usuario.getUsuarioId());
        
        Map<Integer, String> nombresRoles = new HashMap<>();
        for (Informe informe : listaInformes) {
            String nombreRol = informe.getRol().getNombreRol();
            nombresRoles.put(informe.getInformeID(), nombreRol); 
        }
        
        System.out.println("ID del usuario logueado: " + usuario.getUsuarioId());
        System.out.println("Cantidad de informes encontrados: " + listaInformes.size());
        
        int aprobados = 0;
        int pendientes = 0;
        int rechazados = 0;

        for (Informe informe : listaInformes) {
            switch (informe.getEstado()) {
                case "Pendiente" -> pendientes++;
                case "En revisión" -> pendientes++;
                case "Aprobado" -> aprobados++;
                case "Rechazado" -> rechazados++;
            }
        }
        
        Collections.reverse(listaInformes);

        List<Notificacion> notificaciones = NotificacionDAO.obtenerPorUsuario(usuario.getUsuarioId());
        request.setAttribute("notificaciones", notificaciones);
        
        request.setAttribute("pendientes", pendientes);
        request.setAttribute("aprobados", aprobados);
        request.setAttribute("rechazados", rechazados);


        request.setAttribute("informes", listaInformes);
        request.setAttribute("nombresRoles", nombresRoles); 
        request.getRequestDispatcher("views/practicante.jsp").forward(request, response);
        
        

    }
}
