<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="jakarta.servlet.http.*, model.Usuario" %>
<%
    HttpSession sesion = request.getSession(false);
    Usuario usuario = sesion != null ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;
    if (usuario == null) {
        response.sendRedirect("../login.jsp");
        return;
    }
    
    String mensajeExito = (String) request.getAttribute("mensajeExito");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Elaborar Informe - Practicante</title>
    <link rel="stylesheet" href="css/elaborar_informe.css">
</head>
<body>

    <nav class="navbar">
        <div class="navbar-left">
            <a href="practicante.jsp">Inicio</a>
        </div>
        <div class="navbar-right">
            <span>Bienvenido, <%= usuario.getNombreUsuario() %></span>
            <form action="cerrarSesion" method="post" style="display:inline;">
                <button type="submit">Cerrar sesión</button>
            </form>
        </div>
    </nav>

    <section id="inicio">
        <h2>Elaborar Nuevo Informe</h2>

        <% if ("1".equals(mensajeExito)) { %>
            <p style="color: green;">¡Informe enviado correctamente!</p>
        <% } %>

        <form action="${pageContext.request.contextPath}/elaborarInforme" method="post">
            <div>
                <label>Asunto:</label><br>
                <input type="text" name="asunto" required>
            </div>

            <div>
                <label>Periodo de prácticas (meses):</label><br>
                <input type="number" name="periodo" min="1" max="12" required>
            </div>

            <div>
                <label>Actividades realizadas:</label><br>
                <textarea name="actividades" rows="6" cols="50" required></textarea>
            </div>

            <div>
                <button type="submit">Guardar y Enviar</button>
            </div>
        </form>
    </section>

</body>
</html>