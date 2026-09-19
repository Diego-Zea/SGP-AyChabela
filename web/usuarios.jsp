<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, capaEntidad.Usuario" %>
<% request.setAttribute("paginaActual", "usuarios"); %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Usuarios - SGP Ay Chabela</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="contenedor">
    <div class="titulo-pagina">
        <span>Gestion de Usuarios</span>
        <a href="UsuarioServlet?accion=nuevo" class="btn btn-primary">+ Nuevo Usuario</a>
    </div>

    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>DNI</th>
                <th>Nombre</th>
                <th>Usuario</th>
                <th>Rol</th>
                <th>Estado</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
        <%
            List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios");
            if (usuarios != null && !usuarios.isEmpty()) {
                for (Usuario u : usuarios) {
        %>
            <tr>
                <td><%= u.getId() %></td>
                <td><%= u.getDni() %></td>
                <td><strong><%= u.getNombre() %></strong></td>
                <td><%= u.getUsuario() %></td>
                <td><%= u.getRol() %></td>
                <td>
                    <% if (u.isActivo()) { %>
                        <span class="badge badge-disponible">Activo</span>
                    <% } else { %>
                        <span class="badge badge-nodisponible">Inactivo</span>
                    <% } %>
                </td>
                <td>
                    <a href="UsuarioServlet?accion=editar&id=<%= u.getId() %>" class="btn btn-warning btn-sm">Editar</a>
                    <a href="UsuarioServlet?accion=eliminar&id=<%= u.getId() %>"
                       class="btn btn-danger btn-sm"
                       onclick="return confirm('Eliminar este usuario?');">Eliminar</a>
                </td>
            </tr>
        <%
                }
            } else {
        %>
            <tr><td colspan="7" style="text-align:center; padding:24px;">No hay usuarios registrados</td></tr>
        <% } %>
        </tbody>
    </table>
</div>

</body>
</html>
