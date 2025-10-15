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
        <div class="notificaciones">
            <button id="btnNotificaciones">
                Notificaciones
                <span class="badge" id="badgeNotificaciones">1</span>
            </button>
            <div class="notificaciones-lista" id="listaNotificaciones">
                <div class="notificacion" onclick="irRevision()">
                    <div class="notificacion-titulo">Nuevo informe recibido</div>
                    <div class="notificacion-contenido">
                        Revisa los informes enviados por los practicantes.
                    </div>
                </div>
            </div>
        </div>
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
                    int index = 1;
                    for (Informe informe : informes) {
                        String nombreArchivo = new java.io.File(informe.getRutaDocumento()).getName();
                        String idEstado = "estado-informe-" + index;
                        String idAprobar = "btn-aprobar-" + index;
                        String idRechazar = "btn-rechazar-" + index;
                %>
                <tr onclick="verDocumento('<%= nombreArchivo %>')" class="fila-informe">
                    <td><%= nombreArchivo %></td>
                    <td><%= informe.getFechaEnvio().toLocalDate() %></td>
                    <td><%= informe.getRol().getNombreRol() %></td>
                    <td id="<%= idEstado %>"><%= informe.getEstado() %></td>
                    <td>
                        <button class="btn-aprobar"
                                onclick="event.stopPropagation(); aprobarInforme(<%= informe.getInformeID() %>, '<%= idEstado %>')"
                                id="<%= idAprobar %>">Aprobar</button>
                        <button class="btn-rechazar"
                                onclick="event.stopPropagation(); rechazarInforme(<%= informe.getInformeID() %>, '<%= idEstado %>')"
                                id="<%= idRechazar %>">Rechazar</button>
                    </td>
                </tr>
                <% index++; } } else { %>
                    <tr>
                        <td colspan="5">No hay informes pendientes.</td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </div>

    <!-- Modales -->
    <div id="modalObservacion" class="modal">
        <div class="modal-contenido modal-observacion">
            <h3>Agregar observación</h3>
            <textarea id="observacionTexto" required></textarea>
            <div id="notificacionEnvio">Observaciones enviadas correctamente.</div>
            <div class="modal-botones">
                <button onclick="enviarObservacion()" class="btn-enviar">Enviar observaciones</button>
                <button onclick="cerrarModal('modalObservacion')" class="btn-cerrar">Cerrar</button>
            </div>
        </div>
    </div>

    <div id="modalDocumento" class="modal">
        <div class="modal-contenido modal-documento">
            <h3>Visualizar Informe de Prácticas</h3>
            <div id="visorDocumento"></div>
            <div id="notificacionRRHH">Informe enviado a RRHH</div>
            <div id="botonesModalDocumento"></div>
        </div>
    </div>

    <div id="modalFirmaRRHH" class="modal">
        <div class="modal-contenido modal-firma">
            <h3>Validar Firma Digital</h3>
            <input id="firmaUsuarioRRHH" type="text" placeholder="Usuario" required />
            <input id="firmaContraRRHH" type="password" placeholder="Contraseña" required />
            <div id="firmaErrorRRHH">Credenciales incorrectas</div>
            <button onclick="validarFirmaRRHH()" class="btn-validar">Validar y Enviar</button>
            <button onclick="cerrarModal('modalFirmaRRHH')" class="btn-cerrar">Cerrar</button>
        </div>
    </div>
</section>

<script src="https://unpkg.com/pdf-lib/dist/pdf-lib.min.js"></script>
<script src="../js/jefeUnidad.js"></script>
</body>
</html>
