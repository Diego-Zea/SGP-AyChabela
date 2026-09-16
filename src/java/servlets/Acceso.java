package servlets;

import capaEntidad.Usuario;
import javax.servlet.http.HttpServletRequest;

/**
 * Roles del sistema: ADMIN, MESERO, COCINA.
 */
public class Acceso {

    /** Devuelve el usuario en sesion, o null si no hay sesion activa. */
    public static Usuario actual(HttpServletRequest req) {
        if (req.getSession(false) == null) return null;
        return (Usuario) req.getSession().getAttribute("usuario");
    }

    /** true si el usuario tiene alguno de los roles indicados. */
    public static boolean tieneRol(Usuario u, String... roles) {
        if (u == null || u.getRol() == null) return false;
        for (String r : roles) {
            if (r.equalsIgnoreCase(u.getRol())) return true;
        }
        return false;
    }
}
