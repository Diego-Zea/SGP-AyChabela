package servlets;

import capaEntidad.Usuario;
import capaEntidad.reporte.InsumoConsumido;
import capaEntidad.reporte.PedidoReporte;
import capaEntidad.reporte.PlatoVendido;
import capaEntidad.reporte.ResumenVentas;
import capaEntidad.reporte.VentaDia;
import capaNegocio.ReporteNegocio;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.ExcelBuilder;

/**
 * MODULO DE REPORTES DE VENTAS.
 * Acceso: solo ADMINISTRADOR. Es informacion del negocio (cuanto se vendio),
 * no operativa, asi que ni meseros ni cocina la ven.
 */
@WebServlet(name = "ReporteServlet", urlPatterns = {"/ReporteServlet"})
public class ReporteServlet extends HttpServlet {

    private final ReporteNegocio negocio = new ReporteNegocio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Usuario actual = Acceso.actual(req);
        if (actual == null) { resp.sendRedirect("login.jsp"); return; }

        // Reportes: SOLO el administrador
        if (!Acceso.tieneRol(actual, "ADMIN")) {
            resp.sendRedirect("menu.jsp");
            return;
        }

        // Rango de fechas: si no viene nada, la ultima semana
        Date desde = ReporteNegocio.parsear(req.getParameter("desde"), ReporteNegocio.desdePorDefecto());
        Date hasta = ReporteNegocio.parsear(req.getParameter("hasta"), ReporteNegocio.hastaPorDefecto());

        if ("excel".equals(req.getParameter("accion"))) {
            exportarExcel(req, resp, desde, hasta);
            return;
        }

