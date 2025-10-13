<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
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
                <form action="${pageContext.request.contextPath}/cerrarSesion" method="post">
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
                                <th><i class="fas fa-user me-2"></i>Practicante</th>
                                <th><i class="fas fa-file-alt me-2"></i>Asunto</th>
                                <th><i class="fas fa-calendar me-2"></i>Periodo</th>
                                <th><i class="fas fa-calendar-day me-2"></i>Fecha</th>
                                <th><i class="fas fa-info-circle me-2"></i>Estado</th>
                                <th class="text-center"><i class="fas fa-cogs me-2"></i>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td class="fw-semibold">Juan Pérez Gómez</td>
                                <td>Informe Final de Prácticas</td>
                                <td><span class="badge bg-info">6 meses</span></td>
                                <td>2025-01-15</td>
                                <td><span class="badge bg-warning"><i class="fas fa-clock me-1"></i>En revisión</span></td>
                                <td class="text-center">
                                    <div class="btn-group" role="group">
                                        <button type="button" class="btn btn-sm btn-outline-primary btn-action" data-action="ver" data-id="1" title="Ver informe">
                                            <i class="fas fa-eye"></i>
                                        </button>
                                        <button type="button" class="btn btn-sm btn-outline-success btn-action" data-action="aprobar" data-id="1" title="Aprobar informe">
                                            <i class="fas fa-check"></i>
                                        </button>
                                        <button type="button" class="btn btn-sm btn-outline-danger btn-action" data-action="rechazar" data-id="1" title="Rechazar informe">
                                            <i class="fas fa-times"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td class="fw-semibold">María González López</td>
                                <td>Informe Mensual - Periodo 3</td>
                                <td><span class="badge bg-info">3 meses</span></td>
                                <td>2025-01-10</td>
                                <td><span class="badge bg-warning"><i class="fas fa-clock me-1"></i>En revisión</span></td>
                                <td class="text-center">
                                    <div class="btn-group" role="group">
                                        <button type="button" class="btn btn-sm btn-outline-primary btn-action" data-action="ver" data-id="2" title="Ver informe">
                                            <i class="fas fa-eye"></i>
                                        </button>
                                        <button type="button" class="btn btn-sm btn-outline-success btn-action" data-action="aprobar" data-id="2" title="Aprobar informe">
                                            <i class="fas fa-check"></i>
                                        </button>
                                        <button type="button" class="btn btn-sm btn-outline-danger btn-action" data-action="rechazar" data-id="2" title="Rechazar informe">
                                            <i class="fas fa-times"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td class="fw-semibold">Carlos Ramírez Torres</td>
                                <td>Informe de Avance - Mes 2</td>
                                <td><span class="badge bg-info">4 meses</span></td>
                                <td>2025-01-08</td>
                                <td><span class="badge bg-warning"><i class="fas fa-clock me-1"></i>En revisión</span></td>
                                <td class="text-center">
                                    <div class="btn-group" role="group">
                                        <button type="button" class="btn btn-sm btn-outline-primary btn-action" data-action="ver" data-id="3" title="Ver informe">
                                            <i class="fas fa-eye"></i>
                                        </button>
                                        <button type="button" class="btn btn-sm btn-outline-success btn-action" data-action="aprobar" data-id="3" title="Aprobar informe">
                                            <i class="fas fa-check"></i>
                                        </button>
                                        <button type="button" class="btn btn-sm btn-outline-danger btn-action" data-action="rechazar" data-id="3" title="Rechazar informe">
                                            <i class="fas fa-times"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="card mt-4 border-0 bg-light">
            <div class="card-body">
                <h6 class="fw-bold mb-3">
                    <i class="fas fa-info-circle me-2"></i>Leyenda de Estados
                </h6>
                <div class="d-flex flex-wrap gap-3">
                    <div class="d-flex align-items-center">
                        <span class="badge bg-warning me-2">En revisión</span>
                        <small class="text-muted">Pendiente de aprobación</small>
                    </div>
                    <div class="d-flex align-items-center">
                        <span class="badge bg-success me-2">Aprobado</span>
                        <small class="text-muted">Informe aprobado</small>
                    </div>
                    <div class="d-flex align-items-center">
                        <span class="badge bg-danger me-2">Rechazado</span>
                        <small class="text-muted">Informe rechazado</small>
                    </div>
                    <div class="d-flex align-items-center">
                        <span class="badge bg-secondary me-2">Procesando</span>
                        <small class="text-muted">Acción en curso</small>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <script>const contextPath = '<%= request.getContextPath() %>';</script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/jefeUnidad.js"></script>
</body>
</html>