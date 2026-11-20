package servlets;

import capaEntidad.PedidoCocina;
import capaEntidad.Usuario;
import capaNegocio.CocinaNegocio;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * PANEL DE COCINA.
 * Acceso: ADMINISTRADOR y COCINA.
 */
@WebServlet(name = "CocinaServlet", urlPatterns = {"/CocinaServlet"})
public class CocinaServlet extends HttpServlet {

    private final CocinaNegocio negocio = new CocinaNegocio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Usuario actual = Acceso.actual(req);
        if (actual == null) { resp.sendRedirect("login.jsp"); return; }

        // Panel de cocina: solo ADMINISTRADOR y COCINA (el mesero solo mira Pedidos)
        if (!Acceso.tieneRol(actual, "ADMIN", "COCINA")) {
            resp.sendRedirect("menu.jsp");
            return;
        }

        List<PedidoCocina> pedidos = negocio.listarPedidosActivos();

        req.setAttribute("pedidos", pedidos);
        // Contadores de las tres cajas superiores
        req.setAttribute("nPendientes",  negocio.contar(pedidos, "PENDIENTE"));
        req.setAttribute("nPreparacion", negocio.contar(pedidos, "EN_PREPARACION"));
        req.setAttribute("nListos",      negocio.contar(pedidos, "LISTO"));

        req.getRequestDispatcher("cocina.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Usuario actual = Acceso.actual(req);
        if (actual == null) { resp.sendRedirect("login.jsp"); return; }
        if (!Acceso.tieneRol(actual, "ADMIN", "COCINA")) { resp.sendRedirect("menu.jsp"); return; }

        int id = enteroSeguro(req.getParameter("id"), 0);
        String accion = req.getParameter("accion");

        if ("cancelar".equals(accion)) {
            negocio.cancelar(id);
        } else {
            // La capa de negocio verifica contra la BD que el salto sea valido
            negocio.avanzarEstado(id, req.getParameter("estado"));
        }

        // Patron POST-Redirect-GET: evita que al recargar F5 se repita la accion
        resp.sendRedirect("CocinaServlet");
    }

    private int enteroSeguro(String txt, int porDefecto) {
        try { return Integer.parseInt(txt); } catch (Exception e) { return porDefecto; }
    }
}
