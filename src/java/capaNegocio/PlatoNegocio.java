package capaNegocio;

import capaDatos.PlatoDAO;
import capaEntidad.Categoria;
import capaEntidad.Plato;
import java.util.List;

public class PlatoNegocio {

    private final PlatoDAO dao = new PlatoDAO();

    public List<Plato>     listar()                   { return dao.listar(); }
    public List<Plato>     listarDisponibles()        { return dao.listarDisponibles(); }
    public Plato           obtener(int id)            { return dao.obtener(id); }
    public boolean         insertar(Plato p)          { return esValido(p) && dao.insertar(p); }
    public boolean         modificar(Plato p)         { return esValido(p) && dao.modificar(p); }
    public boolean         eliminar(int id)           { return dao.eliminar(id); }
    public List<Categoria> listarCategorias()         { return dao.listarCategorias(); }
    private boolean esValido(Plato p) {
        return p.getNombre() != null
                && !p.getNombre().trim().isEmpty()
                && p.getPrecio() > 0
                && p.getCategoriaId() > 0;
}
