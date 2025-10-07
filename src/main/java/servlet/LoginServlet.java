package servlet;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Usuario;

import java.beans.PersistenceDelegate;
import java.io.IOException;

import dao.UsuarioDAO;


@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private EntityManagerFactory emf = Persistence.createEntityManagerFactory("sqlserver");
	private UsuarioDAO usuarioDAO;

	@Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("sqlserver");
        usuarioDAO = new UsuarioDAO(emf);
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String nombreUsuario = request.getParameter("nombreUsuario");
		String contra = request.getParameter("contra");
		
		try {
			Usuario usuario = usuarioDAO.buscarPorNombre(nombreUsuario);
			
			if(usuario != null && usuario.getContra().equals(contra)) {
				request.getSession().setAttribute("usuarioLogueado", usuario);
				switch (usuario.getNombreUsuario()) {
					case "practicante": 
						response.sendRedirect(request.getContextPath() + "/practicante");
						break;
					case "jefeUnidad": 
						response.sendRedirect(request.getContextPath() + "/jefeUnidad");
						break;
					case "especialista":
						response.sendRedirect(request.getContextPath() + "/especialista");
						break;
					case "rrhh":
						response.sendRedirect(request.getContextPath() + "/rrhh");
						break;
					default:
						request.setAttribute("error", "Rol de usuario no reconocido");
	                    request.getRequestDispatcher("login.jsp").forward(request, response);
				}	
				System.out.println("Usuario encontrado: " + usuario);
				System.out.println("Rol: " + (usuario.getRol() != null ? usuario.getRol().getNombreRol() : "null"));

			}else {
				request.setAttribute("error", "Usuario o contraseña incorrectos");
	            request.getRequestDispatcher("login.jsp").forward(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
	        request.setAttribute("error", "Error interno al intentar iniciar sesión");
	        request.getRequestDispatcher("login.jsp").forward(request, response);
		}
	}

}
