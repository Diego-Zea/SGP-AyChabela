package capaDatos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    // Los datos de conexion se leen de variables de entorno para poder
    // desplegar en la nube sin tocar el codigo ni subir contrasenas al
    // repositorio. Si no estan definidas se usan los valores locales.
    private static final String HOST     = valor("DB_HOST", "localhost");
    private static final String PUERTO   = valor("DB_PORT", "3306");
    private static final String BASE     = valor("DB_NAME", "aychabela");
    private static final String USUARIO  = valor("DB_USER", "root");
    private static final String PASSWORD = valor("DB_PASSWORD", "root");

    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + BASE
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private static String valor(String clave, String porDefecto) {
        String v = System.getenv(clave);
        return (v == null || v.trim().isEmpty()) ? porDefecto : v;
    }

    public static Connection obtener() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("Driver MySQL no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error al conectar con la BD: " + e.getMessage());
        }
        return con;
    }

    public static void cerrar(Connection con) {
        try {
            if (con != null && !con.isClosed()) con.close();
        } catch (SQLException e) {
            System.out.println("Error al cerrar conexion: " + e.getMessage());
        }
    }
}
