<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, capaEntidad.Pedido, java.text.SimpleDateFormat" %>
<% request.setAttribute("paginaActual", "pedidos"); %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Pedidos - SGP Ay Chabela</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="contenedor">
    <div class="titulo-pagina">
        <span>Gestion de Pedidos</span>
        <a href="PedidoServlet?accion=nuevo" class="btn btn-primary">+ Nuevo Pedido</a>
    </div>

    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Fecha</th>
                <th>Mesa</th>
                <th>Mesero</th>
                <th>Total</th>
                <th>Estado</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
        <%
            List<Pedido> pedidos = (List<Pedido>) request.getAttribute("pedidos");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            if (pedidos != null && !pedidos.isEmpty()) {
                for (Pedido pe : pedidos) {
                    String badge = "badge-pendiente";
                    if ("EN_PREPARACION".equals(pe.getEstado())) badge = "badge-preparacion";
                    else if ("LISTO".equals(pe.getEstado()))     badge = "badge-listo";
                    else if ("ENTREGADO".equals(pe.getEstado())) badge = "badge-entregado";
                    else if ("CERRADO".equals(pe.getEstado()))   badge = "badge-entregado";
                    else if ("CANCELADO".equals(pe.getEstado())) badge = "badge-cancelado";
        %>
            <tr>
                <td><strong>#<%= pe.getId() %></strong></td>
                <td><%= pe.getFechaHora() != null ? sdf.format(pe.getFechaHora()) : "" %></td>
                <td>Mesa <%= pe.getMesaNumero() %></td>
                <td><%= pe.getUsuarioNombre() %></td>
                <td><strong>S/ <%= String.format("%.2f", pe.getTotal()) %></strong></td>
                <td><span class="badge <%= badge %>"><%= pe.getEstado() %></span></td>
                <td>
                    <a href="PedidoServlet?accion=ver&id=<%= pe.getId() %>" class="btn btn-primary btn-sm">Ver detalle</a>
                </td>
            </tr>
        <%
                }
            } else {
        %>
            <tr><td colspan="7" style="text-align:center; padding:24px;">No hay pedidos registrados</td></tr>
        <% } %>
        </tbody>
    </table>
</div>

</body>
</html>
