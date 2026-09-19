<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="java.util.List, capaEntidad.Plato" %>
<% request.setAttribute("paginaActual", "platos"); %>

<%@ page import="java.util.List, capaEntidad.Plato, capaEntidad.Usuario" %>
<% request.setAttribute("paginaActual", "platos"); %>
<%
    // El boton "Receta" solo se muestra a quien puede editarla (admin y cocina).
    Usuario uPlatos = (Usuario) session.getAttribute("usuario");
    String rolPlatos = (uPlatos == null || uPlatos.getRol() == null) ? "" : uPlatos.getRol();
    boolean verReceta = "ADMIN".equalsIgnoreCase(rolPlatos) || "COCINA".equalsIgnoreCase(rolPlatos);
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Platos - SGP Ay Chabela</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="contenedor">
    <div class="titulo-pagina">
        <span>Gestion de Platos</span>
        <a href="PlatoServlet?accion=nuevo" class="btn btn-primary">+ Nuevo Plato</a>
    </div>

    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Descripcion</th>
                <th>Categoria</th>
                <th>Precio</th>
                <th>Estado</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
        <%
            List<Plato> platos = (List<Plato>) request.getAttribute("platos");
            if (platos != null && !platos.isEmpty()) {
                for (Plato p : platos) {
        %>
            <tr>
                <td><%= p.getId() %></td>
                <td><strong><%= p.getNombre() %></strong></td>
                <td><%= p.getDescripcion() == null ? "" : p.getDescripcion() %></td>
                <td><%= p.getCategoriaNombre() %></td>
                <td>S/ <%= String.format("%.2f", p.getPrecio()) %></td>
                <td>
                    <% if (p.isDisponible()) { %>
                        <span class="badge badge-disponible">Disponible</span>
                    <% } else { %>
                        <span class="badge badge-nodisponible">No disponible</span>
                    <% } %>
                </td>
                <td>
                    <a href="PlatoServlet?accion=editar&id=<%= p.getId() %>" class="btn btn-warning btn-sm">Editar</a>

                    <% if (verReceta) { %>
                    <a href="RecetaServlet?platoId=<%= p.getId() %>" class="btn btn-secondary btn-sm">Receta</a>
                    <% } %>

                    <a href="PlatoServlet?accion=eliminar&id=<%= p.getId() %>"
                       class="btn btn-danger btn-sm"
                       onclick="return confirm('Eliminar este plato?');">Eliminar</a>
                </td>
            </tr>
        <%
                }
            } else {
        %>
            <tr><td colspan="7" style="text-align:center; padding:24px;">No hay platos registrados</td></tr>
        <% } %>
        </tbody>
    </table>
</div>

</body>
</html>
