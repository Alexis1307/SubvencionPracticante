<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.Informe" %>
<%@ page import="model.Usuario" %>
<%
    HttpSession sesion = request.getSession(false);
    Usuario usuario = (Usuario) sesion.getAttribute("usuarioLogueado");

    if (usuario == null || !usuario.esJefeUnidad()) {
        response.sendRedirect("../login.jsp");
        return;
    }

    List<Informe> informes = (List<Informe>) request.getAttribute("informes");
    String contextPath = request.getContextPath();

    // Mensajes recibidos de la acción procesarInforme
    String mensaje = (String) request.getAttribute("mensaje");
    String tipoMensaje = (String) request.getAttribute("tipoMensaje"); // success, error, warning
    Integer informeIdProcesado = (Integer) request.getAttribute("informeIdProcesado");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8" />
    <title>Panel Jefe de Unidad</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/jefeUnidad.css" />
</head>
<body>

<nav class="navbar">
    <div class="navbar-left">
        <a href="#inicio">Inicio</a>
    </div>
	<form action="${pageContext.request.contextPath}/logout" method="post" class="d-inline">
     	<button type="submit" class="btn btn-light btn-sm" id="logoutBtn">
            <i class="fas fa-sign-out-alt me-2"></i>Cerrar Sesión
        </button>
    </form></nav>

<section id="inicio" class="container mt-4">
    <h1 class="fw-bold">Bienvenido, <%= usuario.getNombreUsuario() %></h1>
    <h2 class="fw-bold">Informes en revisión</h2>

    <%-- Mostrar mensaje si existe --%>
    <%
        if (mensaje != null && !mensaje.trim().isEmpty()) {
            String claseMensaje = "notification-alert bg-info"; // Default info azul
            if ("success".equalsIgnoreCase(tipoMensaje)) {
                claseMensaje = "notification-alert bg-success";
            } else if ("error".equalsIgnoreCase(tipoMensaje)) {
                claseMensaje = "notification-alert bg-danger";
            } else if ("warning".equalsIgnoreCase(tipoMensaje)) {
                claseMensaje = "notification-alert bg-warning";
            }
    %>
        <div class="<%= claseMensaje %>" role="alert" style="padding: 15px; border-radius: 6px; color: white; margin-bottom: 20px;">
            <%= mensaje %>
        </div>
    <% } %>
    
	   <% if (mensaje != null && !mensaje.trim().isEmpty()) { 
		    String claseMensaje = "alert-info";
		    if ("success".equalsIgnoreCase(tipoMensaje)) {
		        claseMensaje = "alert-success";
		    } else if ("error".equalsIgnoreCase(tipoMensaje)) {
		        claseMensaje = "alert-danger";
		    } else if ("warning".equalsIgnoreCase(tipoMensaje)) {
		        claseMensaje = "alert-warning";
		    }
		%>
   	 <div class="alert <%= claseMensaje %>"><%= mensaje %></div>
	<% } %>

    <table class="table">
        <thead>
            <tr>
                <th>Archivo</th>
                <th>Fecha</th>
                <th>Área</th>
                <th>Estado</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
            <%
                if (informes != null && !informes.isEmpty()) {
                    for (Informe informe : informes) {
                        String nombreArchivo = new java.io.File(informe.getRutaDocumento()).getName();
                        String estado = informe.getEstado();
                        String badgeClass = "badge bg-secondary";

                        if ("Pendiente".equalsIgnoreCase(estado)) {
                            badgeClass = "badge bg-warning";
                        } else if ("Aprobado".equalsIgnoreCase(estado)) {
                            badgeClass = "badge bg-success";
                        } else if ("Rechazado".equalsIgnoreCase(estado)) {
                            badgeClass = "badge bg-danger";
                        }

						boolean deshabilitarBotones = !"Pendiente".equalsIgnoreCase(informe.getEstado());
            %>
            <tr>
                <td>
                    <span class="archivo-link text-primary" style="cursor:pointer;"
                          onclick="verDocumento('<%= contextPath %>', '<%= nombreArchivo %>', 'practicante')">
                        <%= nombreArchivo %>
                    </span>
                </td>
                <td><%= informe.getFechaEnvio() %></td>
                <td><%= informe.getRol().getNombreRol() %></td>
                <td><span class="<%= badgeClass %>"><%= estado %></span></td>
                <td>
                    <div class="btn-group" role="group" aria-label="Acciones">
                        <button type="button" class="btn btn-outline-primary btn-action"
                            onclick="verDocumentoURL('<%= contextPath %>/verDocumento?nombre=<%= nombreArchivo %>')">
                            Ver Informe
                        </button>
                        <button type="button" class="btn btn-outline-primary btn-action"
                            onclick="mostrarModalFirma(<%= informe.getInformeID() %>)"
                            <%= deshabilitarBotones ? "disabled" : "" %>>
                            Aprobar
                        </button>
                        <button type="button" class="btn btn-outline-primary btn-action"
                            onclick="mostrarModalRechazo(<%= informe.getInformeID() %>)"
                            <%= deshabilitarBotones ? "disabled" : "" %>>
                            Rechazar
                        </button>
                    </div>
                </td>
            </tr>
            <%  }
                } else { %>
            <tr>
                <td colspan="5">No hay informes pendientes.</td>
            </tr>
            <% } %>
        </tbody>
    </table>
</section>

<!-- Modal para mostrar PDF -->
<div id="modalDocumento" class="modal">
    <div class="modal-contenido" style="height: 80vh; position: relative;">
        <button class="btn-cerrar" style="position: absolute; top: 10px; right: 10px;"
                onclick="cerrarModal('modalDocumento')">Cerrar ✖</button>
        <iframe id="visorDocumento" src="" width="100%" height="100%" frameborder="0"></iframe>
    </div>
</div>

<!-- Modal Aprobar -->
<div id="modalFirma" class="modal">
    <div class="modal-contenido">
        <button class="btn-cerrar" style="float:right;" onclick="cerrarModal('modalFirma')">Cerrar ✖</button>
        <h3>Aprobar Informe</h3>
        <form action="<%= contextPath %>/procesarInforme" method="post">
            <input type="hidden" name="informeId" id="firma_informeId" />
            <input type="hidden" name="accion" value="aprobar" />

            <label for="usuario">Usuario:</label>
            <input type="text" id="usuario" name="usuario" required />

            <label for="contrasena">Contraseña:</label>
            <input type="password" id="contrasena" name="contrasena" required />

            <br /><br />
            <button type="submit" class="btn btn-outline-primary">Confirmar Firma</button>
            <button type="button" class="btn btn-outline-primary" onclick="cerrarModal('modalFirma')">Cancelar</button>
        </form>
    </div>
</div>

<!-- Modal Rechazar -->
<div id="modalRechazo" class="modal">
    <div class="modal-contenido">
        <button class="btn-cerrar" style="float:right;" onclick="cerrarModal('modalRechazo')">Cerrar ✖</button>
        <h3>Rechazar Informe</h3>
        <form method="post" action="<%= contextPath %>/procesarInforme">
            <input type="hidden" name="informeId" id="rechazo_informeId" />
            <input type="hidden" name="accion" value="rechazar" />

            <label for="comentario">Comentario:</label><br />
            <textarea id="comentario" name="comentario" rows="4" cols="50" required></textarea><br />

            <button type="submit" class="btn btn-outline-primary">Enviar Rechazo</button>
            <button type="button" class="btn btn-outline-primary" onclick="cerrarModal('modalRechazo')">Cancelar</button>
        </form>
    </div>
</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jefeUnidad.js"></script>

</body>
</html>
