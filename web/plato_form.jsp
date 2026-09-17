<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, capaEntidad.Plato, capaEntidad.Categoria" %>
<% request.setAttribute("paginaActual", "platos"); %>
<%
    Plato p = (Plato) request.getAttribute("plato");
    boolean esEdicion = (p != null);
    List<Categoria> categorias = (List<Categoria>) request.getAttribute("categorias");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title><%= esEdicion ? "Editar" : "Nuevo" %> Plato - SGP Ay Chabela</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="contenedor">
    <h2 class="titulo-pagina"><%= esEdicion ? "Editar Plato" : "Nuevo Plato" %></h2>

    <div class="card">
        <form action="PlatoServlet" method="post">
            <% if (esEdicion) { %>
                <input type="hidden" name="id" value="<%= p.getId() %>">
            <% } %>

            <div class="form-grid-2">
                <div class="form-group">
                    <label for="nombre">Nombre del plato *</label>
                    <input type="text" id="nombre" name="nombre" required
                           value="<%= esEdicion ? p.getNombre() : "" %>">
                </div>

                <div class="form-group">
                    <label for="precio">Precio (S/) *</label>
                    <input type="number" step="0.01" min="0" id="precio" name="precio" required
                           value="<%= esEdicion ? p.getPrecio() : "" %>">
                </div>
            </div>

            <div class="form-group">
                <label for="descripcion">Descripcion</label>
                <textarea id="descripcion" name="descripcion" rows="3"><%= esEdicion && p.getDescripcion() != null ? p.getDescripcion() : "" %></textarea>
            </div>

            <div class="form-grid-2">
                <div class="form-group">
                    <label for="categoriaId">Categoria *</label>
                    <select id="categoriaId" name="categoriaId" required>
                        <option value="">-- Seleccionar --</option>
                        <% if (categorias != null) {
                               for (Categoria c : categorias) {
                                   boolean sel = esEdicion && p.getCategoriaId() == c.getId();
                        %>
                            <option value="<%= c.getId() %>" <%= sel ? "selected" : "" %>><%= c.getNombre() %></option>
                        <%     }
                           }
                        %>
                    </select>
                </div>

                <div class="form-group">
                    <label>
                        <input type="checkbox" name="disponible" value="on"
                            <%= (!esEdicion || p.isDisponible()) ? "checked" : "" %>>
                        Disponible para la venta
                    </label>
                </div>
            </div>

            <div style="display:flex; gap:10px; margin-top:16px;">
                <button type="submit" class="btn btn-primary">Guardar</button>
                <a href="PlatoServlet" class="btn btn-secondary">Cancelar</a>
            </div>
        </form>
    </div>
</div>

</body>
</html>
