<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="model.Usuario" %>
<%@ page import="model.Informe" %>

<%
    HttpSession sesion = request.getSession(false);
    Usuario usuario = (Usuario) sesion.getAttribute("usuarioLogueado");

    if (usuario == null || !usuario.esRrhh()) {
        response.sendRedirect("${pageContext.request.contextPath}/login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jefe de RRHH</title>
  <style type="text/css">
  	.modal {
  display: none; 
  position: fixed;
  z-index: 999;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: rgba(0,0,0,0.4);
}

.modal-contenido {
  background-color: #fff;
  margin: 10% auto;
  padding: 20px;
  border-radius: 5px;
  width: 50%;
}
  	
  </style>
</head>
<body data-context-path="${pageContext.request.contextPath}">

<nav class="navbar">
  <div class="navbar-links">
    <a href="#">Inicio</a>

    <div class="notificaciones">
      <button id="btnNotificaciones">
        Notificaciones
        <span class="badge" id="badgeNotificaciones">
          <c:out value="${fn:length(notificaciones)}" />
        </span>
      </button>
      <div class="notificaciones-lista" id="listaNotificaciones">
        <c:forEach var="noti" items="${notificaciones}">
          <div class="notificacion">
            <div class="notificacion-titulo">${noti.mensaje}</div>
            <div class="notificacion-contenido">${noti.fecha}</div>
          </div>
        </c:forEach>
      </div>
    </div>
  </div>

  <form action="${pageContext.request.contextPath}/logout" method="post" class="d-inline">
    <button type="submit" class="btn btn-light btn-sm" id="logoutBtn">
      <i class="fas fa-sign-out-alt me-2"></i>Cerrar Sesión
    </button>
  </form>
</nav>

<section id="inicio">
	<h1>Bienvenido, <%= usuario.getNombreUsuario() %></h1>
	
	<%
	    String mensaje = (String) session.getAttribute("mensaje");
	    String tipoMensaje = (String) session.getAttribute("tipoMensaje");
	    session.removeAttribute("mensaje");
	    session.removeAttribute("tipoMensaje");
	%>
	
  <div id="tabla_informes">
    <table>
      <thead>
        <tr>
          <th>Asunto</th>
          <th>Fecha</th>
          <th>Practicante</th>
          <th>Estado</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="informe" items="${informes}">
        	<c:set var="deshabilitar" value="${informe.estado ne 'Pendiente'}" />
          <tr>
            <td>${informe.asunto}</td>
			<td>${informe.fechaEnvio}</td>
            <td>${informe.practicante.nombreUsuario}</td>
            <td><span class="badge">${informe.estado}</span></td>
            <td>
              <button class="btn btn-outline-primary"
					onclick="verDocumento(${informe.informeID})">
				<i class="fas fa-eye"></i> Ver Informe
				</button>

              <!-- Aprobar -->
              	<form action="${pageContext.request.contextPath}/procesarInforme" method="post" style="display:inline;">
	                <input type="hidden" name="informeId" value="${informe.informeID}" />
	                <input type="hidden" name="accion" value="aprobar" />
					<!-- Botón Aprobar -->
					<button type="button" class="btn-aprobar"
					  onclick="mostrarModalFirma(${informe.informeID})"
					  <c:if test="${informe.estado ne 'En revision'}">disabled</c:if>>
					  Aprobar
					</button>              
				</form>

              	<!-- Rechazar -->
              	<form action="${pageContext.request.contextPath}/procesarInforme" method="post" style="display:inline;">
	                <input type="hidden" name="informeId" value="${informe.informeID}" />
	                <input type="hidden" name="accion" value="rechazar" />
	                <input type="hidden" name="comentario" value="Informe rechazado por RRHH." />
					<!-- Botón Rechazar -->
					<button type="button" class="btn-rechazar"
					  onclick="mostrarModalRechazo(${informe.informeID})"
					  <c:if test="${informe.estado ne 'En revision'}">disabled</c:if>>
					  Rechazar
					</button>              
				</form>
            </td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
  </div>

  <div id="notificacionRRHH" class="notificacion-general"></div>
</section>
	<!-- Modal para Ver Informe PDF -->
	<div class="modal fade" id="modalVerInforme" tabindex="-1" aria-labelledby="modalVerInformeLabel" aria-hidden="true">
	  <div class="modal-dialog modal-xl modal-dialog-centered">
	    <div class="modal-content">
	      <div class="modal-header">
	        <h5 class="modal-title" id="modalVerInformeLabel">Visualizar Informe</h5>
	        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar">Cerrar X</button>
	      </div>
	      <div class="modal-body">
	        <iframe id="iframeInforme" src="" width="100%" height="600px" style="border:none;"></iframe>
	      </div>
	    </div>
	  </div>
	</div>

	
	<!-- Modal Aprobar -->
<div id="modalFirma" class="modal">
  <div class="modal-contenido">
    <button class="btn-cerrar" onclick="cerrarModal('modalFirma')">Cerrar ✖</button>
    <h3>Aprobar Informe</h3>
    <form action="${pageContext.request.contextPath}/procesarInforme" method="post">
      <input type="hidden" name="informeId" id="aprobar_informeId" />
      <input type="hidden" name="accion" value="aprobar" />
      <button type="submit">Confirmar Aprobación</button>
    </form>
  </div>
</div>
	
	<!-- Modal Rechazar -->
<div id="modalRechazo" class="modal">
  <div class="modal-contenido">
    <button class="btn-cerrar" onclick="cerrarModal('modalRechazo')">Cerrar ✖</button>
    <h3>Rechazar Informe</h3>
    <form action="${pageContext.request.contextPath}/procesarInforme" method="post">
      <input type="hidden" name="informeId" id="rechazo_informeId" />
      <input type="hidden" name="accion" value="rechazar" />
      <label for="comentario">Comentario:</label>
      <textarea name="comentario" rows="4" required></textarea>
      <button type="submit">Confirmar Rechazo</button>
    </form>
  </div>
</div>
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/rrhh.js"></script>
    <script src="${pageContext.request.contextPath}/js/documento.js"></script>

</body>
</html>
