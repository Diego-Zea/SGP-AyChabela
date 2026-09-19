package capaEntidad;

public class Insumo {

    private int id;
    private String nombre;
    private String unidad;      
    private double stock;
    private double stockMinimo;
    private boolean activo;

    public Insumo() {}

    public Insumo(int id, String nombre, String unidad, double stock,
                  double stockMinimo, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.unidad = unidad;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.activo = activo;
    }

    public boolean isStockBajo() {
        return stock <= stockMinimo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public double getStock() { return stock; }
    public void setStock(double stock) { this.stock = stock; }

    public double getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(double stockMinimo) { this.stockMinimo = stockMinimo; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
