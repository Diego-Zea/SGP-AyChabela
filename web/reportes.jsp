<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.text.SimpleDateFormat, java.sql.Date" %>
<%@ page import="capaEntidad.reporte.ResumenVentas, capaEntidad.reporte.VentaDia" %>
<%@ page import="capaEntidad.reporte.PlatoVendido, capaEntidad.reporte.InsumoConsumido" %>
<% request.setAttribute("paginaActual", "reportes"); %>
<%
    ResumenVentas resumen      = (ResumenVentas) request.getAttribute("resumen");
    List<VentaDia> ventas      = (List<VentaDia>) request.getAttribute("ventas");
    List<PlatoVendido> platos  = (List<PlatoVendido>) request.getAttribute("platos");
    List<InsumoConsumido> insumos = (List<InsumoConsumido>) request.getAttribute("insumos");
    Date desde = (Date) request.getAttribute("desde");
    Date hasta = (Date) request.getAttribute("hasta");

    SimpleDateFormat fmtInput = new SimpleDateFormat("yyyy-MM-dd");  // para <input type=date>
    SimpleDateFormat fmtDia   = new SimpleDateFormat("dd/MM");

    // Los graficos necesitan los datos como arreglos de JavaScript.
    // Se arman aqui, en el servidor, recorriendo las listas una sola vez.
    StringBuilder labelsDias = new StringBuilder();
    StringBuilder totalesDias = new StringBuilder();
    if (ventas != null) {
        for (int i = 0; i < ventas.size(); i++) {
            if (i > 0) { labelsDias.append(","); totalesDias.append(","); }
            labelsDias.append("\"").append(fmtDia.format(ventas.get(i).getFecha())).append("\"");
            totalesDias.append(ventas.get(i).getTotal());
        }
    }

    // Para el grafico de platos solo se toman los 5 primeros del ranking
    StringBuilder labelsPlatos = new StringBuilder();
    StringBuilder cantPlatos = new StringBuilder();
    if (platos != null) {
        int max = Math.min(5, platos.size());
        for (int i = 0; i < max; i++) {
            if (i > 0) { labelsPlatos.append(","); cantPlatos.append(","); }
            String nom = platos.get(i).getPlato().replace("\"", "");
            labelsPlatos.append("\"").append(nom).append("\"");
            cantPlatos.append(platos.get(i).getCantidad());
        }
    }

    String queryFechas = "desde=" + fmtInput.format(desde) + "&hasta=" + fmtInput.format(hasta);
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Reportes - SGP Ay Chabela</title>
    <link rel="stylesheet" href="css/style.css">
    <%-- Chart.js va incluido en el proyecto (web/js), NO se carga de internet.
         Asi los graficos funcionan aunque el restaurante se quede sin conexion
         o el CDN este bloqueado por la red del local. --%>
    <script src="js/chart.umd.min.js"></script>
</head>
<body>

<jsp:include page="header.jsp" />

