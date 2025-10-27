<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="model.Informe" %>
<%@ page import="model.Usuario" %>

<c:choose>
    <c:when test="${sessionScope.usuarioLogueado == null or not sessionScope.usuarioLogueado.esJefeUnidad()}">
        <c:redirect url="../login.jsp" />
    </c:when>
</c:choose>

<c:set var="usuario" value="${sessionScope.usuarioLogueado}" />
<c:set var="informes" value="${requestScope.informes}" />
<c:set var="mensaje" value="${requestScope.mensaje}" />
<c:set var="tipoMensaje" value="${requestScope.tipoMensaje}" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8" />
    <title>Panel Jefe de Unidad</title>
    <link rel="stylesheet" href="${contextPath}/css/jefeUnidad.css" />
</head>
<body data-context-path="${contextPath}">

<nav class="navbar">
    <div class="navbar-left">
        <a href="#inicio">Inicio</a>
    </div>

    <div class="navbar-right d-flex align-items-center">
        <div class="dropdown ms-auto">
            <button class="btn btn-light dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                🔔 Notificaciones
            </button>
            <ul class="dropdown-menu dropdown-menu-end">
                <c:forEach var="noti" items="${requestScope.notificaciones}">
                    <li class="dropdown-item">
                        <strong>${noti.mensaje}</strong><br/>
                        <small class="text-muted">${noti.fecha}</small>
                    </li>
                </c:forEach>
                <c:if test="${empty requestScope.notificaciones}">
                    <li class="dropdown-item text-muted">No hay notificaciones</li>
                </c:if>
            </ul>
        </div>

        <form action="${pageContext.request.contextPath}/logout" method="post" class="d-inline">
        	<button type="submit" class="btn btn-light btn-sm" id="logoutBtn">
            	<i class="fas fa-sign-out-alt me-2"></i>Cerrar Sesión
            </button>
        </form>
    </div>
</nav>

<section id="inicio" class="container mt-4">
    <h1 class="fw-bold">Bienvenido, ${usuario.nombreUsuario}</h1>
    <h2 class="fw-bold">Informes en revisión</h2>

    <c:if test="${not empty mensaje}">
        <c:set var="claseMensaje" value="notification-alert bg-info" />
        <c:choose>
            <c:when test="${tipoMensaje == 'success'}">
                <c:set var="claseMensaje" value="notification-alert bg-success" />
            </c:when>
            <c:when test="${tipoMensaje == 'error'}">
                <c:set var="claseMensaje" value="notification-alert bg-danger" />
            </c:when>
            <c:when test="${tipoMensaje == 'warning'}">
                <c:set var="claseMensaje" value="notification-alert bg-warning" />
            </c:when>
        </c:choose>
        <div class="${claseMensaje}" role="alert" style="padding:15px;border-radius:6px;color:white;margin-bottom:20px;">
            ${mensaje}
        </div>
    </c:if>

    <table class="table">
        <thead>
            <tr>
                <th>Nombre Practicante</th>
                <th>Asunto</th>
                <th>Fecha</th>
                <th>Área</th>
                <th>Estado</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${not empty informes}">
                    <c:forEach var="informe" items="${informes}">
                        <c:set var="nombreArchivo" value="${fn:split(informe.rutaDocumento, '/')[fn:length(fn:split(informe.rutaDocumento, '/')) - 1]}" />
                        <c:set var="estadoInf" value="${informe.estado}" />
                        <c:set var="badgeClass" value="badge bg-secondary" />
                        <c:choose>
                            <c:when test="${estadoInf eq 'Pendiente'}">
                                <c:set var="badgeClass" value="badge bg-warning" />
                            </c:when>
                            <c:when test="${estadoInf eq 'Aprobado'}">
                                <c:set var="badgeClass" value="badge bg-success" />
                            </c:when>
                            <c:when test="${estadoInf eq 'Rechazado'}">
                                <c:set var="badgeClass" value="badge bg-danger" />
                            </c:when>
                        </c:choose>
                        <c:set var="deshabilitarBotones" value="${not (estadoInf eq 'Pendiente')}" />

                        <tr>
                            <td>${informe.practicante.nombreUsuario}</td>
                            <td>${informe.asunto}</td>
                            <td>${informe.fechaEnvio}</td>
                            <td>${informe.rol.nombreRol}</td>
                            <td><span class="${badgeClass}">${estadoInf}</span></td>
                            <td>
                                <div class="btn-group" role="group" aria-label="Acciones">
                                    <button class="btn btn-outline-primary"
                                            onclick="verDocumento(${informe.informeID})">
                                        <i class="fas fa-eye"></i> Ver Informe
                                    </button>
                                    <button type="button" class="btn btn-outline-primary btn-action"
									        onclick="mostrarModalFirma(${informe.informeID})"
									        <c:if test="${deshabilitarBotones}">disabled="disabled"</c:if>>
									    Aprobar
									</button>
                                    <button type="button" class="btn btn-outline-primary btn-action"
                                            onclick="mostrarModalRechazo(${informe.informeID})"
                                            <c:if test="${deshabilitarBotones}">disabled</c:if>>
                                        Rechazar
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="5">No hay informes pendientes.</td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
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
        <button class="btn-cerrar" style="float:right;" onclick="cerrarModal('modalFirma')">Cerrar ✖</button>
        <h3>Aprobar Informe</h3>
        <!-- Mensaje de error -->
        <c:if test="${not empty mensajeErrorFirma}">
            <div class="alert alert-danger">
                ${mensajeErrorFirma}
            </div>
        </c:if>
        <form action="${contextPath}/procesarInforme" method="post">
            <input type="hidden" name="informeId" id="firma_informeId" value="${informeIdModal}" />
            <input type="hidden" name="accion" value="aprobar" />
            <label for="usuario">Usuario:</label>
            <input type="text" id="usuario" name="usuario" required />
            <label for="contrasena">Contraseña:</label>
            <input type="password" id="contrasena" name="contrasena" required />
            <br/><br/>
            <button type="submit" class="btn btn-outline-primary">Confirmar Firma</button>
            <button type="button" class="btn btn-outline-primary"
                    onclick="cerrarModal('modalFirma')">Cancelar</button>
        </form>
    </div>
</div>

<!-- Modal Rechazar -->
<div id="modalRechazo" class="modal">
    <div class="modal-contenido">
        <button class="btn-cerrar" style="float:right;" onclick="cerrarModal('modalRechazo')">Cerrar ✖</button>
        <h3>Rechazar Informe</h3>
        <form method="post" action="${contextPath}/procesarInforme">
            <input type="hidden" name="informeId" id="rechazo_informeId" />
            <input type="hidden" name="accion" value="rechazar" />
            <label for="comentario">Comentario:</label><br/>
            <textarea id="comentario" name="comentario" rows="4" cols="50" required></textarea><br/>
            <button type="submit" class="btn btn-outline-primary">Enviar Rechazo</button>
            <button type="button" class="btn btn-outline-primary"
                    onclick="cerrarModal('modalRechazo')">Cancelar</button>
        </form>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
	var abrirModalFirma = "<c:out value='${abrirModal}' />";
	var informeIdModal = <c:out value='${informeIdModal != null ? informeIdModal : 0}' />;
</script>
<script type="text/javascript" src="${contextPath}/js/jefeUnidad.js"></script>
<script src="${contextPath}/js/documento.js"></script>

</body>
</html>
