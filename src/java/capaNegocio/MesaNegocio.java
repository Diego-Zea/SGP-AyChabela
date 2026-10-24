package capaNegocio;

import capaDatos.MesaDAO;
import capaEntidad.Mesa;
import java.util.List;

public class MesaNegocio {

    private final MesaDAO dao = new MesaDAO();

    public List<Mesa> listar()                                  { return dao.listar(); }
    public boolean    cambiarEstado(int id, String estado)      { return dao.cambiarEstado(id, estado); }
}
