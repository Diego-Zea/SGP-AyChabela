<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="capaEntidad.Usuario" %>
<%
    // Validar sesion: si no hay usuario logueado, ir al login
    Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
    if (usuarioSesion == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    String paginaActual = request.getAttribute("paginaActual") == null
        ? "" : request.getAttribute("paginaActual").toString();
    String rol = usuarioSesion.getRol() == null ? "" : usuarioSesion.getRol();
    boolean esAdmin  = "ADMIN".equalsIgnoreCase(rol);
    boolean esMesero = "MESERO".equalsIgnoreCase(rol);
    boolean esCocina = "COCINA".equalsIgnoreCase(rol);
    // Permisos por modulo
    boolean verPlatos   = esAdmin || esMesero || esCocina;
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
</nav>
