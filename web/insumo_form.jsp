<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="capaEntidad.Insumo" %>
<% request.setAttribute("paginaActual", "insumos"); %>
<%
    Insumo i = (Insumo) request.getAttribute("insumo");
    boolean editando = (i != null && i.getId() > 0);
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title><%= editando ? "Editar" : "Nuevo" %> Insumo - SGP Ay Chabela</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="contenedor">
    <h2 class="titulo-pagina"><%= editando ? "Editar Insumo" : "Nuevo Insumo" %></h2>

    <% if (error != null) { %>
        <div class="alert alert-error"><%= error %></div>
    <% } %>

    <div class="card">
        <form action="InsumoServlet" method="post">
            <% if (editando) { %>
            <input type="hidden" name="id" value="<%= i.getId() %>">
            <% } %>

            <div class="form-group">
                <label for="nombre">Nombre del insumo *</label>
                <input type="text" id="nombre" name="nombre" maxlength="80" required
                       value="<%= i == null || i.getNombre() == null ? "" : i.getNombre() %>">
            </div>

            <div class="form-grid-2">
                <div class="form-group">
                    <label for="unidad">Unidad de medida *</label>
                    <select id="unidad" name="unidad" required>
                        <%
                            String[] unidades = {"kg", "g", "l", "ml", "und"};
                            String actual = (i == null || i.getUnidad() == null) ? "kg" : i.getUnidad();
                            for (String u : unidades) {
                        %>
                        <option value="<%= u %>" <%= u.equals(actual) ? "selected" : "" %>><%= u %></option>
                        <% } %>
                    </select>
                </div>

                <div class="form-group">
                    <label for="stock">Stock actual *</label>
                    <input type="number" id="stock" name="stock" step="0.001" min="0" required
                           value="<%= i == null ? "0" : String.format("%.3f", i.getStock()) %>">
                </div>
            </div>

            <div class="form-group">
                <label for="stockMinimo">Stock minimo (avisa cuando hay que comprar) *</label>
                <input type="number" id="stockMinimo" name="stockMinimo" step="0.001" min="0" required
                       value="<%= i == null ? "0" : String.format("%.3f", i.getStockMinimo()) %>">
            </div>

            <% if (editando) { %>
            <div class="form-group">
                <label>
                    <input type="checkbox" name="activo" <%= i.isActivo() ? "checked" : "" %>>
                    Insumo activo
                </label>
            </div>
            <% } %>

            <div class="acciones-form">
                <button type="submit" class="btn btn-primary">Guardar</button>
                <a href="InsumoServlet" class="btn btn-secondary">Cancelar</a>
            </div>
        </form>
    </div>
</div>

</body>
</html>
