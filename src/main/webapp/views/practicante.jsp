<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.io.File" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.*, model.Informe, model.Usuario" %>
<%!
    public String getEstadoClass(String estado) {
        if ("Aprobado".equalsIgnoreCase(estado)) {
            return "estado-aprobado";
        } else if ("En revisión".equalsIgnoreCase(estado) || "Pendiente".equalsIgnoreCase(estado)) {
            return "estado-revision";
        } else if ("Rechazado".equalsIgnoreCase(estado)) {
            return "estado-rechazado";
        }
        return "";
    }
%>
<%
    HttpSession sesion = request.getSession(false);
    Usuario usuario = sesion != null ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;
    if (usuario == null) {
        response.sendRedirect("../login.jsp");
        return;
    }

    List<Informe> informes = (List<Informe>) request.getAttribute("informes");
    Map<Integer, String> nombresRoles = (Map<Integer, String>) request.getAttribute("nombresRoles");

%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Practicante</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <h1>Bienvenido, <%= usuario.getNombreUsuario() %></h1>

        <% if (informes == null || informes.isEmpty()) { %>
            <p>No hay informes registrados.</p>
        <% } else { %>
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>Archivo</th>
                        <th>Fecha</th>
                        <th>Área</th>
                        <th>Estado</th>
                        <th>Acción</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Informe informe : informes) { %>
                        <tr onclick="verDocumento('<%= new File(informe.getRutaDocumento()).getName() %>')">
                            <td><%= new File(informe.getRutaDocumento()).getName() %></td>
                            <td><%= informe.getFechaEnvio().toLocalDate() %></td>
							<td><%= nombresRoles.get(informe.getInformeID()) %></td>
                            <td class="<%= getEstadoClass(informe.getEstado()) %>"><%= informe.getEstado() %></td>
                            <td>
                                <button class="btn btn-info btn-sm" onclick="event.stopPropagation(); mostrarModal('modalSinContenido')">
                                    Ver observaciones
                                </button>
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <% } %>
    </div>
</body>
</html>
