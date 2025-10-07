package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Informe;
import model.Usuario;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.InformeDAO;
import dao.RolDAO;


@WebServlet("/jefeUnidad")
public class JefeUnidadServlet extends HttpServlet {
    
	private InformeDAO informeDAO = new InformeDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	System.out.println("Jefe Unidad Servlet");

        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
        	response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
    	System.out.println("Sesion: " + session);
        
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null || !usuario.esJefeUnidad()) {
        	System.out.println("usuario: " + usuario);
        	response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        

        List<Informe> informesPendientes = informeDAO.obtenerInformesPendientes();
        
        

        request.setAttribute("informes", informesPendientes);
        request.getRequestDispatcher("views/jefeUnidad.jsp").forward(request, response);
    }
}

