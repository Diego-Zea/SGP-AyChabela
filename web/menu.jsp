<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="capaEntidad.Usuario" %>
<% request.setAttribute("paginaActual", "menu"); %>
<%
    Usuario uSes = (Usuario) session.getAttribute("usuario");
    if (uSes == null) { response.sendRedirect("login.jsp"); return; }
    String rolMenu = uSes.getRol() == null ? "" : uSes.getRol();
    boolean adminMenu  = "ADMIN".equalsIgnoreCase(rolMenu);
    boolean meseroMenu = "MESERO".equalsIgnoreCase(rolMenu);
    boolean cocinaMenu = "COCINA".equalsIgnoreCase(rolMenu);
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Menu Principal - SGP Ay Chabela</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="contenedor">
    <h2 class="titulo-pagina">Menu Principal
        <% if (adminMenu)  { %>- Administrador<% } %>
        <% if (meseroMenu) { %>- Mesero<% } %>
        <% if (cocinaMenu) { %>- Cocina<% } %>
    </h2>

    <div class="card">
        <p>Bienvenido al Sistema de Gestion de Pedidos del restaurante
           <strong>Ay Chabela</strong>. Estas son las opciones disponibles
           para tu perfil.</p>
    </div>

    <div class="menu-grid">
        <% if (adminMenu || meseroMenu || cocinaMenu) { %>
        <a href="PlatoServlet">
            <div class="menu-card">
                <div class="icono">&#127869;</div>
                <h3>Platos</h3>
                <p>Registrar, listar y editar platos</p>
            </div>
        </a>
        <% } %>

        <% if (adminMenu || meseroMenu || cocinaMenu) { %>
        <a href="PedidoServlet">
            <div class="menu-card">
                <div class="icono">&#128221;</div>
                <h3>Pedidos</h3>
                <p>Registrar y ver pedidos de los clientes</p>
            </div>
        </a>
        <% } %>

        <%-- ── MODULO NUEVO: panel de cocina ── --%>
        <% if (adminMenu || cocinaMenu) { %>
        <a href="CocinaServlet">
            <div class="menu-card">
                <div class="icono">&#127859;</div>
                <h3>Panel de Cocina</h3>
                <p>Pedidos en tiempo real y cambio de estado</p>
            </div>
        </a>
        <% } %>

        <% if (adminMenu || meseroMenu) { %>
        <a href="MesaServlet">
            <div class="menu-card">
                <div class="icono">&#128666;</div>
                <h3>Mesas</h3>
                <p>Estado de las mesas del salon</p>
            </div>
        </a>
        <% } %>

        <%-- ── MODULO NUEVO: almacen de insumos ── --%>
        <% if (adminMenu || cocinaMenu) { %>
        <a href="InsumoServlet">
            <div class="menu-card">
                <div class="icono">&#129387;</div>
                <h3>Insumos</h3>
                <p>Almacen, stock y recetas de los platos</p>
            </div>
        </a>
        <% } %>

        <%-- ── MODULO NUEVO: reportes de ventas ── --%>
        <% if (adminMenu) { %>
        <a href="ReporteServlet">
            <div class="menu-card">
                <div class="icono">&#128202;</div>
                <h3>Reportes</h3>
                <p>Ventas, platos e insumos. Exporta a Excel</p>
            </div>
        </a>
        <% } %>

        <% if (adminMenu) { %>
        <a href="UsuarioServlet">
            <div class="menu-card">
                <div class="icono">&#128100;</div>
                <h3>Usuarios</h3>
                <p>Gestion de usuarios del sistema</p>
            </div>
        </a>
        <% } %>
    </div>
</div>

</body>
</html>
