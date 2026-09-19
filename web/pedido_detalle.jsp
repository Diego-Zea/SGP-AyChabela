<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, capaEntidad.Pedido, capaEntidad.DetallePedido, java.text.SimpleDateFormat" %>
<% request.setAttribute("paginaActual", "pedidos"); %>
<%
    Pedido pe = (Pedido) request.getAttribute("pedido");
    List<DetallePedido> detalle = (List<DetallePedido>) request.getAttribute("detalle");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Detalle de Pedido - SGP Ay Chabela</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="contenedor">
    <h2 class="titulo-pagina">Detalle del Pedido #<%= pe == null ? "" : pe.getId() %></h2>

    <% if (pe != null) {
           String badge = "badge-pendiente";
           if ("EN_PREPARACION".equals(pe.getEstado())) badge = "badge-preparacion";
           else if ("LISTO".equals(pe.getEstado()))     badge = "badge-listo";
           else if ("ENTREGADO".equals(pe.getEstado())) badge = "badge-entregado";
           else if ("CERRADO".equals(pe.getEstado()))   badge = "badge-entregado";
           else if ("CANCELADO".equals(pe.getEstado())) badge = "badge-cancelado";
    %>

    <div class="card">
        <div class="form-grid-2">
            <div>
                <p><strong>Mesa:</strong> #<%= pe.getMesaNumero() %></p>
                <p><strong>Mesero:</strong> <%= pe.getUsuarioNombre() %></p>
                <p><strong>Fecha:</strong> <%= pe.getFechaHora() != null ? sdf.format(pe.getFechaHora()) : "" %></p>
            </div>
            <div>
                <p><strong>Estado:</strong> <span class="badge <%= badge %>"><%= pe.getEstado() %></span></p>
                <p><strong>Total:</strong> S/ <%= String.format("%.2f", pe.getTotal()) %></p>
                <p><strong>Notas:</strong> <%= pe.getNotas() == null ? "(ninguna)" : pe.getNotas() %></p>
            </div>
        </div>
    </div>

    <h3 style="margin: 20px 0 10px; color: #1A1A2E;">Platos del pedido</h3>
    <table>
        <thead>
            <tr>
                <th>Plato</th>
                <th>Cantidad</th>
                <th>Precio unit.</th>
                <th>Subtotal</th>
            </tr>
        </thead>
        <tbody>
        <%
            if (detalle != null && !detalle.isEmpty()) {
                for (DetallePedido d : detalle) {
        %>
            <tr>
                <td><%= d.getPlatoNombre() %></td>
                <td><%= d.getCantidad() %></td>
                <td>S/ <%= String.format("%.2f", d.getPrecioUnitario()) %></td>
                <td>S/ <%= String.format("%.2f", d.getSubtotal()) %></td>
            </tr>
        <%
                }
            } else {
        %>
            <tr><td colspan="4" style="text-align:center; padding:18px;">Este pedido no tiene platos</td></tr>
        <% } %>
        </tbody>
    </table>

    <div class="total-box">TOTAL: S/ <%= String.format("%.2f", pe.getTotal()) %></div>

    <div class="card" style="margin-top:20px;">
        <h3 style="margin-bottom:12px; color:#1A1A2E;">Cambiar estado del pedido</h3>
        <div style="display:flex; gap:8px; flex-wrap:wrap;">
            <a href="PedidoServlet?accion=cambiarEstado&id=<%= pe.getId() %>&estado=PENDIENTE"      class="btn btn-warning btn-sm">Pendiente</a>
            <a href="PedidoServlet?accion=cambiarEstado&id=<%= pe.getId() %>&estado=EN_PREPARACION" class="btn btn-primary btn-sm">En preparacion</a>
            <a href="PedidoServlet?accion=cambiarEstado&id=<%= pe.getId() %>&estado=LISTO"          class="btn btn-success btn-sm">Listo</a>
            <a href="PedidoServlet?accion=cambiarEstado&id=<%= pe.getId() %>&estado=ENTREGADO"      class="btn btn-secondary btn-sm">Entregado</a>
            <a href="PedidoServlet?accion=cambiarEstado&id=<%= pe.getId() %>&estado=CERRADO"        class="btn btn-secondary btn-sm">Cerrado</a>
            <a href="PedidoServlet?accion=cambiarEstado&id=<%= pe.getId() %>&estado=CANCELADO"      class="btn btn-danger btn-sm">Cancelar</a>
        </div>
    </div>

    <div style="margin-top:20px;">
        <a href="PedidoServlet" class="btn btn-secondary">&larr; Volver a la lista</a>
    </div>

    <% } else { %>
        <div class="alert alert-error">Pedido no encontrado.</div>
        <a href="PedidoServlet" class="btn btn-secondary">Volver</a>
    <% } %>
</div>

</body>
</html>
