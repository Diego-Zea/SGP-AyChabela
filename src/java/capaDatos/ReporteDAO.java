package capaDatos;

import capaEntidad.reporte.InsumoConsumido;
import capaEntidad.reporte.PedidoReporte;
import capaEntidad.reporte.PlatoVendido;
import capaEntidad.reporte.ResumenVentas;
import capaEntidad.reporte.VentaDia;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos del modulo de Reportes.
 *
 * Todos los metodos reciben el rango de fechas y llaman a un procedimiento
 * almacenado. Ningun calculo (sumas, promedios, rankings) se hace en Java:
 * lo resuelve MySQL, que es lo que sabe hacer bien.
 */
public class ReporteDAO {

    /** Las tres cifras de la cabecera: ventas, pedidos y ticket promedio. */
    public ResumenVentas resumen(Date desde, Date hasta) {
        ResumenVentas r = new ResumenVentas();
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL ReporteResumenVentas(?,?)}");
            cs.setDate(1, desde);
            cs.setDate(2, hasta);
            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
                r.setTotalPedidos(rs.getInt("total_pedidos"));
                r.setTotalVentas(rs.getDouble("total_ventas"));
                r.setTicketPromedio(rs.getDouble("ticket_promedio"));
            }
            rs.close();
            cs.close();
        } catch (Exception e) {
            System.out.println("Error en resumen de ventas: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return r;
    }

    /** Alimenta el grafico de barras "Ventas por dia". */
    public List<VentaDia> ventasPorDia(Date desde, Date hasta) {
        List<VentaDia> lista = new ArrayList<>();
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL ReporteVentasPorDia(?,?)}");
            cs.setDate(1, desde);
            cs.setDate(2, hasta);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                VentaDia v = new VentaDia();
                v.setFecha(rs.getDate("fecha"));
                v.setPedidos(rs.getInt("pedidos"));
                v.setTotal(rs.getDouble("total"));
                lista.add(v);
            }
            rs.close();
            cs.close();
        } catch (Exception e) {
            System.out.println("Error en ventas por dia: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return lista;
    }

    /** Ranking de platos. Responde la pregunta del dueño: ¿que se vende mas? */
    public List<PlatoVendido> platosMasVendidos(Date desde, Date hasta) {
        List<PlatoVendido> lista = new ArrayList<>();
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL ReportePlatosMasVendidos(?,?)}");
            cs.setDate(1, desde);
            cs.setDate(2, hasta);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                PlatoVendido p = new PlatoVendido();
                p.setPlato(rs.getString("plato"));
                p.setCategoria(rs.getString("categoria"));
                p.setCantidad(rs.getInt("cantidad"));
                p.setTotal(rs.getDouble("total"));
                lista.add(p);
            }
            rs.close();
            cs.close();
        } catch (Exception e) {
            System.out.println("Error en platos mas vendidos: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return lista;
    }

    /** Consumo real de insumos, leido del kardex (movimiento_insumo). */
    public List<InsumoConsumido> insumosConsumidos(Date desde, Date hasta) {
        List<InsumoConsumido> lista = new ArrayList<>();
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL ReporteInsumosConsumidos(?,?)}");
            cs.setDate(1, desde);
            cs.setDate(2, hasta);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                InsumoConsumido i = new InsumoConsumido();
                i.setInsumo(rs.getString("insumo"));
                i.setUnidad(rs.getString("unidad"));
                i.setConsumido(rs.getDouble("consumido"));
                i.setStockActual(rs.getDouble("stock_actual"));
                i.setStockMinimo(rs.getDouble("stock_minimo"));
                lista.add(i);
            }
            rs.close();
            cs.close();
        } catch (Exception e) {
            System.out.println("Error en insumos consumidos: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return lista;
    }

    /** Listado plano de pedidos para la ultima hoja del Excel. */
    public List<PedidoReporte> pedidosDetallado(Date desde, Date hasta) {
        List<PedidoReporte> lista = new ArrayList<>();
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL ReportePedidosDetallado(?,?)}");
            cs.setDate(1, desde);
            cs.setDate(2, hasta);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                PedidoReporte p = new PedidoReporte();
                p.setId(rs.getInt("id"));
                p.setFechaHora(rs.getTimestamp("fecha_hora"));
                p.setMesaNumero(rs.getInt("mesa_numero"));
                p.setUsuarioNombre(rs.getString("usuario_nombre"));
                p.setEstado(rs.getString("estado"));
                p.setTotal(rs.getDouble("total"));
                lista.add(p);
            }
            rs.close();
            cs.close();
        } catch (Exception e) {
            System.out.println("Error en pedidos detallado: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return lista;
    }
}
