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
    public boolean         insertar(Plato p)          { return dao.insertar(p); }
    public boolean         modificar(Plato p)         { return dao.modificar(p); }
    public boolean         eliminar(int id)           { return dao.eliminar(id); }
    public List<Categoria> listarCategorias()         { return dao.listarCategorias(); }
}
