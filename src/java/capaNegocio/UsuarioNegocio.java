package capaNegocio;

import capaDatos.UsuarioDAO;
import capaEntidad.Usuario;
import java.util.List;

public class UsuarioNegocio {

    private final UsuarioDAO dao = new UsuarioDAO();

    public Usuario login(String usuario, String password) {
        if (usuario == null || password == null) return null;
        if (usuario.trim().isEmpty() || password.trim().isEmpty()) return null;
        return dao.login(usuario.trim(), password.trim());
    }

    public List<Usuario> listar()                  { return dao.listar(); }
    public Usuario      obtener(int id)            { return dao.obtener(id); }
    public boolean      insertar(Usuario u)        { return dao.insertar(u); }
    public boolean      modificar(Usuario u)       { return dao.modificar(u); }
    public boolean      eliminar(int id)           { return dao.eliminar(id); }
}