        // ── Pantalla ──
        req.setAttribute("desde",   desde);
        req.setAttribute("hasta",   hasta);
        req.setAttribute("resumen", negocio.resumen(desde, hasta));
        req.setAttribute("ventas",  negocio.ventasPorDia(desde, hasta));
        req.setAttribute("platos",  negocio.platosMasVendidos(desde, hasta));
        req.setAttribute("insumos", negocio.insumosConsumidos(desde, hasta));
        req.getRequestDispatcher("reportes.jsp").forward(req, resp);
    }

    // ════════════════════════════════════════════════════════════
    //  EXPORTACION A EXCEL
    // ════════════════════════════════════════════════════════════

    /**
     * Arma un libro de 5 hojas y lo escribe directamente en la respuesta HTTP.
     * El archivo nunca se guarda en el disco del servidor: se genera en memoria
     * y viaja al navegador.
     */
    private void exportarExcel(HttpServletRequest req, HttpServletResponse resp,
                               Date desde, Date hasta) throws IOException {

        SimpleDateFormat fmtDia   = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat fmtHora  = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        SimpleDateFormat fmtArch  = new SimpleDateFormat("yyyyMMdd");
        String periodo = fmtDia.format(desde) + " al " + fmtDia.format(hasta);

        ExcelBuilder libro = new ExcelBuilder();

        // ── HOJA 1: Resumen ──
        ResumenVentas r = negocio.resumen(desde, hasta);
        ExcelBuilder.Hoja h1 = libro.nuevaHoja("Resumen");
        h1.anchos(30, 20, 20);
        h1.titulo("REPORTE DE VENTAS - RESTAURANTE AY CHABELA");
        h1.linea("Periodo: " + periodo);
        h1.linea("Generado por: " + Acceso.actual(req).getNombre()
                 + "  |  " + fmtHora.format(new java.util.Date()));
        h1.blanco();
        h1.cabecera("Indicador", "Valor", "Detalle");
        h1.fila("Ventas totales",   ExcelBuilder.soles(r.getTotalVentas()),   "No incluye pedidos cancelados");
        h1.fila("Total de pedidos", r.getTotalPedidos(),                      "Pedidos atendidos en el periodo");
        h1.fila("Ticket promedio",  ExcelBuilder.soles(r.getTicketPromedio()), "Venta media por pedido");

        // ── HOJA 2: Ventas por dia ──
        List<VentaDia> ventas = negocio.ventasPorDia(desde, hasta);
        ExcelBuilder.Hoja h2 = libro.nuevaHoja("Ventas por dia");
        h2.anchos(16, 12, 18);
        h2.titulo("VENTAS POR DIA");
        h2.linea("Periodo: " + periodo);
        h2.blanco();
        h2.cabecera("Fecha", "Pedidos", "Total vendido");
        double acumulado = 0;
        for (VentaDia v : ventas) {
            h2.fila(fmtDia.format(v.getFecha()), v.getPedidos(), ExcelBuilder.soles(v.getTotal()));
            acumulado += v.getTotal();
        }
        h2.blanco();
        h2.fila("TOTAL", null, ExcelBuilder.soles(acumulado));

        // ── HOJA 3: Platos mas vendidos ──
        List<PlatoVendido> platos = negocio.platosMasVendidos(desde, hasta);
        ExcelBuilder.Hoja h3 = libro.nuevaHoja("Platos mas vendidos");
        h3.anchos(32, 20, 12, 18);
        h3.titulo("RANKING DE PLATOS MAS VENDIDOS");
        h3.linea("Periodo: " + periodo);
        h3.blanco();
        h3.cabecera("Plato", "Categoria", "Cantidad", "Total vendido");
        for (PlatoVendido p : platos) {
            h3.fila(p.getPlato(), p.getCategoria(), p.getCantidad(), ExcelBuilder.soles(p.getTotal()));
        }
        if (platos.isEmpty()) h3.linea("(No hay ventas registradas en este periodo)");

        // ── HOJA 4: Insumos consumidos ──
        List<InsumoConsumido> insumos = negocio.insumosConsumidos(desde, hasta);
        ExcelBuilder.Hoja h4 = libro.nuevaHoja("Insumos consumidos");
        h4.anchos(28, 10, 16, 16, 16, 14);
        h4.titulo("INSUMOS CONSUMIDOS");
        h4.linea("Periodo: " + periodo + "  |  Calculado desde el kardex (movimiento_insumo)");
        h4.blanco();
        h4.cabecera("Insumo", "Unidad", "Consumido", "Stock actual", "Stock minimo", "Estado");
        for (InsumoConsumido i : insumos) {
            h4.fila(i.getInsumo(), i.getUnidad(), i.getConsumido(),
                    i.getStockActual(), i.getStockMinimo(),
                    i.isStockBajo() ? "REPONER" : "OK");
        }
        if (insumos.isEmpty()) h4.linea("(Sin consumo registrado: revisa que los platos tengan receta)");

        // ── HOJA 5: Detalle de pedidos ──
        List<PedidoReporte> pedidos = negocio.pedidosDetallado(desde, hasta);
        ExcelBuilder.Hoja h5 = libro.nuevaHoja("Detalle de pedidos");
        h5.anchos(10, 20, 10, 26, 18, 16);
        h5.titulo("DETALLE DE PEDIDOS");
        h5.linea("Periodo: " + periodo);
        h5.blanco();
        h5.cabecera("N. Pedido", "Fecha y hora", "Mesa", "Mesero", "Estado", "Total");
        for (PedidoReporte p : pedidos) {
            h5.fila(p.getId(),
                    p.getFechaHora() == null ? "" : fmtHora.format(p.getFechaHora()),
                    p.getMesaNumero(),
                    p.getUsuarioNombre(),
                    p.getEstado(),
                    ExcelBuilder.soles(p.getTotal()));
        }

        // ── Cabeceras HTTP: le dicen al navegador que descargue el archivo ──
        String nombreArchivo = "Reporte_Ventas_AyChabela_"
                             + fmtArch.format(desde) + "_" + fmtArch.format(hasta) + ".xlsx";

        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"");
        resp.setHeader("Cache-Control", "no-cache");

        OutputStream salida = resp.getOutputStream();
        libro.escribir(salida);
        salida.flush();
    }
}
