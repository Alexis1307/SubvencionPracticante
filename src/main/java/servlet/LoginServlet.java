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
    
    private EntityManagerFactory emf;
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("sqlserver");
        usuarioDAO = new UsuarioDAO(emf);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nombreUsuario = request.getParameter("nombreUsuario");
        String contra = request.getParameter("contra");

        try {
            Usuario usuario = usuarioDAO.buscarPorNombre(nombreUsuario);

            if (usuario != null && usuario.getContra().equals(contra)) {
                // Guardar el usuario en sesión
                request.getSession().setAttribute("usuarioLogueado", usuario);
                request.getSession().setAttribute("nombreUsuario", usuario.getNombreUsuario());

                // Redirección según tipo de usuario
                String destino;
                switch (usuario.getNombreUsuario()) {
                    case "practicante":
                        destino = "/practicante";
                        break;
                    case "jefeUnidad":
                        destino = "/jefeUnidad";
                        break;
                    case "especialista":
                        destino = "/especialista";
                        break;
                    case "rrhh":
                        destino = "/rrhh";
                        break;
                    default:
                        // ✅ Cualquier otro usuario existente -> redirige a /practicante
                        destino = "/practicante";
                        break;
                }

                // Redirige al destino correspondiente
                response.sendRedirect(request.getContextPath() + destino);

                System.out.println("Usuario encontrado: " + usuario.getNombreUsuario());
                System.out.println("Rol: " + 
                    (usuario.getRol() != null ? usuario.getRol().getNombreRol() : "sin rol"));
            } else {
                // Usuario no encontrado o contraseña incorrecta
                request.setAttribute("error", "Usuario o contraseña incorrectos");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error interno al intentar iniciar sesión");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    public void destroy() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}

