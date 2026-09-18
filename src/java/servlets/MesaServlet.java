package servlets;

import capaNegocio.MesaNegocio;
import capaEntidad.Usuario;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "MesaServlet", urlPatterns = {"/MesaServlet"})
public class MesaServlet extends HttpServlet {

    private final MesaNegocio negocio = new MesaNegocio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Usuario actual = Acceso.actual(req);
        if (actual == null) { resp.sendRedirect("login.jsp"); return; }
        // Mesas: solo ADMINISTRADOR y MESERO
        if (!Acceso.tieneRol(actual, "ADMIN", "MESERO")) { resp.sendRedirect("menu.jsp"); return; }

        String accion = req.getParameter("accion");
        if ("cambiarEstado".equals(accion)) {
            int id = Integer.parseInt(req.getParameter("id"));
            String estado = req.getParameter("estado");
            negocio.cambiarEstado(id, estado);
            resp.sendRedirect("MesaServlet");
            return;
        }

        req.setAttribute("mesas", negocio.listar());
        req.getRequestDispatcher("mesas.jsp").forward(req, resp);
    }
}
