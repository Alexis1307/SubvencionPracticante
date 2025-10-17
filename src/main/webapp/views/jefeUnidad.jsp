<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="jakarta.servlet.http.*, model.Usuario" %>
<%
    HttpSession sesion = request.getSession(false);
    Usuario usuario = sesion != null ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Panel | Jefe de Unidad</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/jefeUnidad.css">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container-fluid">
        <a class="navbar-brand fw-bold" href="#">
            <i class="fas fa-clipboard-check me-2"></i>Panel Jefe de Unidad
        </a>
        <div class="d-flex align-items-center gap-3">
            <span class="text-white">
                <i class="fas fa-user-circle me-2"></i><%= usuario.getNombreUsuario() %>
            </span>
            <form action="${pageContext.request.contextPath}/logout" method="post">
                <button class="btn btn-light btn-sm">
                    <i class="fas fa-sign-out-alt me-1"></i>Cerrar sesión
                </button>
            </form>
        </div>
    </div>
</nav>

<main class="container py-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="fw-bold text-primary">
            <i class="fas fa-folder-open me-2"></i>Informes de Practicantes
        </h2>
        <button class="btn btn-outline-primary btn-sm" onclick="location.reload()">
            <i class="fas fa-sync-alt me-1"></i>Actualizar
        </button>
    </div>

    <div class="card shadow-sm">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0" id="tabla-informes">
                    <thead class="table-light">
                    <tr>
                        <th>Practicante</th>
                        <th>Asunto</th>
                        <th>Area</th>
                        <th>Periodo</th>
                        <th>Fecha</th>
                        <th>Estado</th>
                        <th class="text-center">Acciones</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="informe" items="${informes}">
                        <tr>
                            <td class="fw-semibold">${informe.practicante.nombreUsuario}</td>
                            <td>${informe.asunto}</td>
                            <td>${informe.rol.nombreRol}</td>
                            <td><span class="badge bg-info">${informe.periodoPracticas}</span></td>
                            <td>${informe.fechaEnvio}</td>
                            <td>
                                <span class="badge 
                                    ${informe.estado == 'En revisión' ? 'bg-warning' :
                                      informe.estado == 'Aprobado' ? 'bg-success' :
                                      informe.estado == 'Rechazado' ? 'bg-danger' : 'bg-secondary'}">
                                    ${informe.estado}
                                </span>
                            </td>
                            <td class="text-center">
                                <div class="btn-group" role="group">
                                	<button onclick="verDocumento('Informe_practicante_1759649033838.pdf')" 
                                			class="btn-ver">
									    Ver Informe
									</button>
                                    <button type="button"
                                            class="btn btn-sm btn-outline-success btn-action-aprobar"
                                            data-id="${informe.informeID}"
                                            title="Aprobar informe">
                                        <i class="fas fa-check"></i>
                                    </button>
                                    <button type="button"
                                            class="btn btn-sm btn-outline-danger btn-action-rechazar"
                                            data-id="${informe.informeID}"
                                            title="Rechazar informe">
                                        <i class="fas fa-times"></i>
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Leyenda -->
    <div class="card mt-4 border-0 bg-light">
        <div class="card-body">
            <h6 class="fw-bold mb-3">
                <i class="fas fa-info-circle me-2"></i>Leyenda de Estados
            </h6>
            <div class="d-flex flex-wrap gap-3">
            	<div class="d-flex align-items-center">
                    <span class="badge bg-secondary me-2">Pendiente</span>
                    <small class="text-muted">Pendiente a Revisar</small>
                </div>
                <div class="d-flex align-items-center">
                    <span class="badge bg-warning me-2">En revisión</span>
                    <small class="text-muted">Falta Aprobación</small>
                </div>
                <div class="d-flex align-items-center">
                    <span class="badge bg-success me-2">Aprobado</span>
                    <small class="text-muted">Informe aprobado</small>
                </div>
                <div class="d-flex align-items-center">
                    <span class="badge bg-danger me-2">Rechazado</span>
                    <small class="text-muted">Informe rechazado</small>
                </div>
            </div>
        </div>
    </div>
</main>

<!-- Modal para aver Documento -->
<div id="modalDocumento" class="modal" style="display:none;">
    <div class="modal-contenido modal-documento">
        <h3>Visualización del informe</h3>
        <div id="visorDocumento" style="height:500px;"></div>
        <div id="botonesModalDocumento" style="margin-top: 10px;">
            <button onclick="cerrarModal('modalDocumento')" class="btn-cerrar">
                Cerrar
            </button>
        </div>
    </div>
</div>

<!-- Modal para aprobar (credenciales + comentario) -->
<div class="modal fade" id="modalAprobar" tabindex="-1" aria-labelledby="modalAprobarLabel" aria-hidden="true">
    <div class="modal-dialog">
        <form id="formAprobar" method="post" action="${pageContext.request.contextPath}/procesarInforme">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Confirmar aprobación</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
                </div>
                <div class="modal-body">
                    <input type="hidden" name="informeId" id="modalAprobarInformeId">
                    <input type="hidden" name="accion" value="aprobar">
                    <div class="mb-3">
                        <label for="usuarioAprobar" class="form-label">Usuario</label>
                        <input type="text" name="usuario" id="usuarioAprobar" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label for="contrasenaAprobar" class="form-label">Contraseña</label>
                        <input type="password" name="contrasena" id="contrasenaAprobar" class="form-control" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-success">Aprobar</button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                </div>
            </div>
        </form>
    </div>
</div>

<!-- Scripts -->
<script>const contextPath = '<%= request.getContextPath() %>';</script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jefeUnidad.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>