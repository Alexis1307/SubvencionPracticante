package servlet;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Rol;
import model.Usuario;

import java.io.IOException;
import java.util.List;

import dao.RolDAO;
import dao.UsuarioDAO;

@WebServlet("/nuevoUsuario")
public class NuevoUsuarioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private EntityManagerFactory emf;
    private UsuarioDAO usuarioDAO;
    private RolDAO rolDAO;

    @Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("sqlserver");
        usuarioDAO = new UsuarioDAO(emf);
        rolDAO = new RolDAO(emf);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	System.out.println("Entramos a nuevo usuario Sevlet metodo get");
        List<Rol> roles = rolDAO.obtenerTodos();
        request.setAttribute("roles", roles);

        request.getRequestDispatcher("views/nuevoUsuario.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nombreUsuario = request.getParameter("nombreUsuario");
        String contra = request.getParameter("contra");
        String rolIdParam = request.getParameter("rolId");

        try {
            if (nombreUsuario == null || contra == null || rolIdParam == null ||
                nombreUsuario.isEmpty() || contra.isEmpty() || rolIdParam.isEmpty()) {

                request.setAttribute("error", "Todos los campos son obligatorios.");
                doGet(request, response);
                return;
            }

            int rolId = Integer.parseInt(rolIdParam);

            Rol rol = rolDAO.obtenerPorId(rolId);

            if (rol == null) {
                request.setAttribute("error", "El rol seleccionado no existe.");
                doGet(request, response);
                return;
            }

            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreUsuario(nombreUsuario);
            nuevoUsuario.setContra(contra); 
            nuevoUsuario.setRol(rol);

            usuarioDAO.guardar(nuevoUsuario);

            request.setAttribute("mensaje", "Usuario registrado correctamente. Ahora puede iniciar sesión.");
            request.getRequestDispatcher("login.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al registrar el usuario.");
            doGet(request, response);
        }
    }

    @Override
    public void destroy() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
