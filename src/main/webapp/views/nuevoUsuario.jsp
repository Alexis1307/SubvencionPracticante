<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Registrar nuevo usuario</title>
</head>
<body>
    <h2>Registrar nuevo usuario</h2>

    <c:if test="${not empty error}">
        <p style="color:red;">${error}</p>
    </c:if>
    <c:if test="${not empty mensaje}">
        <p style="color:green;">${mensaje}</p>
    </c:if>

    <form action="${pageContext.request.contextPath}/nuevoUsuario" method="post">
        <label>Nombre de usuario:</label><br>
        <input type="text" name="nombreUsuario" required><br><br>

        <label>Contraseña:</label><br>
        <input type="password" name="contra" required><br><br>

        <label>Área / Rol:</label><br>
        <select name="rolId" required>
            <option value="">Seleccione un área...</option>
            <c:forEach var="r" items="${roles}">
                <option value="${r.rolId}">${r.nombreRol}</option>
            </c:forEach>
        </select><br><br>

        <button type="submit">Registrar</button>
    </form>

    <br>
    <a href="${pageContext.request.contextPath}/login">Volver al login</a>
</body>
</html>
