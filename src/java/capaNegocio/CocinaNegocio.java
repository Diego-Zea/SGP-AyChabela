package capaNegocio;

import capaDatos.CocinaDAO;
import capaDatos.PedidoDAO;
import capaEntidad.PedidoCocina;
import java.util.Arrays;
import java.util.List;

/**
 * Reglas del Panel de Cocina.
 * La mas importante: el pedido solo puede avanzar en un sentido
 * (PENDIENTE -> EN_PREPARACION -> LISTO -> ENTREGADO). Asi se evita que
 * alguien retroceda un pedido por URL y se vuelvan a descontar insumos.
 */
public class CocinaNegocio {

    private final CocinaDAO   dao       = new CocinaDAO();
    private final PedidoDAO   pedidoDAO = new PedidoDAO();

    private static final List<String> FLUJO =
            Arrays.asList("PENDIENTE", "EN_PREPARACION", "LISTO", "ENTREGADO");

    public List<PedidoCocina> listarPedidosActivos() {
        return dao.listarPedidosActivos();
    }

    /** Contadores de las tres cajas superiores del panel. */
    public int contar(List<PedidoCocina> pedidos, String estado) {
        int n = 0;
        for (PedidoCocina p : pedidos) {
            if (estado.equals(p.getEstado())) n++;
        }
        return n;
    }

    /**
     * Avanza el pedido al siguiente estado.
     * Se comprueba contra la BD (no contra lo que llega del formulario)
     * que el salto sea de exactamente un paso hacia adelante.
     */
    public boolean avanzarEstado(int pedidoId, String estadoNuevo) {
        if (estadoNuevo == null) return false;

        capaEntidad.Pedido actual = pedidoDAO.obtener(pedidoId);
        if (actual == null) return false;

        int posActual = FLUJO.indexOf(actual.getEstado());
        int posNueva  = FLUJO.indexOf(estadoNuevo);

        // El estado debe existir en el flujo y ser el inmediato siguiente
        if (posActual < 0 || posNueva < 0) return false;
        if (posNueva != posActual + 1) return false;

        return pedidoDAO.cambiarEstado(pedidoId, estadoNuevo);
    }

    /** Cancelar es la unica salida fuera del flujo, y solo si no se preparo. */
    public boolean cancelar(int pedidoId) {
        capaEntidad.Pedido actual = pedidoDAO.obtener(pedidoId);
        if (actual == null) return false;
        if (!"PENDIENTE".equals(actual.getEstado())) return false;
        return pedidoDAO.cambiarEstado(pedidoId, "CANCELADO");
    }
}
