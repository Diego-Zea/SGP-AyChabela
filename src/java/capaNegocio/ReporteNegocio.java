package capaNegocio;

import capaDatos.ReporteDAO;
import capaEntidad.reporte.InsumoConsumido;
import capaEntidad.reporte.PedidoReporte;
import capaEntidad.reporte.PlatoVendido;
import capaEntidad.reporte.ResumenVentas;
import capaEntidad.reporte.VentaDia;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Reglas del modulo de Reportes.
 * Se encarga sobre todo de normalizar el rango de fechas antes de consultar:
 * si el usuario no elige nada, se asume la ultima semana; si invierte las
 * fechas, se corrigen solas.
 */
public class ReporteNegocio {

    private final ReporteDAO dao = new ReporteDAO();

    /** Rango por defecto del informe semanal: los ultimos 7 dias. */
    public static Date desdePorDefecto() {
        return Date.valueOf(LocalDate.now().minusDays(6));
    }

    public static Date hastaPorDefecto() {
        return Date.valueOf(LocalDate.now());
    }

    /**
     * Convierte el texto del formulario (yyyy-MM-dd) a fecha SQL.
     * Si viene vacio o mal escrito, devuelve el valor por defecto en vez
     * de reventar con una excepcion.
     */
    public static Date parsear(String texto, Date porDefecto) {
        if (texto == null || texto.trim().isEmpty()) return porDefecto;
        try {
            return Date.valueOf(LocalDate.parse(texto.trim()));
        } catch (Exception e) {
            return porDefecto;
        }
    }

    public ResumenVentas resumen(Date desde, Date hasta) {
        return dao.resumen(min(desde, hasta), max(desde, hasta));
    }

    public List<VentaDia> ventasPorDia(Date desde, Date hasta) {
        return dao.ventasPorDia(min(desde, hasta), max(desde, hasta));
    }

    public List<PlatoVendido> platosMasVendidos(Date desde, Date hasta) {
        return dao.platosMasVendidos(min(desde, hasta), max(desde, hasta));
    }

    public List<InsumoConsumido> insumosConsumidos(Date desde, Date hasta) {
        return dao.insumosConsumidos(min(desde, hasta), max(desde, hasta));
    }

    public List<PedidoReporte> pedidosDetallado(Date desde, Date hasta) {
        return dao.pedidosDetallado(min(desde, hasta), max(desde, hasta));
    }

    // Si el usuario puso "desde" despues de "hasta", se intercambian
    private Date min(Date a, Date b) { return a.after(b) ? b : a; }
    private Date max(Date a, Date b) { return a.after(b) ? a : b; }
}
