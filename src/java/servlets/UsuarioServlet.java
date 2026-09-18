package servlets;

import capaEntidad.Usuario;
import capaNegocio.UsuarioNegocio;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "UsuarioServlet", urlPatterns = {"/UsuarioServlet"})
public class UsuarioServlet extends HttpServlet {

    private final UsuarioNegocio negocio = new UsuarioNegocio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Usuario actual = Acceso.actual(req);
        if (actual == null) { resp.sendRedirect("login.jsp"); return; }
        // Solo el ADMINISTRADOR puede gestionar usuarios
        if (!Acceso.tieneRol(actual, "ADMIN")) { resp.sendRedirect("menu.jsp"); return; }

        String accion = req.getParameter("accion");
        if (accion == null) accion = "listar";

        switch (accion) {
            case "nuevo":
                req.getRequestDispatcher("usuario_form.jsp").forward(req, resp);
                break;
            case "editar":
                int id = Integer.parseInt(req.getParameter("id"));
                req.setAttribute("u", negocio.obtener(id));
                req.getRequestDispatcher("usuario_form.jsp").forward(req, resp);
                break;
            case "eliminar":
                int idEliminar = Integer.parseInt(req.getParameter("id"));
                negocio.eliminar(idEliminar);
                resp.sendRedirect("UsuarioServlet");
                break;
            default:
                req.setAttribute("usuarios", negocio.listar());
                req.getRequestDispatcher("usuarios.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Usuario actual = Acceso.actual(req);
        if (actual == null) { resp.sendRedirect("login.jsp"); return; }
        if (!Acceso.tieneRol(actual, "ADMIN")) { resp.sendRedirect("menu.jsp"); return; }

        String idTxt = req.getParameter("id");
        Usuario u = new Usuario();
        u.setDni(req.getParameter("dni"));
        u.setNombres(req.getParameter("nombres"));
        u.setApellidos(req.getParameter("apellidos"));
        u.setUsuario(req.getParameter("usuario"));
        u.setPassword(req.getParameter("password"));
        u.setRol(req.getParameter("rol"));
        u.setActivo("on".equals(req.getParameter("activo"))
                 || "true".equals(req.getParameter("activo")));

        if (idTxt == null || idTxt.isEmpty()) {
            negocio.insertar(u);
        } else {
            u.setId(Integer.parseInt(idTxt));
            negocio.modificar(u);
        }
        resp.sendRedirect("UsuarioServlet");
    }
}
