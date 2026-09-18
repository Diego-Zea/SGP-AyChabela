package capaDatos;

import capaEntidad.Insumo;
import capaEntidad.RecetaItem;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos del modulo de Insumos.
 * Sigue el mismo patron que los DAO ya existentes: toda la logica SQL vive
 * en procedimientos almacenados y aqui solo se llaman con CallableStatement.
 */
public class InsumoDAO {

    // ── CRUD de insumos ──────────────────────────────────────────

    public List<Insumo> listar() {
        List<Insumo> lista = new ArrayList<>();
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL ListarInsumos()}");
            ResultSet rs = cs.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
            rs.close();
            cs.close();
        } catch (Exception e) {
            System.out.println("Error al listar insumos: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return lista;
    }

    /** Solo los activos: es la lista que se ofrece al armar una receta. */
    public List<Insumo> listarActivos() {
        List<Insumo> lista = new ArrayList<>();
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL ListarInsumosActivos()}");
            ResultSet rs = cs.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
            rs.close();
            cs.close();
        } catch (Exception e) {
            System.out.println("Error al listar insumos activos: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return lista;
    }

    public Insumo obtener(int id) {
        Insumo i = null;
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL ObtenerInsumo(?)}");
            cs.setInt(1, id);
            ResultSet rs = cs.executeQuery();
            if (rs.next()) i = mapear(rs);
            rs.close();
            cs.close();
        } catch (Exception e) {
            System.out.println("Error al obtener insumo: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return i;
    }

    public boolean insertar(Insumo i) {
        boolean ok = false;
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL InsertarInsumo(?,?,?,?)}");
            cs.setString(1, i.getNombre());
            cs.setString(2, i.getUnidad());
            cs.setDouble(3, i.getStock());
            cs.setDouble(4, i.getStockMinimo());
            cs.executeUpdate();
            cs.close();
            ok = true;
        } catch (Exception e) {
            System.out.println("Error al insertar insumo: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return ok;
    }

    public boolean modificar(Insumo i) {
        boolean ok = false;
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL ModificarInsumo(?,?,?,?,?,?)}");
            cs.setInt(1, i.getId());
            cs.setString(2, i.getNombre());
            cs.setString(3, i.getUnidad());
            cs.setDouble(4, i.getStock());
            cs.setDouble(5, i.getStockMinimo());
            cs.setBoolean(6, i.isActivo());
            cs.executeUpdate();
            cs.close();
            ok = true;
        } catch (Exception e) {
            System.out.println("Error al modificar insumo: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return ok;
    }

    /** Baja logica (activo = 0): el kardex sigue apuntando a este insumo. */
    public boolean eliminar(int id) {
        boolean ok = false;
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL EliminarInsumo(?)}");
            cs.setInt(1, id);
            cs.executeUpdate();
            cs.close();
            ok = true;
        } catch (Exception e) {
            System.out.println("Error al eliminar insumo: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return ok;
    }

    /** Compra o reposicion: suma stock y deja el movimiento en el kardex. */
    public boolean registrarEntrada(int id, double cantidad, String obs) {
        boolean ok = false;
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL RegistrarEntradaInsumo(?,?,?)}");
            cs.setInt(1, id);
            cs.setDouble(2, cantidad);
            cs.setString(3, obs);
            cs.executeUpdate();
            cs.close();
            ok = true;
        } catch (Exception e) {
            System.out.println("Error al registrar entrada de insumo: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return ok;
    }

    // ── Receta (plato_insumo) ────────────────────────────────────

    public List<RecetaItem> listarReceta(int platoId) {
        List<RecetaItem> lista = new ArrayList<>();
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL ListarRecetaPlato(?)}");
            cs.setInt(1, platoId);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                RecetaItem r = new RecetaItem();
                r.setId(rs.getInt("id"));
                r.setPlatoId(rs.getInt("plato_id"));
                r.setInsumoId(rs.getInt("insumo_id"));
                r.setCantidad(rs.getDouble("cantidad"));
                r.setInsumoNombre(rs.getString("insumo_nombre"));
                r.setUnidad(rs.getString("unidad"));
                r.setStock(rs.getDouble("stock"));
                lista.add(r);
            }
            rs.close();
            cs.close();
        } catch (Exception e) {
            System.out.println("Error al listar receta: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return lista;
    }

    public boolean agregarInsumoAPlato(int platoId, int insumoId, double cantidad) {
        boolean ok = false;
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL AgregarInsumoAPlato(?,?,?)}");
            cs.setInt(1, platoId);
            cs.setInt(2, insumoId);
            cs.setDouble(3, cantidad);
            cs.executeUpdate();
            cs.close();
            ok = true;
        } catch (Exception e) {
            System.out.println("Error al agregar insumo al plato: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return ok;
    }

    public boolean eliminarInsumoDePlato(int id) {
        boolean ok = false;
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL EliminarInsumoDePlato(?)}");
            cs.setInt(1, id);
            cs.executeUpdate();
            cs.close();
            ok = true;
        } catch (Exception e) {
            System.out.println("Error al eliminar insumo del plato: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return ok;
    }

    // ── Utilitario ───────────────────────────────────────────────

    private Insumo mapear(ResultSet rs) throws Exception {
        Insumo i = new Insumo();
        i.setId(rs.getInt("id"));
        i.setNombre(rs.getString("nombre"));
        i.setUnidad(rs.getString("unidad"));
        i.setStock(rs.getDouble("stock"));
        i.setStockMinimo(rs.getDouble("stock_minimo"));
        i.setActivo(rs.getBoolean("activo"));
        return i;
    }
}
