<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Panel Especialista de Remuneraciones – Gestión de Informes</title>
</head>
<body data-context-path="${pageContext.request.contextPath}">

<nav class="navbar">
    <div class="navbar-left">
        <a href="#inicio">Inicio</a>
        <div class="dropdown">
		  <button class="btn btn-light dropdown-toggle" type="button" data-bs-toggle="dropdown">
		    🔔 Notificaciones
		  </button>
		  <ul class="dropdown-menu dropdown-menu-end">
		    <c:forEach var="noti" items="${notificaciones}">
		      <li class="dropdown-item">
		        <strong>${noti.mensaje}</strong><br />
		        <small class="text-muted">${noti.fecha}</small>
		      </li>
		    </c:forEach>
		    <c:if test="${empty notificaciones}">
		      <li class="dropdown-item text-muted">No hay notificaciones</li>
		    </c:if>
		  </ul>
		</div>

    </div>
	
	<form action="${pageContext.request.contextPath}/logout" method="post" class="d-inline">
     	<button type="submit" class="btn btn-light btn-sm" id="logoutBtn">
            <i class="fas fa-sign-out-alt me-2"></i>Cerrar Sesión
        </button>
    </form></nav>

<div class="contenedor">
    <h2>Panel de Especialista de Remuneraciones</h2>
	
	<c:if test="${not empty sessionScope.mensaje}">
    	<div class="alert ${sessionScope.tipoMensaje}">${sessionScope.mensaje}</div>
	    <c:remove var="mensaje" scope="session" />
	    <c:remove var="tipoMensaje" scope="session" />
	</c:if>


	
	<form id="formGenerarPlanilla" action="${pageContext.request.contextPath}/generarPlanilla" method="post" onsubmit="return prepararEnvio();">
	    <input type="hidden" name="ids" id="idsSeleccionados">
	    <button type="submit">Generar Planilla</button>
	</form>

     
    <!-- Tabla dinámica -->
    <section id="tabla_revision">
        <table>
            <thead>
            <tr>
                <th><input type="checkbox" id="selectAll" onchange="toggleAll(this)"></th>
                <th>ID</th>
                <th>Nombre</th>
                <th>Fecha</th>
                <th>Acciones</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="informe" items="${informes}">
                <tr>
                    <td>
                        <input type="checkbox" class="informe-check" data-id="${informe.informeID}"
                               value="${informe.informeID}" onclick="event.stopPropagation();">
                    </td>
                    <td>${informe.informeID}</td>
                    <td>${informe.nombreDocumento}</td>
                    <td>${informe.fechaEnvio}</td>
                    <td>
                        <button class="btn btn-outline-primary"
							 onclick="verDocumento(${informe.informeID})">
							<i class="fas fa-eye"></i> Ver Informe
						</button>

                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </section>
    
    <!-- Modal para Ver Informe PDF -->
	<div class="modal fade" id="modalVerInforme" tabindex="-1" aria-labelledby="modalVerInformeLabel" aria-hidden="true">
	  <div class="modal-dialog modal-xl modal-dialog-centered">
	    <div class="modal-content">
	      <div class="modal-header">
	        <h5 class="modal-title" id="modalVerInformeLabel">Visualizar Informe</h5>
	        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar">Cerrrar X</button>
	      </div>
	      <div class="modal-body">
	        <iframe id="iframeInforme" src="" width="100%" height="600px" style="border:none;"></iframe>
	      </div>
	    </div>
	  </div>
	</div>
    

    <div id="mensaje"></div>
</div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/especialista.js"></script>
    <script src="${pageContext.request.contextPath}/js/documento.js"></script>

</body>
</html>
