package servlets;

import capaEntidad.DetallePedido;
import capaEntidad.Pedido;
import capaEntidad.Usuario;
import capaNegocio.MesaNegocio;
import capaNegocio.PedidoNegocio;
import capaNegocio.PlatoNegocio;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "PedidoServlet", urlPatterns = {"/PedidoServlet"})
public class PedidoServlet extends HttpServlet {

    private final PedidoNegocio negocio       = new PedidoNegocio();
    private final PlatoNegocio  platoNegocio  = new PlatoNegocio();
    private final MesaNegocio   mesaNegocio   = new MesaNegocio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (req.getSession(false) == null || req.getSession().getAttribute("usuario") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        String accion = req.getParameter("accion");
        if (accion == null) accion = "listar";

        switch (accion) {
            case "nuevo":
                req.setAttribute("mesas",  mesaNegocio.listar());
                req.setAttribute("platos", platoNegocio.listarDisponibles());
                req.getRequestDispatcher("pedido_form.jsp").forward(req, resp);
                break;
            case "ver":
                int idVer = Integer.parseInt(req.getParameter("id"));
                Pedido p = negocio.obtener(idVer);
                req.setAttribute("pedido", p);
                req.setAttribute("detalle", negocio.listarDetalle(idVer));
                req.getRequestDispatcher("pedido_detalle.jsp").forward(req, resp);
                break;
            case "cambiarEstado":
                int idCambio = Integer.parseInt(req.getParameter("id"));
                negocio.cambiarEstado(idCambio, req.getParameter("estado"));
                resp.sendRedirect("PedidoServlet");
                break;
            default:
                req.setAttribute("pedidos", negocio.listar());
                req.getRequestDispatcher("pedidos.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (req.getSession(false) == null || req.getSession().getAttribute("usuario") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }
        Usuario u = (Usuario) req.getSession().getAttribute("usuario");

        Pedido pedido = new Pedido();
        try {
            pedido.setMesaId(Integer.parseInt(req.getParameter("mesaId")));
        } catch (NumberFormatException e) {
            pedido.setMesaId(0);
        }
        pedido.setUsuarioId(u.getId());
        pedido.setNotas(req.getParameter("notas"));

        // Construir lista de detalles a partir de los parametros del formulario:
        // platoIds[] y cantidades[] llegan en paralelo
        String[] platoIds  = req.getParameterValues("platoId");
        String[] cantidades = req.getParameterValues("cantidad");

        List<DetallePedido> items = new ArrayList<>();
        if (platoIds != null && cantidades != null) {
            for (int i = 0; i < platoIds.length; i++) {
                try {
                    int idPlato = Integer.parseInt(platoIds[i]);
                    int cant = Integer.parseInt(cantidades[i]);
                    if (idPlato > 0 && cant > 0) {
                        items.add(new DetallePedido(idPlato, cant));
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        if (items.isEmpty() || pedido.getMesaId() == 0) {
            req.setAttribute("error", "Debes seleccionar una mesa y al menos un plato.");
            req.setAttribute("mesas",  mesaNegocio.listar());
            req.setAttribute("platos", platoNegocio.listarDisponibles());
            req.getRequestDispatcher("pedido_form.jsp").forward(req, resp);
            return;
        }

        negocio.registrar(pedido, items);
        resp.sendRedirect("PedidoServlet");
    }
}
