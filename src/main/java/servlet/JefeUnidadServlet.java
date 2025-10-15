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
        

        List<Informe> informesPendientes = informeDAO.obtenerInformesPendientes();
        
        System.out.println("Total informes: " + informesPendientes.size());
        for (Informe i : informesPendientes) {
            System.out.println("Informe ID: " + i.getInformeID() + ", Usuario: " + i.getPracticante());
        }

        request.setAttribute("informes", informesPendientes);
        request.getRequestDispatcher("views/jefeUnidad.jsp").forward(request, response);
    }
}

