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
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Jefe de Unidad</title>
    <link rel="stylesheet" href="../css/jefeUnidad.css" />
</head>
<body>
<nav class="navbar">
    <div class="navbar-left">
        <a href="#inicio">Inicio</a>
    </div>
    <button onclick="cerrarSesion()">Cerrar sesión</button>
</nav>

<section id="inicio">
    <h1>Bienvenido, <%= usuario.getNombreUsuario() %></h1>
    <h2>Informes en revisión</h2>

    <div id="tabla_revision">
        <table>
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
                <% if (informes != null && !informes.isEmpty()) {
                    for (Informe informe : informes) {
                        String nombreArchivo = new java.io.File(informe.getRutaDocumento()).getName();
                %>
                <tr>
                    <td onclick="verDocumento('<%= nombreArchivo %>')" style="cursor:pointer;"><%= nombreArchivo %></td>
                    <td><%= informe.getFechaEnvio().toLocalDate() %></td>
                    <td><%= informe.getRol().getNombreRol() %></td>
                    <td><%= informe.getEstado() %></td>
                    <td>
                        <button onclick="mostrarModalFirma(<%= informe.getInformeID() %>)">Aprobar</button>
                        <button onclick="mostrarModalRechazo(<%= informe.getInformeID() %>)">Rechazar</button>
                    </td>
                </tr>
                <% } } else { %>
                    <tr>
                        <td colspan="5">No hay informes pendientes.</td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </div>
</section>

<!-- Modal de firma -->
<div id="modalFirma" style="display:none;">
    <form action="${pageContext.request.contextPath}/procesarInforme" method="post">
        <input type="hidden" name="informeId" id="firma_informeId" />
        <input type="hidden" name="accion" value="aprobar" />

        <label>Usuario:</label>
        <input type="text" name="usuario" required />

        <label>Contraseña:</label>
        <input type="password" name="contrasena" required />

        <label>Comentario:</label>
        <textarea name="comentario"></textarea>

        <button type="submit">Confirmar Firma</button>
        <button type="button" onclick="cerrarModal('modalFirma')">Cancelar</button>
    </form>
</div>

<!-- Modal de rechazo -->
<div id="modalRechazo" style="display:none;">
    <form method="post" action="<%= request.getContextPath() %>/procesarInforme">
        <input type="hidden" name="informeId" id="rechazo_informeId">
        <input type="hidden" name="accion" value="rechazar">
        <label>Comentario:</label><br>
        <textarea name="comentario" required></textarea><br>
        <button type="submit">Enviar Rechazo</button>
        <button type="button" onclick="cerrarModal('modalRechazo')">Cancelar</button>
    </form>
</div>

<script>
    function mostrarModalFirma(informeId) {
        document.getElementById('firma_informeId').value = informeId;
        document.getElementById('modalFirma').style.display = 'block';
    }

    function mostrarModalRechazo(informeId) {
        document.getElementById('rechazo_informeId').value = informeId;
        document.getElementById('modalRechazo').style.display = 'block';
    }

    function cerrarModal(id) {
        document.getElementById(id).style.display = 'none';
    }

    function cerrarSesion() {
        window.location.href = "<%= request.getContextPath() %>/logout";
    }
</script>
</body>
</html>
