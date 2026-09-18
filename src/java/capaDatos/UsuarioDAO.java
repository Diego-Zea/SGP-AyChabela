package capaDatos;

import capaEntidad.Usuario;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public Usuario login(String usuario, String password) {
        Usuario u = null;
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL LoginUsuario(?,?)}");
            cs.setString(1, usuario);
            cs.setString(2, password);
            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
                u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setDni(rs.getString("dni"));
                u.setNombres(rs.getString("nombres"));
                u.setApellidos(rs.getString("apellidos"));
                u.setNombre(rs.getString("nombre"));
                u.setUsuario(rs.getString("usuario"));
                u.setRol(rs.getString("rol"));
                u.setActivo(rs.getBoolean("activo"));
            }
            rs.close();
            cs.close();
        } catch (Exception e) {
            System.out.println("Error en login: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return u;
    }

    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL ListarUsuarios()}");
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setDni(rs.getString("dni"));
                u.setNombres(rs.getString("nombres"));
                u.setApellidos(rs.getString("apellidos"));
                u.setNombre(rs.getString("nombre"));
                u.setUsuario(rs.getString("usuario"));
                u.setPassword(rs.getString("password"));
                u.setRol(rs.getString("rol"));
                u.setActivo(rs.getBoolean("activo"));
                lista.add(u);
            }
            rs.close();
            cs.close();
        } catch (Exception e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return lista;
    }

    public Usuario obtener(int id) {
        Usuario u = null;
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL ObtenerUsuario(?)}");
            cs.setInt(1, id);
            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
                u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setDni(rs.getString("dni"));
                u.setNombres(rs.getString("nombres"));
                u.setApellidos(rs.getString("apellidos"));
                u.setNombre(rs.getString("nombre"));
                u.setUsuario(rs.getString("usuario"));
                u.setPassword(rs.getString("password"));
                u.setRol(rs.getString("rol"));
                u.setActivo(rs.getBoolean("activo"));
            }
            rs.close();
            cs.close();
        } catch (Exception e) {
            System.out.println("Error al obtener usuario: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return u;
    }

    public boolean insertar(Usuario u) {
        boolean ok = false;
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL InsertarUsuario(?,?,?,?,?,?)}");
            cs.setString(1, u.getDni());
            cs.setString(2, u.getNombres());
            cs.setString(3, u.getApellidos());
            cs.setString(4, u.getUsuario());
            cs.setString(5, u.getPassword());
            cs.setString(6, u.getRol());
            cs.executeUpdate();
            cs.close();
            ok = true;
        } catch (Exception e) {
            System.out.println("Error al insertar usuario: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return ok;
    }

    public boolean modificar(Usuario u) {
        boolean ok = false;
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL ModificarUsuario(?,?,?,?,?,?,?,?)}");
            cs.setInt(1, u.getId());
            cs.setString(2, u.getDni());
            cs.setString(3, u.getNombres());
            cs.setString(4, u.getApellidos());
            cs.setString(5, u.getUsuario());
            cs.setString(6, u.getPassword());
            cs.setString(7, u.getRol());
            cs.setBoolean(8, u.isActivo());
            cs.executeUpdate();
            cs.close();
            ok = true;
        } catch (Exception e) {
            System.out.println("Error al modificar usuario: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return ok;
    }

    public boolean eliminar(int id) {
        boolean ok = false;
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL EliminarUsuario(?)}");
            cs.setInt(1, id);
            cs.executeUpdate();
            cs.close();
            ok = true;
        } catch (Exception e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return ok;
    }
}
