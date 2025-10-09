package servlet;

import dao.InformeFlujoDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.*;

import java.io.IOException;

@WebServlet("/procesarInforme")
public class ProcesarInformeServlet extends HttpServlet {

    private InformeFlujoDAO flujoDAO = new InformeFlujoDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
        	response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Obtener parámetros
        int informeId = Integer.parseInt(request.getParameter("informeId"));
        String accion = request.getParameter("accion"); // aprobar o rechazar
        String comentario = request.getParameter("comentario"); // solo si rechaza

        // Aquí vendría lógica para obtener el informe y los roles
        // Simulación:
        Informe informe = new Informe(); 
        informe.setInformeID(informeId);
        Rol rolOrigen = usuarioLogueado.getRol();
        Rol rolDestino = determinarDestino(accion, rolOrigen); // lógica que tú defines

        InformeFlujo flujo = new InformeFlujo();
        flujo.setInforme(informe);
        flujo.setUsuario(usuarioLogueado);
        flujo.setRolOrigen(rolOrigen);
        flujo.setRolDestino(rolDestino);
        flujo.setEstado(accion.equals("aprobar") ? "Aprobado" : "Rechazado");
        flujo.setComentario(comentario);
        
        flujoDAO.registrarFlujo(flujo);

        // Redireccionar o devolver respuesta
        response.sendRedirect("jefeUnidad.jsp");
    }

    private Rol determinarDestino(String accion, Rol rolOrigen) {
        // Aquí decides a quién se envía si se aprueba
        // Por ejemplo, si rolOrigen es JefeUnidad y aprueba, entonces destino es RRHH
        if (accion.equals("aprobar") && rolOrigen.getNombreRol().equals("JefeUnidad")) {
            Rol rrhh = new Rol();
            rrhh.setRolId(3); // ID de RRHH
            return rrhh;
        }
        return null; // Si es rechazo o flujo final
    }
}
