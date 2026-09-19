<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, capaEntidad.Plato, capaEntidad.Insumo, capaEntidad.RecetaItem" %>
<% request.setAttribute("paginaActual", "platos"); %>
<%
    Plato plato = (Plato) request.getAttribute("plato");
    List<RecetaItem> receta = (List<RecetaItem>) request.getAttribute("receta");
    List<Insumo> insumos = (List<Insumo>) request.getAttribute("insumos");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Receta - SGP Ay Chabela</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="contenedor">
    <div class="titulo-pagina">
        <span>Receta de: <%= plato == null ? "?" : plato.getNombre() %></span>
        <a href="PlatoServlet" class="btn btn-secondary">Volver a Platos</a>
    </div>

    <div class="alert alert-info">
        Indica cuanto insumo consume <strong>UNA unidad</strong> de este plato.
        Si un pedido lleva 3 platos, el sistema descontara 3 veces estas cantidades
        cuando cocina lo ponga "En preparacion".
    </div>

    <%-- ── Agregar insumo a la receta ─────────────────────────── --%>
    <div class="card">
        <h4 class="titulo-grafico">Agregar insumo</h4>
        <form action="RecetaServlet" method="post" class="filtro-fechas">
            <input type="hidden" name="platoId" value="<%= plato == null ? 0 : plato.getId() %>">

            <div class="form-group">
                <label for="insumoId">Insumo</label>
                <select id="insumoId" name="insumoId" required>
                    <% if (insumos != null) for (Insumo i : insumos) { %>
                    <option value="<%= i.getId() %>"><%= i.getNombre() %> (<%= i.getUnidad() %>)</option>
                    <% } %>
                </select>
            </div>

            <div class="form-group">
                <label for="cantidad">Cantidad por plato</label>
                <input type="number" id="cantidad" name="cantidad" step="0.001" min="0.001" required
                       placeholder="0.250">
            </div>

            <button type="submit" class="btn btn-primary">Agregar</button>
        </form>
    </div>

    <%-- ── Receta actual ──────────────────────────────────────── --%>
    <table>
        <thead>
            <tr>
                <th>Insumo</th><th>Cantidad por plato</th>
                <th>Stock disponible</th><th>Accion</th>
            </tr>
        </thead>
        <tbody>
        <%
            if (receta != null && !receta.isEmpty()) {
                for (RecetaItem r : receta) {
        %>
            <tr>
                <td><strong><%= r.getInsumoNombre() %></strong></td>
                <td><%= String.format("%.3f", r.getCantidad()) %> <%= r.getUnidad() %></td>
                <td><%= String.format("%.3f", r.getStock()) %> <%= r.getUnidad() %></td>
                <td>
                    <a href="RecetaServlet?accion=quitar&id=<%= r.getId() %>&platoId=<%= r.getPlatoId() %>"
                       class="btn btn-danger btn-sm"
                       onclick="return confirm('Quitar este insumo de la receta?');">Quitar</a>
                </td>
            </tr>
        <%      }
            } else { %>
            <tr><td colspan="4" class="vacio">
                Este plato aun no tiene receta. Sin receta no se descuenta stock
                ni aparece en el reporte de insumos consumidos.
            </td></tr>
        <% } %>
        </tbody>
    </table>
</div>

</body>
</html>
