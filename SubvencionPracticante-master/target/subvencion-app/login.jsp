<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

    <div class="d-flex justify-content-center align-items-center vh-100">
        <div class="card shadow-sm p-4 rounded-4" style="max-width: 350px; width: 100%;">
            <div class="text-center mb-4">
                <h2 class="fw-bold">Iniciar sesión</h2>
            </div>
            <form action="login" method="post">
			    <div class="mb-3">
			        <label for="username" class="form-label">Usuario</label>
			        <input type="text" name="nombreUsuario" id="username" class="form-control" placeholder="Ingrese su usuario" required>
			    </div>
			    <div class="mb-3">
			        <label for="password" class="form-label">Contraseña</label>
			        <input type="password" name="contra" id="password" class="form-control" placeholder="Ingrese su contraseña" required>
			    </div>
			    <button type="submit" class="btn btn-primary w-100 fw-bold">Ingresar</button>
			    
			    <!-- Mensaje de error si falla -->
			    <div class="mt-3 text-center text-danger">
			        <%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %>
			    </div>
			</form>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="js/login.js"></script>
</body>
</html>