<div class="contenedor">
    <div class="titulo-pagina">
        <span>Reportes de Ventas</span>
        <%-- Mismo servlet, misma consulta, otra salida: accion=excel --%>
        <a href="ReporteServlet?accion=excel&<%= queryFechas %>" class="btn btn-success">
            &#128202; Descargar Excel
        </a>
    </div>

    <%-- ── Filtro de fechas ───────────────────────────────────── --%>
    <div class="card">
        <form action="ReporteServlet" method="get" class="filtro-fechas">
            <div class="form-group">
                <label for="desde">Desde</label>
                <input type="date" id="desde" name="desde" value="<%= fmtInput.format(desde) %>">
            </div>
            <div class="form-group">
                <label for="hasta">Hasta</label>
                <input type="date" id="hasta" name="hasta" value="<%= fmtInput.format(hasta) %>">
            </div>
            <button type="submit" class="btn btn-primary">Consultar</button>
        </form>
    </div>

    <%-- ── Resumen semanal ────────────────────────────────────── --%>
    <h3 class="subtitulo">Resumen del periodo</h3>
    <div class="contadores">
        <div class="contador contador-listo">
            <span class="contador-label">Ventas totales</span>
            <span class="contador-num">S/ <%= String.format("%.2f", resumen.getTotalVentas()) %></span>
        </div>
        <div class="contador contador-preparacion">
            <span class="contador-label">Total pedidos</span>
            <span class="contador-num"><%= resumen.getTotalPedidos() %></span>
        </div>
        <div class="contador contador-pendiente">
            <span class="contador-label">Ticket promedio</span>
            <span class="contador-num">S/ <%= String.format("%.2f", resumen.getTicketPromedio()) %></span>
        </div>
    </div>

    <%-- ── Graficos ───────────────────────────────────────────── --%>
    <h3 class="subtitulo">Graficos</h3>
    <div class="grid-graficos">
        <div class="card">
            <h4 class="titulo-grafico">Ventas por dia</h4>
            <canvas id="graficoVentas" height="200"></canvas>
        </div>
        <div class="card">
            <h4 class="titulo-grafico">Top 5 platos mas vendidos</h4>
            <canvas id="graficoPlatos" height="200"></canvas>
        </div>
    </div>

    <%-- ── Ranking de platos ──────────────────────────────────── --%>
    <h3 class="subtitulo">Platos mas vendidos</h3>
    <table>
        <thead>
            <tr>
                <th>#</th><th>Plato</th><th>Categoria</th>
                <th>Cantidad</th><th>Total vendido</th>
            </tr>
        </thead>
        <tbody>
        <%
            if (platos != null && !platos.isEmpty()) {
                int pos = 1;
                for (PlatoVendido p : platos) {
        %>
            <tr>
                <td><strong><%= pos++ %></strong></td>
                <td><%= p.getPlato() %></td>
                <td><%= p.getCategoria() %></td>
                <td><%= p.getCantidad() %></td>
                <td><strong>S/ <%= String.format("%.2f", p.getTotal()) %></strong></td>
            </tr>
        <%      }
            } else { %>
            <tr><td colspan="5" class="vacio">No hay ventas en el periodo seleccionado</td></tr>
        <% } %>
        </tbody>
    </table>

    <%-- ── Insumos consumidos ─────────────────────────────────── --%>
    <h3 class="subtitulo">Insumos utilizados</h3>
    <table>
        <thead>
            <tr>
                <th>Insumo</th><th>Consumido</th><th>Stock actual</th>
                <th>Stock minimo</th><th>Estado</th>
            </tr>
        </thead>
        <tbody>
        <%
            if (insumos != null && !insumos.isEmpty()) {
                for (InsumoConsumido i : insumos) {
        %>
            <tr>
                <td><%= i.getInsumo() %></td>
                <td><strong><%= String.format("%.3f", i.getConsumido()) %></strong> <%= i.getUnidad() %></td>
                <td><%= String.format("%.3f", i.getStockActual()) %> <%= i.getUnidad() %></td>
                <td><%= String.format("%.3f", i.getStockMinimo()) %> <%= i.getUnidad() %></td>
                <td>
                    <% if (i.isStockBajo()) { %>
                        <span class="badge badge-cancelado">Reponer</span>
                    <% } else { %>
                        <span class="badge badge-disponible">OK</span>
                    <% } %>
                </td>
            </tr>
        <%      }
            } else { %>
            <tr><td colspan="5" class="vacio">
                Sin consumo registrado. Verifica que los platos tengan receta cargada
                y que los pedidos hayan pasado por "En preparacion".
            </td></tr>
        <% } %>
        </tbody>
    </table>
</div>

<script>
    // ── Grafico de barras: ventas por dia ──
    new Chart(document.getElementById('graficoVentas'), {
        type: 'bar',
        data: {
            labels: [<%= labelsDias %>],
            datasets: [{
                label: 'Ventas (S/)',
                data: [<%= totalesDias %>],
                backgroundColor: '#E94560'
            }]
        },
        options: {
            responsive: true,
            plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: true } }
        }
    });

    // ── Grafico horizontal: ranking de platos ──
    new Chart(document.getElementById('graficoPlatos'), {
        type: 'bar',
        data: {
            labels: [<%= labelsPlatos %>],
            datasets: [{
                label: 'Cantidad vendida',
                data: [<%= cantPlatos %>],
                backgroundColor: '#0F3460'
            }]
        },
        options: {
            indexAxis: 'y',
            responsive: true,
            plugins: { legend: { display: false } },
            scales: { x: { beginAtZero: true } }
        }
    });
</script>

</body>
</html>
