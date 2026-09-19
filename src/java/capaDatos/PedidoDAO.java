package capaDatos;

import capaEntidad.DetallePedido;
import capaEntidad.Pedido;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    /**
     * Registra un nuevo pedido y agrega su lista de detalles.
     * Devuelve el id generado, o -1 si hubo error.
     */
    public int registrar(Pedido p, List<DetallePedido> items) {
        int idGenerado = -1;
        Connection con = ConexionBD.obtener();
        try {
            // 1) Crear cabecera del pedido
            CallableStatement cs = con.prepareCall("{CALL RegistrarPedido(?,?,?,?)}");
            cs.setInt(1, p.getMesaId());
            cs.setInt(2, p.getUsuarioId());
            cs.setString(3, p.getNotas());
            cs.registerOutParameter(4, Types.INTEGER);
            cs.executeUpdate();
            idGenerado = cs.getInt(4);
            cs.close();

            // 2) Agregar cada detalle
            if (idGenerado > 0 && items != null) {
                for (DetallePedido d : items) {
                    CallableStatement cs2 = con.prepareCall("{CALL AgregarDetallePedido(?,?,?)}");
                    cs2.setInt(1, idGenerado);
                    cs2.setInt(2, d.getPlatoId());
                    cs2.setInt(3, d.getCantidad());
                    cs2.executeUpdate();
                    cs2.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Error al registrar pedido: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return idGenerado;
    }

    public List<Pedido> listar() {
        List<Pedido> lista = new ArrayList<>();
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL ListarPedidos()}");
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Pedido p = new Pedido();
                p.setId(rs.getInt("id"));
                p.setFechaHora(rs.getTimestamp("fecha_hora"));
                p.setEstado(rs.getString("estado"));
                p.setTotal(rs.getDouble("total"));
                p.setNotas(rs.getString("notas"));
                p.setMesaNumero(rs.getInt("mesa_numero"));
                p.setUsuarioNombre(rs.getString("usuario_nombre"));
                lista.add(p);
            }
            rs.close();
            cs.close();
        } catch (Exception e) {
            System.out.println("Error al listar pedidos: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return lista;
    }

    public Pedido obtener(int id) {
        Pedido p = null;
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL ObtenerPedido(?)}");
            cs.setInt(1, id);
            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
                p = new Pedido();
                p.setId(rs.getInt("id"));
                p.setFechaHora(rs.getTimestamp("fecha_hora"));
                p.setEstado(rs.getString("estado"));
                p.setTotal(rs.getDouble("total"));
                p.setNotas(rs.getString("notas"));
                p.setMesaId(rs.getInt("mesa_id"));
                p.setMesaNumero(rs.getInt("mesa_numero"));
                p.setUsuarioId(rs.getInt("usuario_id"));
                p.setUsuarioNombre(rs.getString("usuario_nombre"));
            }
            rs.close();
            cs.close();
        } catch (Exception e) {
            System.out.println("Error al obtener pedido: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return p;
    }

    public List<DetallePedido> listarDetalle(int pedidoId) {
        List<DetallePedido> lista = new ArrayList<>();
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL ListarDetallePedido(?)}");
            cs.setInt(1, pedidoId);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                DetallePedido d = new DetallePedido();
                d.setId(rs.getInt("id"));
                d.setCantidad(rs.getInt("cantidad"));
                d.setPrecioUnitario(rs.getDouble("precio_unitario"));
                d.setSubtotal(rs.getDouble("subtotal"));
                d.setPlatoId(rs.getInt("plato_id"));
                d.setPlatoNombre(rs.getString("plato_nombre"));
                lista.add(d);
            }
            rs.close();
            cs.close();
        } catch (Exception e) {
            System.out.println("Error al listar detalle: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return lista;
    }

    public boolean cambiarEstado(int id, String estado) {
        boolean ok = false;
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL CambiarEstadoPedido(?,?)}");
            cs.setInt(1, id);
            cs.setString(2, estado);
            cs.executeUpdate();
            cs.close();
            ok = true;
        } catch (Exception e) {
            System.out.println("Error al cambiar estado de pedido: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return ok;
    }
}
