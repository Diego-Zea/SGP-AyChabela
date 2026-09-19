package capaEntidad.reporte;

/** Una fila del ranking "Platos mas vendidos". */
public class PlatoVendido {

    private String plato;
    private String categoria;
    private int cantidad;
    private double total;

    public PlatoVendido() {}

    public String getPlato() { return plato; }
    public void setPlato(String plato) { this.plato = plato; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
