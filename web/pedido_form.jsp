<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, capaEntidad.Mesa, capaEntidad.Plato" %>
<% request.setAttribute("paginaActual", "pedidos"); %>
<%
    List<Mesa>  mesas  = (List<Mesa>)  request.getAttribute("mesas");
    List<Plato> platos = (List<Plato>) request.getAttribute("platos");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Nuevo Pedido - SGP Ay Chabela</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="contenedor">
    <h2 class="titulo-pagina">Registrar Nuevo Pedido</h2>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error"><%= request.getAttribute("error") %></div>
    <% } %>

    <div class="card">
        <form action="PedidoServlet" method="post">

            <div class="form-grid-2">
                <div class="form-group">
                    <label for="mesaId">Mesa *</label>
                    <select id="mesaId" name="mesaId" required>
                        <option value="">-- Seleccionar mesa --</option>
                        <% if (mesas != null) {
                              for (Mesa m : mesas) {
                                  if (!"OCUPADA".equals(m.getEstado())) {
                        %>
                            <option value="<%= m.getId() %>">
                                Mesa <%= m.getNumero() %> (<%= m.getCapacidad() %> personas - <%= m.getEstado() %>)
                            </option>
                        <%        }
                              }
                           }
                        %>
                    </select>
                </div>

                <div class="form-group">
                    <label for="notas">Notas (opcional)</label>
                    <input type="text" id="notas" name="notas" placeholder="Ej. sin sal, sin picante...">
                </div>
            </div>

            <h3 style="margin-top:20px; margin-bottom:12px; color:#1A1A2E;">Platos del pedido</h3>
            <p style="font-size:13px; color:#64748B; margin-bottom:14px;">
                Selecciona los platos y la cantidad. Puedes agregar mas filas con el boton de abajo.
            </p>

            <div id="contenedorDetalle">
                <!-- Fila inicial -->
                <div class="fila-detalle">
                    <div class="form-group">
                        <label>Plato</label>
                        <select name="platoId">
                            <option value="">-- Seleccionar --</option>
                            <% if (platos != null) {
                                  for (Plato pl : platos) {
                            %>
                                <option value="<%= pl.getId() %>">
                                    <%= pl.getNombre() %> (S/ <%= String.format("%.2f", pl.getPrecio()) %>)
                                </option>
                            <%   }
                               }
                            %>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Cantidad</label>
                        <input type="number" name="cantidad" value="1" min="1">
                    </div>
                    <div style="display:flex; align-items:end; padding-bottom:14px;">
                        <button type="button" class="btn btn-danger btn-sm" onclick="quitarFila(this)">X</button>
                    </div>
                </div>
            </div>

            <button type="button" class="btn btn-secondary" onclick="agregarFila()">+ Agregar plato</button>

            <div style="display:flex; gap:10px; margin-top:24px;">
                <button type="submit" class="btn btn-primary">Registrar Pedido</button>
                <a href="PedidoServlet" class="btn btn-secondary">Cancelar</a>
            </div>
        </form>
    </div>
</div>

<script>
    // Funcion para agregar una nueva fila de plato + cantidad
    function agregarFila() {
        var contenedor = document.getElementById("contenedorDetalle");
        var primera = contenedor.firstElementChild;
        var copia = primera.cloneNode(true);
        // Resetear valores
        copia.querySelector("select").value = "";
        copia.querySelector("input[name='cantidad']").value = 1;
        contenedor.appendChild(copia);
    }

    function quitarFila(boton) {
        var contenedor = document.getElementById("contenedorDetalle");
        if (contenedor.children.length > 1) {
            boton.closest(".fila-detalle").remove();
        }
    }
</script>

</body>
</html>
