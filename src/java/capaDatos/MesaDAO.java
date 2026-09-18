package capaDatos;

import capaEntidad.Mesa;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MesaDAO {

    public List<Mesa> listar() {
        List<Mesa> lista = new ArrayList<>();
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL ListarMesas()}");
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Mesa m = new Mesa();
                m.setId(rs.getInt("id"));
                m.setNumero(rs.getInt("numero"));
                m.setCapacidad(rs.getInt("capacidad"));
                m.setEstado(rs.getString("estado"));
                m.setUbicacion(rs.getString("ubicacion"));
                lista.add(m);
            }
            rs.close();
            cs.close();
        } catch (Exception e) {
            System.out.println("Error al listar mesas: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return lista;
    }

    public boolean cambiarEstado(int id, String estado) {
        boolean ok = false;
        Connection con = ConexionBD.obtener();
        try {
            CallableStatement cs = con.prepareCall("{CALL CambiarEstadoMesa(?,?)}");
            cs.setInt(1, id);
            cs.setString(2, estado);
            cs.executeUpdate();
            cs.close();
            ok = true;
        } catch (Exception e) {
            System.out.println("Error al cambiar estado de mesa: " + e.getMessage());
        } finally {
            ConexionBD.cerrar(con);
        }
        return ok;
    }
}
