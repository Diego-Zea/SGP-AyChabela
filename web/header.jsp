<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="capaEntidad.Usuario" %>
<%@ page import="servlets.Acceso" %>
<%
    // Validar sesion: si no hay usuario logueado, ir al login
    Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
    if (usuarioSesion == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    String paginaActual = request.getAttribute("paginaActual") == null
        ? "" : request.getAttribute("paginaActual").toString();
    // Permisos por modulo, centralizados en la clase Acceso
    boolean verPlatos   = Acceso.tieneRol(usuarioSesion, "ADMIN", "MESERO", "COCINA");
    boolean verPedidos  = Acceso.tieneRol(usuarioSesion, "ADMIN", "MESERO", "COCINA");
    boolean verUsuarios = Acceso.tieneRol(usuarioSesion, "ADMIN");
%>
<header class="header">
    <h1>Ay Chabela - SGP</h1>
    <div class="user-info">
        Hola, <strong><%= usuarioSesion.getNombre() %></strong>
        (<%= usuarioSesion.getRol() %>)
        <a href="LogoutServlet">Cerrar sesion</a>
    </div>
</header>

<nav class="nav">
    <a href="menu.jsp"       class="<%= "menu".equals(paginaActual)     ? "activo" : "" %>">Inicio</a>
    <% if (verPlatos) { %>
    <a href="PlatoServlet"   class="<%= "platos".equals(paginaActual)   ? "activo" : "" %>">Platos</a>
    <% } %>
    <% if (verUsuarios) { %>
    <a href="UsuarioServlet" class="<%= "usuarios".equals(paginaActual) ? "activo" : "" %>">Usuarios</a>
    <% } %>
    <% if (verPedidos) { %>
    <a href="PedidoServlet"
    class="<%= "pedidos".equals(paginaActual) ? "activo" : "" %>"> Pedidos
    </a>
    <% } %>
</nav>
