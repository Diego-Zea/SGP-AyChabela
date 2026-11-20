package servlets;

import capaEntidad.Insumo;
import capaEntidad.Usuario;
import capaNegocio.InsumoNegocio;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "InsumoServlet", urlPatterns = {"/InsumoServlet"})
public class InsumoServlet extends HttpServlet {

    private final InsumoNegocio negocio = new InsumoNegocio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Usuario actual = Acceso.actual(req);
        if (actual == null) { resp.sendRedirect("login.jsp"); return; }

        // Insumos: solo ADMINISTRADOR y COCINA
        if (!Acceso.tieneRol(actual, "ADMIN", "COCINA")) {
            resp.sendRedirect("menu.jsp");
            return;
        }

        String accion = req.getParameter("accion");
        if (accion == null) accion = "listar";

        switch (accion) {
            case "nuevo":
                req.getRequestDispatcher("insumo_form.jsp").forward(req, resp);
                break;

            case "editar":
                int id = Integer.parseInt(req.getParameter("id"));
                req.setAttribute("insumo", negocio.obtener(id));
                req.getRequestDispatcher("insumo_form.jsp").forward(req, resp);
                break;

            case "eliminar":
                // Solo el administrador da de baja un insumo
                if (!Acceso.tieneRol(actual, "ADMIN")) { resp.sendRedirect("InsumoServlet"); return; }
                negocio.eliminar(Integer.parseInt(req.getParameter("id")));
                resp.sendRedirect("InsumoServlet?msg=baja");
                break;

            default: // listar
                req.setAttribute("insumos", negocio.listar());
                req.getRequestDispatcher("insumos.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Usuario actual = Acceso.actual(req);
        if (actual == null) { resp.sendRedirect("login.jsp"); return; }
        if (!Acceso.tieneRol(actual, "ADMIN", "COCINA")) { resp.sendRedirect("menu.jsp"); return; }

        String accion = req.getParameter("accion");

        // ── Registrar una compra / reposicion de stock ──
        if ("entrada".equals(accion)) {
            int id = enteroSeguro(req.getParameter("id"), 0);
            double cantidad = decimalSeguro(req.getParameter("cantidad"), 0);
            boolean ok = negocio.registrarEntrada(id, cantidad, "Compra registrada por " + actual.getNombre());
            resp.sendRedirect("InsumoServlet?msg=" + (ok ? "entrada" : "error"));
            return;
        }

        // ── Alta / edicion ──
        Insumo i = new Insumo();
        i.setNombre(req.getParameter("nombre"));
        i.setUnidad(req.getParameter("unidad"));
        i.setStock(decimalSeguro(req.getParameter("stock"), 0));
        i.setStockMinimo(decimalSeguro(req.getParameter("stockMinimo"), 0));
        i.setActivo(req.getParameter("activo") != null);

        String idTxt = req.getParameter("id");
        boolean ok;
        if (idTxt == null || idTxt.isEmpty()) {
            i.setActivo(true);
            ok = negocio.insertar(i);
        } else {
            i.setId(Integer.parseInt(idTxt));
            ok = negocio.modificar(i);
        }

        if (!ok) {
            // La validacion de negocio rechazo los datos: se vuelve al formulario
            req.setAttribute("error", "Revisa los datos: el nombre y la unidad son obligatorios "
                                    + "y las cantidades no pueden ser negativas.");
            req.setAttribute("insumo", i);
            req.getRequestDispatcher("insumo_form.jsp").forward(req, resp);
            return;
        }
        resp.sendRedirect("InsumoServlet?msg=ok");
    }

    private int enteroSeguro(String txt, int porDefecto) {
        try { return Integer.parseInt(txt); } catch (Exception e) { return porDefecto; }
    }

    private double decimalSeguro(String txt, double porDefecto) {
        try { return Double.parseDouble(txt); } catch (Exception e) { return porDefecto; }
    }
}
