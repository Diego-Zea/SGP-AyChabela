package capaEntidad.reporte;

/** Una fila de "Insumos consumidos", leida del kardex. */
public class InsumoConsumido {

    private String insumo;
    private String unidad;
    private double consumido;
    private double stockActual;
    private double stockMinimo;

    public InsumoConsumido() {}

    public boolean isStockBajo() { return stockActual <= stockMinimo; }

    public String getInsumo() { return insumo; }
    public void setInsumo(String insumo) { this.insumo = insumo; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public double getConsumido() { return consumido; }
    public void setConsumido(double consumido) { this.consumido = consumido; }

    public double getStockActual() { return stockActual; }
    public void setStockActual(double stockActual) { this.stockActual = stockActual; }

    public double getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(double stockMinimo) { this.stockMinimo = stockMinimo; }
}
