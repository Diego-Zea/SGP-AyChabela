<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="capaEntidad.Usuario" %>
<% request.setAttribute("paginaActual", "usuarios"); %>
<%
    Usuario u = (Usuario) request.getAttribute("u");
    boolean esEdicion = (u != null);
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title><%= esEdicion ? "Editar" : "Nuevo" %> Usuario - SGP Ay Chabela</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="contenedor">
    <h2 class="titulo-pagina"><%= esEdicion ? "Editar Usuario" : "Nuevo Usuario" %></h2>

    <div class="card">
        <form action="UsuarioServlet" method="post">
            <% if (esEdicion) { %>
                <input type="hidden" name="id" value="<%= u.getId() %>">
            <% } %>

            <div class="form-grid-2">
                <div class="form-group">
                    <label for="dni">DNI *</label>
                    <input type="text" id="dni" name="dni" maxlength="15" required
                           value="<%= esEdicion ? u.getDni() : "" %>">
                </div>

                <div class="form-group">
                    <label for="usuario">Usuario (login) *</label>
                    <input type="text" id="usuario" name="usuario" required
                           value="<%= esEdicion ? u.getUsuario() : "" %>">
                </div>
            </div>

            <div class="form-grid-2">
                <div class="form-group">
                    <label for="nombres">Nombres *</label>
                    <input type="text" id="nombres" name="nombres" required
                           value="<%= esEdicion ? u.getNombres() : "" %>">
                </div>

                <div class="form-group">
                    <label for="apellidos">Apellidos *</label>
                    <input type="text" id="apellidos" name="apellidos" required
                           value="<%= esEdicion ? u.getApellidos() : "" %>">
                </div>
            </div>

            <div class="form-grid-2">
                <div class="form-group">
                    <label for="password">Clave *</label>
                    <input type="text" id="password" name="password" required
                           value="<%= esEdicion ? u.getPassword() : "" %>">
                </div>

                <div class="form-group">
                    <label for="rol">Rol *</label>
                    <select id="rol" name="rol" required>
                        <option value="MESERO" <%= esEdicion && "MESERO".equals(u.getRol()) ? "selected" : "" %>>Mesero</option>
                        <option value="COCINA" <%= esEdicion && "COCINA".equals(u.getRol()) ? "selected" : "" %>>Cocina</option>
                        <option value="ADMIN"  <%= esEdicion && "ADMIN".equals(u.getRol())  ? "selected" : "" %>>Administrador</option>
                    </select>
                </div>
            </div>

            <div class="form-group">
                <label>
                    <input type="checkbox" name="activo" value="on"
                        <%= (!esEdicion || u.isActivo()) ? "checked" : "" %>>
                    Usuario activo
                </label>
            </div>

            <div style="display:flex; gap:10px; margin-top:16px;">
                <button type="submit" class="btn btn-primary">Guardar</button>
                <a href="UsuarioServlet" class="btn btn-secondary">Cancelar</a>
            </div>
        </form>
    </div>
</div>

</body>
</html>
