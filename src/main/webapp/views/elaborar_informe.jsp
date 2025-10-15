<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="jakarta.servlet.http.*, model.Usuario" %>
<%
    HttpSession sesion = request.getSession(false);
    Usuario usuario = sesion != null ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;
    if (usuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Elaborar Informe - Sistema de Gestión de Subvenciones</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/elaborar_informe.css">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark fixed-top">
        <div class="container-fluid">
            <a class="navbar-brand" href="#">
                <i class="fas fa-file-alt me-2"></i>Elaborar Informe
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarContent">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="<%= request.getContextPath() %>/views/practicante.jsp">
                            <i class="fas fa-arrow-left me-2"></i>Volver al Panel
                        </a>
                    </li>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                            <i class="fas fa-user-circle me-2"></i><%= usuario.getNombreUsuario() %>
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end">
                            <li>
                                <form action="<%= request.getContextPath() %>/logout" method="POST" class="m-0">
                                    <button type="submit" class="dropdown-item text-danger">
                                        <i class="fas fa-sign-out-alt me-2"></i>Cerrar Sesión
                                    </button>
                                </form>
                            </li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <main class="main-container">
        <div class="container">
            <nav aria-label="breadcrumb" class="breadcrumb-nav">
                <ol class="breadcrumb">
                    <li class="breadcrumb-item">
                        <a href="<%= request.getContextPath() %>/views/practicante.jsp">
                            <i class="fas fa-home"></i> Inicio
                        </a>
                    </li>
                    <li class="breadcrumb-item">
                        <a href="<%= request.getContextPath() %>/views/practicante">Panel del Practicante</a>
                    </li>
                    <li class="breadcrumb-item active">Elaborar Informe</li>
                </ol>
            </nav>

            <div class="form-wrapper">
                <div class="form-header">
                    <h1><i class="fas fa-edit me-3"></i>Nuevo Informe de Prácticas</h1>
                    <p>Complete los campos para registrar su informe del periodo</p>
                </div>

                <% if (request.getAttribute("mensajeExito") != null) { %>
                    <div class="alert alert-success alert-dismissible fade show">
                        <i class="fas fa-check-circle me-2"></i>
                        <%= request.getAttribute("mensajeExito") %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                <% } else if (request.getAttribute("mensajeError") != null) { %>
                    <div class="alert alert-danger alert-dismissible fade show">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        <%= request.getAttribute("mensajeError") %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                <% } %>

				<form id="formInforme" action="<%= request.getContextPath() %>/elaborarInforme" method="POST">
                    <div class="row g-4">
                        <div class="col-md-6">
                            <label for="asunto" class="form-label">
                                <i class="fas fa-tag me-2"></i>Asunto del Informe
                            </label>
                            <input 
                                type="text" 
                                class="form-control" 
                                id="asunto" 
                                name="asunto" 
                                placeholder="Ejemplo: Informe del 2° mes de prácticas"
                                maxlength="200"
                                required>
                            <div class="invalid-feedback"></div>
                        </div>

                        <div class="col-md-6">
                            <label for="periodo" class="form-label">
                                <i class="fas fa-calendar-alt me-2"></i>Periodo del Informe
                            </label>
                            <select class="form-select" id="periodo" name="periodo" required>
                                <option value="" selected disabled>Seleccione el periodo</option>
                                <option value="1">1 mes</option>
                                <option value="2">2 mes</option>
                                <option value="3">3 mes</option>
                                <option value="4">4 mes</option>
                                <option value="5">5 mes</option>
                                <option value="6">6 mes (Final)</option>
                                <option value="7">7 mes</option>
                                <option value="8">8 mes</option>
                                <option value="9">9 mes</option>
                                <option value="10">10 mes</option>
                                <option value="11">11 mes</option>
                                <option value="12">12 mes</option>
                            </select>
                            <div class="invalid-feedback"></div>
                        </div>

                        <div class="col-12">
                            <label for="actividades" class="form-label">
                                <i class="fas fa-tasks me-2"></i>Actividades Realizadas
                            </label>
                            <textarea 
                                class="form-control" 
                                id="actividades" 
                                name="actividades" 
                                rows="8"
                                placeholder="Describe las actividades realizadas durante tu periodo de prácticas…"
                                maxlength="2000"
                                required></textarea>
                            <div class="invalid-feedback"></div>
                            <div class="char-counter">
                                <span id="charCount">0</span> / 2000 caracteres
                            </div>
                        </div>

                        <div class="col-12">
                            <div class="form-actions">
                                <a href="<%= request.getContextPath() %>/views/practicante.jsp" class="btn btn-secondary">
                                    <i class="fas fa-times me-2"></i>Cancelar
                                </a>
                                <button type="submit" class="btn btn-primary" id="submitBtn">
                                    <span class="btn-text">
                                        <i class="fas fa-paper-plane me-2"></i>Enviar Informe
                                    </span>
                                    <span class="btn-spinner" style="display: none;">
                                        <span class="spinner-border spinner-border-sm me-2"></span>Enviando...
                                    </span>
                                </button>
                            </div>
                        </div>
                    </div>
                </form>

                <div class="info-cards">
                    <div class="info-card">
                        <div class="info-icon tips">
                            <i class="fas fa-lightbulb"></i>
                        </div>
                        <h5>💡 Consejo rápido</h5>
                        <p>Sea claro y específico al describir sus actividades. Incluya resultados medibles y aprendizajes obtenidos.</p>
                    </div>

                    <div class="info-card">
                        <div class="info-icon time">
                            <i class="fas fa-clock"></i>
                        </div>
                        <h5>⏱ Tiempo estimado de revisión</h5>
                        <p>Su informe será revisado y validado en un plazo de 24-48 horas hábiles por el supervisor asignado.</p>
                    </div>

                    <div class="info-card">
                        <div class="info-icon support">
                            <i class="fas fa-headset"></i>
                        </div>
                        <h5>📞 Soporte</h5>
                        <p>¿Necesita ayuda? Contacte al Jefe de Unidad o escriba a soporte@subvenciones.edu.pe</p>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <footer class="footer">
        <div class="container">
            <p class="mb-0">
                <i class="fas fa-shield-alt me-2"></i>
                Sistema de Gestión de Subvenciones © 2025 – Todos los derechos reservados.
            </p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="<%= request.getContextPath() %>/js/elaborar_informe.js"></script>
</body>
</html>