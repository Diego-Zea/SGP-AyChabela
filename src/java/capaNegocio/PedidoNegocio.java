package capaNegocio;

import capaDatos.PedidoDAO;
import capaEntidad.DetallePedido;
import capaEntidad.Pedido;
import java.util.List;

public class PedidoNegocio {

    private final PedidoDAO dao = new PedidoDAO();

    public int registrar(Pedido p, List<DetallePedido> items) {
        // Validación propia (Commit 8): Verificar que el pedido tenga al menos un plato/ítem
        if (items == null || items.isEmpty()) {
            System.err.println("Error: El pedido debe contener al menos un plato.");
            return 0;
        }

        return dao.registrar(p, items);
    }

    public List<Pedido> listar() {
        return dao.listar();
    }

    public Pedido obtener(int id) {
        return dao.obtener(id);
    }

    public List<DetallePedido> listarDetalle(int pedidoId) {
        return dao.listarDetalle(pedidoId);
    }

    public boolean cambiarEstado(int id, String estado) {
        return dao.cambiarEstado(id, estado);
    }
}