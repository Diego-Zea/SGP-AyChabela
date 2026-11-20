package servlets;

import capaEntidad.Plato;
import capaEntidad.Usuario;
import capaNegocio.InsumoNegocio;
import capaNegocio.PlatoNegocio;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Receta de un plato: que insumos consume y en que cantidad.
 * Es la pieza que conecta Platos con Insumos y la que hace posible que el
 * descuento de almacen y el reporte de consumo funcionen solos.
 *
 * Acceso: ADMINISTRADOR y COCINA.
 */
@WebServlet(name = "RecetaServlet", urlPatterns = {"/RecetaServlet"})
public class RecetaServlet extends HttpServlet {

    private final InsumoNegocio insumoNegocio = new InsumoNegocio();
    private final PlatoNegocio  platoNegocio  = new PlatoNegocio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Usuario actual = Acceso.actual(req);
        if (actual == null) { resp.sendRedirect("login.jsp"); return; }
        if (!Acceso.tieneRol(actual, "ADMIN", "COCINA")) { resp.sendRedirect("menu.jsp"); return; }

        String accion = req.getParameter("accion");

        // Quitar un insumo de la receta
        if ("quitar".equals(accion)) {
            int idItem  = Integer.parseInt(req.getParameter("id"));
            int platoId = Integer.parseInt(req.getParameter("platoId"));
            insumoNegocio.eliminarInsumoDePlato(idItem);
            resp.sendRedirect("RecetaServlet?platoId=" + platoId);
            return;
        }

        int platoId = Integer.parseInt(req.getParameter("platoId"));
        Plato plato = platoNegocio.obtener(platoId);

        req.setAttribute("plato",   plato);
        req.setAttribute("receta",  insumoNegocio.listarReceta(platoId));
        req.setAttribute("insumos", insumoNegocio.listarActivos());
        req.getRequestDispatcher("receta.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Usuario actual = Acceso.actual(req);
        if (actual == null) { resp.sendRedirect("login.jsp"); return; }
        if (!Acceso.tieneRol(actual, "ADMIN", "COCINA")) { resp.sendRedirect("menu.jsp"); return; }

        int platoId  = enteroSeguro(req.getParameter("platoId"), 0);
        int insumoId = enteroSeguro(req.getParameter("insumoId"), 0);
        double cantidad;
        try {
            cantidad = Double.parseDouble(req.getParameter("cantidad"));
        } catch (Exception e) {
            cantidad = 0;
        }

        // Si ya existia ese insumo en la receta, el SP actualiza la cantidad
        boolean ok = insumoNegocio.agregarInsumoAPlato(platoId, insumoId, cantidad);

        resp.sendRedirect("RecetaServlet?platoId=" + platoId + (ok ? "" : "&error=1"));
    }

    private int enteroSeguro(String txt, int porDefecto) {
        try { return Integer.parseInt(txt); } catch (Exception e) { return porDefecto; }
    }
}
