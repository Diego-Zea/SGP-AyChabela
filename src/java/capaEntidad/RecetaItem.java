package capaEntidad;

/**
 * Una linea de la receta de un plato: "1 Arroz con Pollo lleva 0.250 kg de Arroz".
 * Corresponde a una fila de la tabla plato_insumo.
 */
public class RecetaItem {

    private int id;
    private int platoId;
    private int insumoId;
    private String insumoNombre;
    private String unidad;
    private double cantidad;      // por 1 plato
    private double stock;         // stock actual del insumo (informativo)

    public RecetaItem() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPlatoId() { return platoId; }
    public void setPlatoId(int platoId) { this.platoId = platoId; }

    public int getInsumoId() { return insumoId; }
    public void setInsumoId(int insumoId) { this.insumoId = insumoId; }

    public String getInsumoNombre() { return insumoNombre; }
    public void setInsumoNombre(String insumoNombre) { this.insumoNombre = insumoNombre; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }

    public double getStock() { return stock; }
    public void setStock(double stock) { this.stock = stock; }
}
