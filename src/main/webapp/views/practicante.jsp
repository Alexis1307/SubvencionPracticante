<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
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
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="description" content="Panel del Practicante - Sistema de Gestión de Subvenciones">
    <title>Panel del Practicante | Sistema de Subvenciones</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/practicante.css">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-gradient-primary shadow-sm sticky-top">
        <div class="container-fluid">
            <a class="navbar-brand d-flex align-items-center" href="${pageContext.request.contextPath}/panel">
                <i class="fas fa-shield-halved me-2"></i>
                <span class="fw-bold">Subvención</span>
            </a>
            
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarContent">
                <ul class="navbar-nav ms-auto align-items-lg-center gap-3">
                    <li class="nav-item">
                        <div class="user-info">
                            <i class="fas fa-user-circle me-2"></i>
                            <span id="greeting">Hola, <%= usuario.getNombreUsuario() %></span>
                        </div>
                    </li>
                    <li class="nav-item">
                        <button id="darkModeToggle" class="btn btn-outline-light btn-sm" aria-label="Cambiar modo oscuro">
                            <i class="fas fa-moon"></i>
                        </button>
                    </li>
                    <li class="nav-item">
                        <form action="${pageContext.request.contextPath}/logout" method="post" class="d-inline">
                            <button type="submit" class="btn btn-light btn-sm" id="logoutBtn">
                                <i class="fas fa-sign-out-alt me-2"></i>Cerrar Sesión
                            </button>
                        </form>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <main class="container-fluid px-4 py-5">
        <div class="row mb-4">
            <div class="col-12">
                <div class="header-section d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center gap-3">
                    <div>
                        <h1 class="page-title mb-2">Panel del Practicante</h1>
                        <p class="text-muted mb-0">Gestiona tus informes y revisa tu progreso</p>
                    </div>
                    <a href="${pageContext.request.contextPath}/views/elaborar_informe.jsp" class="btn btn-primary btn-lg shadow-sm">
                        <i class="fas fa-file-edit me-2"></i>Elaborar Informe
                    </a>
                </div>
            </div>
        </div>

        <div class="row g-4 mb-4">
            <div class="col-md-4">
                <div class="stats-card card-animate">
                    <div class="stats-icon-wrapper">
                        <div class="stats-icon bg-primary">
                            <i class="fas fa-file-alt"></i>
                        </div>
                    </div>
                    <div class="stats-content">
						<h3 class="stats-number">${pendientes}</h3>
                        <p class="stats-label">Informes Enviados</p>
                        <div class="stats-progress">
                            <div class="progress-bar bg-primary" style="width: 100%"></div>
                        </div> 
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="stats-card card-animate">
                    <div class="stats-icon-wrapper">
                        <div class="stats-icon bg-success">
                            <i class="fas fa-check-circle"></i>
                        </div>
                    </div>
                    <div class="stats-content">
						<h3 class="stats-number">${aprobados}</h3>
                        <p class="stats-label">Informes Aprobados</p>
                        <div class="stats-progress">
                            <div class="progress-bar bg-success" style="width: 60%"></div>
                        </div> 
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="stats-card card-animate">
                    <div class="stats-icon-wrapper">
                        <div class="stats-icon bg-warning">
                            <i class="fas fa-clock"></i>
                        </div>
                    </div>
                     <div class="stats-content">
						<h3 class="stats-number">${rechazados}</h3>
                        <p class="stats-label">Informes Rechazados</p>
                        <div class="stats-progress">
                            <div class="progress-bar bg-warning" style="width: 40%"></div>
                        </div> 
                    </div>
                </div>
            </div>
        </div>

        <div class="row">
            <div class="col-12">
                <div class="card table-card shadow-sm card-animate">
                    <div class="card-header bg-white border-bottom">
                        <div class="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center gap-3">
                            <h5 class="card-title mb-0">
                                <i class="fas fa-list me-2 text-primary"></i>Mis Informes
                            </h5>
                            <div class="search-wrapper">
                                <i class="fas fa-search search-icon"></i>
                                <input type="text" id="tableSearch" class="form-control form-control-sm search-input" placeholder="Buscar informes...">
                            </div>
                        </div>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0" id="informesTable">
                                <thead class="table-light">
                                    <tr>
                                        <th class="text-center">#</th>
                                        <th>Asunto</th>
                                        <th class="text-center">Periodo</th>
                                        <th class="text-center">Fecha</th>
                                        <th class="text-center">Estado</th>
                                        <th class="text-center">Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
								    <c:forEach var="informe" items="${informes}" varStatus="status">
								        <tr>
								            <td class="text-center fw-bold">${status.index + 1}</td>
								            <td>${informe.asunto}</td>
								            <td class="text-center"> ${informe.periodoPracticas}</td>
								            <td class="text-center">
								                <fmt:formatDate value="${informe.fechaEnvioDate}" pattern="dd/MM/yyyy"/>
								            </td>
								            <td class="text-center">
								                <c:choose>
								                    <c:when test="${informe.estado == 'Aprobado'}">
								                        <span class="badge status-badge bg-success">Aprobado</span>
								                    </c:when>
								                    <c:when test="${informe.estado == 'Pendiente'}">
								                        <span class="badge status-badge bg-warning">Pendiente</span>
								                    </c:when>
								                    <c:when test="${informe.estado == 'Rechazado'}">
								                        <span class="badge status-badge bg-danger">Rechazado</span>
								                    </c:when>
								                    <c:otherwise>
								                        <span class="badge status-badge bg-secondary">${informe.estado}</span>
								                    </c:otherwise>
								                </c:choose>
								            </td>
								            <td class="text-center">
											    <div class="btn-group btn-group-sm" role="group">
											        <c:choose>
													    <c:when test="${informe.estado == 'Aprobado'}">
													        <button class="btn btn-outline-primary"
													            onclick="verDocumento('${pageContext.request.contextPath}', '${informe.nombreDocumento}', 'firmado')">
													            <i class="fas fa-eye"></i>
													        </button>
													    </c:when>
													    <c:otherwise>
													        <button class="btn btn-outline-primary"
													            onclick="verDocumento('${pageContext.request.contextPath}', '${informe.nombreDocumento}', 'practicante')">
													            <i class="fas fa-eye"></i>
													        </button>
													    </c:otherwise>
													</c:choose>					
											        <a href="${pageContext.request.contextPath}/archivos/${informe.rutaDocumento}"
											           class="btn btn-outline-secondary"
											           download
											           data-bs-toggle="tooltip" title="Descargar">
											            <i class="fas fa-download"></i>
											        </a>
											    </div>
											</td>
								        </tr>
								    </c:forEach>
								</tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <footer class="footer mt-5 py-4 bg-white border-top shadow-sm">
        <div class="container-fluid px-4">
            <div class="row align-items-center">
                <div class="col-md-6 text-center text-md-start">
                    <p class="mb-0 text-muted">
                        &copy; 2025 Sistema de Subvenciones. Todos los derechos reservados.
                    </p>
                </div>
                <div class="col-md-6 text-center text-md-end">
                    <div class="footer-links">
                        <a href="#" class="text-muted text-decoration-none me-3">
                            <i class="fas fa-question-circle me-1"></i>Ayuda
                        </a>
                        <a href="#" class="text-muted text-decoration-none me-3">
                            <i class="fas fa-file-contract me-1"></i>Términos
                        </a>
                        <a href="#" class="text-muted text-decoration-none">
                            <i class="fas fa-shield-alt me-1"></i>Privacidad
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </footer>
    
    <div id="modalDocumento" style="display: none; position: fixed; top:0; left:0; width:100%; height:100%; background: rgba(0,0,0,0.5); z-index: 1050;">
	    <div class="modal-content bg-white shadow rounded p-3" style="width: 80%; height: 90%; position: relative;">
	        <button type="button" class="btn-close position-absolute top-0 end-0 m-3" aria-label="Cerrar" onclick="cerrarModal('modalDocumento')"></button>
	        <div id="visorDocumento" style="width: 100%; height: 100%;"></div>
	    </div>
	</div>

    

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <!-- <script src="${pageContext.request.contextPath}/js/practicante.js"></script> -->
    <script>const contextPath = '<%= request.getContextPath() %>';</script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/practicante.js"></script>
</body>
</html>