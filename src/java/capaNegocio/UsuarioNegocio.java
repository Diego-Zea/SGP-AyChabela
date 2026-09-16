package capaNegocio;

import capaDatos.UsuarioDAO;
import capaEntidad.Usuario;

public class UsuarioNegocio {

    private final UsuarioDAO dao = new UsuarioDAO();

    public Usuario login(String usuario, String password) {
        if (usuario == null || password == null) return null;
        if (usuario.trim().isEmpty() || password.trim().isEmpty()) return null;
        return dao.login(usuario.trim(), password.trim());
    }
}
