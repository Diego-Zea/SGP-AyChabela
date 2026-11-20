<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, capaEntidad.PedidoCocina, capaEntidad.DetallePedido" %>
<% request.setAttribute("paginaActual", "cocina"); %>
<%
    List<PedidoCocina> pedidos = (List<PedidoCocina>) request.getAttribute("pedidos");
    Integer nPendientes  = (Integer) request.getAttribute("nPendientes");
    Integer nPreparacion = (Integer) request.getAttribute("nPreparacion");
    Integer nListos      = (Integer) request.getAttribute("nListos");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Panel de Cocina - SGP Ay Chabela</title>

    <meta http-equiv="refresh" content="15">

    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="contenedor">
    <div class="titulo-pagina">
        <span>Panel de Cocina</span>
        <span class="auto-refresh">Se actualiza solo cada 15 s</span>
    </div>

    <%-- ── Contadores  --%>
    <div class="contadores">
        <div class="contador contador-pendiente">
            <span class="contador-label">Pendientes</span>
            <span class="contador-num"><%= nPendientes == null ? 0 : nPendientes %></span>
        </div>
        <div class="contador contador-preparacion">
            <span class="contador-label">En preparacion</span>
            <span class="contador-num"><%= nPreparacion == null ? 0 : nPreparacion %></span>
        </div>
        <div class="contador contador-listo">
            <span class="contador-label">Listos</span>
            <span class="contador-num"><%= nListos == null ? 0 : nListos %></span>
        </div>
    </div>

    <h3 class="subtitulo">Lista de pedidos en tiempo real</h3>

    <%-- ── Tarjetas de pedido ─────────────────────────────────── --%>
    <div class="tarjetas-cocina">
    <%
        if (pedidos != null && !pedidos.isEmpty()) {
            for (PedidoCocina p : pedidos) {
                String badge = "badge-pendiente";
                if ("EN_PREPARACION".equals(p.getEstado())) badge = "badge-preparacion";
                else if ("LISTO".equals(p.getEstado()))     badge = "badge-listo";
    %>
        <div class="tarjeta-pedido <%= p.getColorTiempo() %>">

            <div class="tarjeta-cab">
                <span class="mesa">Mesa <%= p.getMesaNumero() %></span>
                <span class="minutos"><%= p.getMinutos() %> min</span>
            </div>

            <div class="tarjeta-sub">
                Pedido #<%= p.getId() %> &middot; <%= p.getUsuarioNombre() %>
            </div>

            <ul class="lista-platos">
            <% for (DetallePedido d : p.getItems()) { %>
                <li><strong><%= d.getCantidad() %>x</strong> <%= d.getPlatoNombre() %></li>
            <% } %>
            </ul>

            <% if (p.getNotas() != null && !p.getNotas().trim().isEmpty()) { %>
            <div class="nota-pedido">Nota: <%= p.getNotas() %></div>
            <% } %>

            <div class="tarjeta-estado">
                Estado: <span class="badge <%= badge %>"><%= p.getEstado() %></span>
            </div>

            <%-- El cambio de estado va por POST, no por enlace GET, porque
                 descuenta insumos del almacen y no debe repetirse al recargar. --%>
            <form action="CocinaServlet" method="post" class="form-accion">
                <input type="hidden" name="id"     value="<%= p.getId() %>">
                <input type="hidden" name="estado" value="<%= p.getSiguienteEstado() %>">
                <button type="submit" class="btn btn-success btn-block">
                    <%= p.getTextoBoton() %>
                </button>
            </form>

            <% if ("PENDIENTE".equals(p.getEstado())) { %>
            <form action="CocinaServlet" method="post" class="form-accion">
                <input type="hidden" name="id"     value="<%= p.getId() %>">
                <input type="hidden" name="accion" value="cancelar">
                <button type="submit" class="btn btn-secondary btn-block btn-sm"
                        onclick="return confirm('Cancelar el pedido #<%= p.getId() %>?');">
                    Cancelar pedido
                </button>
            </form>
            <% } %>

            <div class="tarjeta-total">Total: <strong>S/ <%= String.format("%.2f", p.getTotal()) %></strong></div>
        </div>
    <%
            }
        } else {
    %>
        <div class="sin-pedidos">
            <p>No hay pedidos activos en este momento.</p>
            <p class="pista">Cuando un mesero registre un pedido, aparecera aqui automaticamente.</p>
        </div>
    <% } %>
    </div>
</div>

</body>
</html>
