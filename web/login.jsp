<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Login - SGP Ay Chabela</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body class="login-body">

    <div class="login-card">
        <div class="brand">
            <div class="brand-title">Ay Chabela</div>
            <div class="brand-sub">Sistema de Gestion de Pedidos</div>
        </div>

        <h2>Iniciar sesion</h2>
        <p class="subtitle">Ingresa tus credenciales para continuar</p>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>

        <form action="LoginServlet" method="post">
            <div class="form-group">
                <label for="usuario">Usuario</label>
                <input type="text" id="usuario" name="usuario" required autofocus>
            </div>

            <div class="form-group">
                <label for="password">Contrasena</label>
                <input type="password" id="password" name="password" required>
            </div>

            <button type="submit" class="btn btn-primary btn-block">Ingresar</button>
        </form>

        <div style="margin-top:20px; padding:12px; background:#F0F4F8; border-radius:6px; font-size:12px; color:#64748B;">
            <strong>Usuarios de prueba:</strong><br>
            admin / admin123 (Administrador)<br>
            mesero / mesero123 (Mesero)<br>
            cocina / cocina123 (Cocina)
        </div>
    </div>

</body>
</html>
