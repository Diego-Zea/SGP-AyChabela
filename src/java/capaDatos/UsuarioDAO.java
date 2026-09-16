package capaDatos;

import capaEntidad.Usuario;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

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
}
