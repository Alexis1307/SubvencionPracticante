<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String error = (String) request.getAttribute("error");
String mensaje = (String) request.getAttribute("mensaje");

HttpSession sesion = request.getSession(false);
if (sesion != null && sesion.getAttribute("usuarioLogueado") != null) {
    response.sendRedirect(request.getContextPath() + "/login");
    return;
}
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="description" content="Sistema de Gestión - Inicio de Sesión">
    <title>Iniciar Sesión | Sistema de Gestión</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
</head>
<body>
    <div class="particles"></div>
    
    <div class="login-container">
        <div class="login-card">
            <div class="login-header">
                <div class="logo-circle">
                    <i class="fas fa-shield-halved"></i>
                </div>
                <h1 class="login-title">Bienvenido</h1>
                <p class="login-subtitle">Ingresa tus credenciales para continuar</p>
            </div>

            <% if (error != null && !error.isEmpty()) { %>
            <div class="alert alert-error">
                <i class="fas fa-exclamation-circle"></i>
                <span><%= error %></span>
            </div>
            <% } %>

            <% if (mensaje != null && !mensaje.isEmpty()) { %>
            <div class="alert alert-info">
                <i class="fas fa-info-circle"></i>
                <span><%= mensaje %></span>
            </div>
            <% } %>

            <div id="errorBox" class="error-box"></div>

            <form id="loginForm" action="${pageContext.request.contextPath}/login" method="POST">
                <div class="form-group">
                    <label for="correo" class="form-label">
                        <i class="fas fa-envelope"></i>
                        Usuario
                    </label>
                    <input 
					    type="text" 
					    id="correo" 
					    name="nombreUsuario"  
					    class="form-control" 
					    placeholder="Usuario"
					    autofocus>
                </div>

                <div class="form-group">
                    <label for="clave" class="form-label">
                        <i class="fas fa-lock"></i>
                        Contraseña
                    </label>
                    <div class="password-wrapper">
                        <input 
						    type="password" 
						    id="clave" 
						    name="contra"  
						    class="form-control" 
						    placeholder="••••••••"
						    autocomplete="current-password">
                        <button type="button" class="toggle-password" id="togglePassword" aria-label="Mostrar contraseña">
                            <i class="fas fa-eye"></i>
                        </button>
                    </div>
                </div>

                <div class="form-options">
                	<a href="${pageContext.request.contextPath}/nuevoUsuario">Registrarse</a>
                    <label class="checkbox-wrapper">
                        <input type="checkbox" id="remember" name="remember">
                        <span class="checkbox-label">Recordarme</span>
                    </label>
                    <a href="${pageContext.request.contextPath}/recuperar-password" class="link-forgot">
                        ¿Olvidaste tu contraseña?
                    </a>
                </div>

                <button type="submit" class="btn-submit">
                    <span class="btn-text">Iniciar Sesión</span>
                    <span class="btn-loader">
                        <i class="fas fa-circle-notch fa-spin"></i>
                    </span>
                </button>
            </form>

            <div class="divider">
                <span>o</span>
            </div>

            <div class="register-section">
                <p>¿No tienes una cuenta?</p>
                <a href="${pageContext.request.contextPath}/registro" class="link-register">
                    Registrarse ahora
                </a>
            </div>

            <div class="footer-links">
                <a href="${pageContext.request.contextPath}/soporte">
                    <i class="fas fa-headset"></i>
                    Soporte
                </a>
                <a href="${pageContext.request.contextPath}/ayuda">
                    <i class="fas fa-question-circle"></i>
                    Ayuda
                </a>
                <a href="${pageContext.request.contextPath}/terminos">
                    <i class="fas fa-file-contract"></i>
                    Términos
                </a>
            </div>
        </div>

        <div class="features-section">
            <div class="feature-card">
                <div class="feature-icon">
                    <i class="fas fa-lock-open"></i>
                </div>
                <h3>Seguro y Confiable</h3>
                <p>Tus datos están protegidos con cifrado de nivel empresarial</p>
            </div>
            <div class="feature-card">
                <div class="feature-icon">
                    <i class="fas fa-bolt"></i>
                </div>
                <h3>Acceso Rápido</h3>
                <p>Inicia sesión en segundos desde cualquier dispositivo</p>
            </div>
            <div class="feature-card">
                <div class="feature-icon">
                    <i class="fas fa-clock"></i>
                </div>
                <h3>Disponible 24/7</h3>
                <p>Accede a tu cuenta en cualquier momento del día</p>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/login.js"></script>
</body>
</html>