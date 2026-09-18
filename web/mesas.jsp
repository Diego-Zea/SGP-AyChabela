<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, capaEntidad.Mesa" %>
<% request.setAttribute("paginaActual", "mesas"); %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Mesas - SGP Ay Chabela</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="contenedor">
    <h2 class="titulo-pagina">Gestion de Mesas</h2>

    <table>
        <thead>
            <tr>
                <th>Numero</th>
                <th>Capacidad</th>
                <th>Ubicacion</th>
                <th>Estado</th>
                <th>Cambiar estado</th>
            </tr>
        </thead>
        <tbody>
        <%
            List<Mesa> mesas = (List<Mesa>) request.getAttribute("mesas");
            if (mesas != null && !mesas.isEmpty()) {
                for (Mesa m : mesas) {
                    String badge = "badge-libre";
                    if ("OCUPADA".equals(m.getEstado())) badge = "badge-ocupada";
                    else if ("LISTA".equals(m.getEstado())) badge = "badge-lista";
        %>
            <tr>
                <td><strong>Mesa #<%= m.getNumero() %></strong></td>
                <td><%= m.getCapacidad() %> personas</td>
                <td><%= m.getUbicacion() == null ? "" : m.getUbicacion() %></td>
                <td><span class="badge <%= badge %>"><%= m.getEstado() %></span></td>
                <td>
                    <a href="MesaServlet?accion=cambiarEstado&id=<%= m.getId() %>&estado=LIBRE"
                       class="btn btn-success btn-sm">Libre</a>
                    <a href="MesaServlet?accion=cambiarEstado&id=<%= m.getId() %>&estado=OCUPADA"
                       class="btn btn-danger btn-sm">Ocupada</a>
                    <a href="MesaServlet?accion=cambiarEstado&id=<%= m.getId() %>&estado=LISTA"
                       class="btn btn-warning btn-sm">Lista</a>
                </td>
            </tr>
        <%
                }
            } else {
        %>
            <tr><td colspan="5" style="text-align:center; padding:24px;">No hay mesas registradas</td></tr>
        <% } %>
        </tbody>
    </table>
</div>

</body>
</html>
