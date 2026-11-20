package capaNegocio;

import capaDatos.InsumoDAO;
import capaEntidad.Insumo;
import capaEntidad.RecetaItem;
import java.util.List;

public class InsumoNegocio {

    private final InsumoDAO dao = new InsumoDAO();

    public List<Insumo> listar()        { return dao.listar(); }
    public List<Insumo> listarActivos() { return dao.listarActivos(); }
    public Insumo obtener(int id)       { return dao.obtener(id); }
    public boolean eliminar(int id)     { return dao.eliminar(id); }

    public boolean insertar(Insumo i) {
        if (!esValido(i)) return false;
        return dao.insertar(i);
    }

    public boolean modificar(Insumo i) {
        if (!esValido(i)) return false;
        return dao.modificar(i);
    }

    /** Una entrada de mercaderia siempre es positiva. */
    public boolean registrarEntrada(int id, double cantidad, String obs) {
        if (cantidad <= 0) return false;
        return dao.registrarEntrada(id, cantidad, obs);
    }

    // ── Receta ──

    public List<RecetaItem> listarReceta(int platoId) {
        return dao.listarReceta(platoId);
    }

    public boolean agregarInsumoAPlato(int platoId, int insumoId, double cantidad) {
        // Sin cantidad no hay consumo que descontar: se rechaza
        if (platoId <= 0 || insumoId <= 0 || cantidad <= 0) return false;
        return dao.agregarInsumoAPlato(platoId, insumoId, cantidad);
    }

    public boolean eliminarInsumoDePlato(int id) {
        return dao.eliminarInsumoDePlato(id);
    }

    // ── Validacion comun ─

    private boolean esValido(Insumo i) {
        if (i == null) return false;
        if (i.getNombre() == null || i.getNombre().trim().isEmpty()) return false;
        if (i.getUnidad() == null || i.getUnidad().trim().isEmpty()) return false;
        if (i.getStock() < 0 || i.getStockMinimo() < 0) return false;
        i.setNombre(i.getNombre().trim());
        return true;
    }
}
