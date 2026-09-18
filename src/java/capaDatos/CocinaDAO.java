package capaDatos;

import capaEntidad.DetallePedido;
import capaEntidad.PedidoCocina;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Acceso a datos del Panel de Cocina.
 *
 * Detalle importante: en lugar de pedir el detalle de cada pedido por
 * separado (1 consulta por tarjeta = problema N+1), se traen los pedidos
 * en una consulta y TODOS sus detalles en otra, y se unen en memoria.
 * Con eso el panel siempre hace 2 consultas, tenga 3 o 30 pedidos.
 */
public class CocinaDAO {

    public List<PedidoCocina> listarPedidosActivos() {
        // LinkedHashMap: conserva el orden por urgencia que impone el SP
        Map<Integer, PedidoCocina> mapa = new LinkedHashMap<>();
        Connection con = ConexionBD.obtener();
        try {
            // ── 1) Cabeceras de los pedidos activos ──
            CallableStatement cs = con.prepareCall("{CALL ListarPedidosCocina()}");
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                PedidoCocina p = new PedidoCocina();
                p.setId(rs.getInt("id"));
                p.setFechaHora(rs.getTimestamp("fecha_hora"));
                p.setEstado(rs.getString("estado"));
                p.setTotal(rs.getDouble("total"));
                p.setNotas(rs.getString("notas"));
                p.setMesaNumero(rs.getInt("mesa_numero"));
                p.setUsuarioNombre(rs.getString("usuario_nombre"));
                p.setMinutos(rs.getInt("minutos"));
                mapa.put(p.getId(), p);
            }
            rs.close();
            cs.close();

            // ── 2) Detalle de todos ellos de una sola vez ──
            CallableStatement cs2 = con.prepareCall("{CALL ListarDetallePedidosCocina()}");
            ResultSet rs2 = cs2.executeQuery();
            while (rs2.next()) {
                int pedidoId = rs2.getInt("pedido_id");
                PedidoCocina p = mapa.get(pedidoId);
                if (p != null) {
                    DetallePedido d = new DetallePedido();
                    d.setCantidad(rs2.getInt("cantidad"));
                    d.setPlatoNombre(rs2.getString("plato_nombre"));
                    p.getItems().add(d);
                }
            }
            rs2.close();
            cs2.close();

        } catch (Exception e) {
            System.out.println("Error al listar pedidos de cocina: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return new ArrayList<>(mapa.values());
    }
}
