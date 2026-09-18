package capaDatos;

import capaEntidad.Categoria;
import capaEntidad.Plato;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PlatoDAO {

    public List<Plato> listar() {
        List<Plato> lista = new ArrayList<>();
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL ListarPlatos()}");
            ResultSet rs = cs.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
            rs.close();
            cs.close();
        } catch (Exception e) {
            System.out.println("Error al listar platos: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return lista;
    }

    public List<Plato> listarDisponibles() {
        List<Plato> lista = new ArrayList<>();
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL ListarPlatosDisponibles()}");
            ResultSet rs = cs.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
            rs.close();
            cs.close();
        } catch (Exception e) {
            System.out.println("Error al listar platos disponibles: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return lista;
    }

    public Plato obtener(int id) {
        Plato p = null;
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL ObtenerPlato(?)}");
            cs.setInt(1, id);
            ResultSet rs = cs.executeQuery();
            if (rs.next()) p = mapear(rs);
            rs.close();
            cs.close();
        } catch (Exception e) {
            System.out.println("Error al obtener plato: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return p;
    }

    public boolean insertar(Plato p) {
        boolean ok = false;
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL InsertarPlato(?,?,?,?,?)}");
            cs.setString(1, p.getNombre());
            cs.setString(2, p.getDescripcion());
            cs.setDouble(3, p.getPrecio());
            cs.setInt(4, p.getCategoriaId());
            cs.setBoolean(5, p.isDisponible());
            cs.executeUpdate();
            cs.close();
            ok = true;
        } catch (Exception e) {
            System.out.println("Error al insertar plato: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return ok;
    }

    public boolean modificar(Plato p) {
        boolean ok = false;
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL ModificarPlato(?,?,?,?,?,?)}");
            cs.setInt(1, p.getId());
            cs.setString(2, p.getNombre());
            cs.setString(3, p.getDescripcion());
            cs.setDouble(4, p.getPrecio());
            cs.setInt(5, p.getCategoriaId());
            cs.setBoolean(6, p.isDisponible());
            cs.executeUpdate();
            cs.close();
            ok = true;
        } catch (Exception e) {
            System.out.println("Error al modificar plato: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return ok;
    }

    public boolean eliminar(int id) {
        boolean ok = false;
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL EliminarPlato(?)}");
            cs.setInt(1, id);
            cs.executeUpdate();
            cs.close();
            ok = true;
        } catch (Exception e) {
            System.out.println("Error al eliminar plato: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return ok;
    }

    public List<Categoria> listarCategorias() {
        List<Categoria> lista = new ArrayList<>();
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL ListarCategorias()}");
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Categoria c = new Categoria();
                c.setId(rs.getInt("id"));
                c.setNombre(rs.getString("nombre"));
                c.setDescripcion(rs.getString("descripcion"));
                lista.add(c);
            }
            rs.close();
            cs.close();
        } catch (Exception e) {
            System.out.println("Error al listar categorias: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return lista;
    }

    private Plato mapear(ResultSet rs) throws Exception {
        Plato p = new Plato();
        p.setId(rs.getInt("id"));
        p.setNombre(rs.getString("nombre"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setPrecio(rs.getDouble("precio"));
        p.setDisponible(rs.getBoolean("disponible"));
        p.setCategoriaId(rs.getInt("categoria_id"));
        p.setCategoriaNombre(rs.getString("categoria_nombre"));
        return p;
    }
}
