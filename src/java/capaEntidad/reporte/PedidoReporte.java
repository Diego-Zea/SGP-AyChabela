package capaEntidad.reporte;

import java.util.Date;

/** Una fila de la hoja "Detalle de pedidos" del Excel. */
public class PedidoReporte {

    private int id;
    private Date fechaHora;
    private int mesaNumero;
    private String usuarioNombre;
    private String estado;
    private double total;

    public PedidoReporte() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getFechaHora() { return fechaHora; }
    public void setFechaHora(Date fechaHora) { this.fechaHora = fechaHora; }

    public int getMesaNumero() { return mesaNumero; }
    public void setMesaNumero(int mesaNumero) { this.mesaNumero = mesaNumero; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
