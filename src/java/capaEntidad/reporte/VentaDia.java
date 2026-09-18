package capaEntidad.reporte;

import java.util.Date;

/** Una barra del grafico "Ventas por dia". */
public class VentaDia {

    private Date fecha;
    private int pedidos;
    private double total;

    public VentaDia() {}

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public int getPedidos() { return pedidos; }
    public void setPedidos(int pedidos) { this.pedidos = pedidos; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
