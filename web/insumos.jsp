<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, capaEntidad.Insumo, capaEntidad.Usuario" %>
<% request.setAttribute("paginaActual", "insumos"); %>
<%
    List<Insumo> insumos = (List<Insumo>) request.getAttribute("insumos");
    String msg = request.getParameter("msg");
    Usuario uIns = (Usuario) session.getAttribute("usuario");
    boolean esAdminIns = uIns != null && "ADMIN".equalsIgnoreCase(uIns.getRol());
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Insumos - SGP Ay Chabela</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="contenedor">
    <div class="titulo-pagina">
        <span>Almacen de Insumos</span>
        <a href="InsumoServlet?accion=nuevo" class="btn btn-primary">+ Nuevo Insumo</a>
    </div>

    <% if ("ok".equals(msg)) { %>
        <div class="alert alert-success">Insumo guardado correctamente.</div>
    <% } else if ("entrada".equals(msg)) { %>
        <div class="alert alert-success">Entrada de stock registrada en el kardex.</div>
    <% } else if ("baja".equals(msg)) { %>
        <div class="alert alert-info">Insumo dado de baja. Su historial se conserva.</div>
    <% } else if ("error".equals(msg)) { %>
        <div class="alert alert-error">No se pudo completar la operacion. Revisa los datos.</div>
    <% } %>

    <div class="alert alert-info">
        Cuando cocina pasa un pedido a <strong>En preparacion</strong>, el sistema descuenta
        automaticamente de este almacen los insumos segun la receta de cada plato.
    </div>

    <table>
        <thead>
            <tr>
                <th>ID</th><th>Insumo</th><th>Unidad</th>
                <th>Stock actual</th><th>Stock minimo</th>
                <th>Estado</th><th>Reponer</th><th>Acciones</th>
            </tr>
        </thead>
        <tbody>
        <%
            if (insumos != null && !insumos.isEmpty()) {
                for (Insumo i : insumos) {
        %>
            <tr>
                <td><%= i.getId() %></td>
                <td><strong><%= i.getNombre() %></strong></td>
                <td><%= i.getUnidad() %></td>
                <td><%= String.format("%.3f", i.getStock()) %></td>
                <td><%= String.format("%.3f", i.getStockMinimo()) %></td>
                <td>
                    <% if (!i.isActivo()) { %>
                        <span class="badge badge-nodisponible">De baja</span>
                    <% } else if (i.isStockBajo()) { %>
                        <span class="badge badge-cancelado">Stock bajo</span>
                    <% } else { %>
                        <span class="badge badge-disponible">OK</span>
                    <% } %>
                </td>
                <td>
                    <%-- Compra rapida: suma stock y deja el movimiento en el kardex --%>
                    <form action="InsumoServlet" method="post" class="form-entrada">
                        <input type="hidden" name="accion" value="entrada">
                        <input type="hidden" name="id" value="<%= i.getId() %>">
                        <input type="number" name="cantidad" step="0.001" min="0.001"
                               placeholder="0.000" class="input-mini" required>
                        <button type="submit" class="btn btn-success btn-sm">+</button>
                    </form>
                </td>
                <td>
                    <a href="InsumoServlet?accion=editar&id=<%= i.getId() %>"
                       class="btn btn-warning btn-sm">Editar</a>
                    <% if (esAdminIns && i.isActivo()) { %>
                    <a href="InsumoServlet?accion=eliminar&id=<%= i.getId() %>"
                       class="btn btn-danger btn-sm"
                       onclick="return confirm('Dar de baja este insumo?');">Baja</a>
                    <% } %>
                </td>
            </tr>
        <%      }
            } else { %>
            <tr><td colspan="8" class="vacio">No hay insumos registrados</td></tr>
        <% } %>
        </tbody>
    </table>
</div>

</body>
</html>
