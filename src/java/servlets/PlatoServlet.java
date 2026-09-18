package servlets;

import capaEntidad.Categoria;
import capaEntidad.Plato;
import capaNegocio.PlatoNegocio;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "PlatoServlet", urlPatterns = {"/PlatoServlet"})
public class PlatoServlet extends HttpServlet {

    private final PlatoNegocio negocio = new PlatoNegocio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!sesionValida(req, resp)) return;

        String accion = req.getParameter("accion");
        if (accion == null) accion = "listar";

        switch (accion) {
            case "nuevo":
                req.setAttribute("categorias", negocio.listarCategorias());
                req.getRequestDispatcher("plato_form.jsp").forward(req, resp);
                break;
            case "editar":
                int id = Integer.parseInt(req.getParameter("id"));
                Plato p = negocio.obtener(id);
                req.setAttribute("plato", p);
                req.setAttribute("categorias", negocio.listarCategorias());
                req.getRequestDispatcher("plato_form.jsp").forward(req, resp);
                break;
            case "eliminar":
                int idEliminar = Integer.parseInt(req.getParameter("id"));
                negocio.eliminar(idEliminar);
                resp.sendRedirect("PlatoServlet");
                break;
            default: // listar
                List<Plato> lista = negocio.listar();
                req.setAttribute("platos", lista);
                req.getRequestDispatcher("platos.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!sesionValida(req, resp)) return;

        String idTxt = req.getParameter("id");

        Plato p = new Plato();
        p.setNombre(req.getParameter("nombre"));
        p.setDescripcion(req.getParameter("descripcion"));
        try {
            p.setPrecio(Double.parseDouble(req.getParameter("precio")));
        } catch (NumberFormatException e) {
            p.setPrecio(0);
        }
        try {
            p.setCategoriaId(Integer.parseInt(req.getParameter("categoriaId")));
        } catch (NumberFormatException e) {
            p.setCategoriaId(0);
        }
        p.setDisponible("on".equals(req.getParameter("disponible"))
                     || "true".equals(req.getParameter("disponible")));

        if (idTxt == null || idTxt.isEmpty()) {
            negocio.insertar(p);
        } else {
            p.setId(Integer.parseInt(idTxt));
            negocio.modificar(p);
        }
        resp.sendRedirect("PlatoServlet");
    }

    private boolean sesionValida(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if (req.getSession(false) == null || req.getSession().getAttribute("usuario") == null) {
            resp.sendRedirect("login.jsp");
            return false;
        }
        return true;
    }
}
