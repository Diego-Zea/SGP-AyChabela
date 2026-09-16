package servlets;

import capaEntidad.Usuario;
import capaNegocio.UsuarioNegocio;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    private final UsuarioNegocio negocio = new UsuarioNegocio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Si se accede via GET redirigir al login
        resp.sendRedirect("login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String usuario  = req.getParameter("usuario");
        String password = req.getParameter("password");

        Usuario u = negocio.login(usuario, password);
        if (u != null) {
            HttpSession sesion = req.getSession();
            sesion.setAttribute("usuario", u);
            resp.sendRedirect("menu.jsp");
        } else {
            req.setAttribute("error", "Usuario o contrasena incorrectos");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }
}
