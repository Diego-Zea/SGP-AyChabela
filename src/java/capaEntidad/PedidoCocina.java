package capaEntidad;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PedidoCocina {

    private int id;
    private Date fechaHora;
    private String estado;
    private double total;
    private String notas;
    private int mesaNumero;
    private String usuarioNombre;
    private int minutos;                       // tiempo transcurrido
    private List<DetallePedido> items = new ArrayList<>();

    public PedidoCocina() {}

    /**
     * Semaforo de la tarjeta segun el tiempo de espera.
     * Verde < 10 min, ambar < 20 min, rojo a partir de 20.
     */
    public String getColorTiempo() {
        if (minutos < 10) return "tiempo-ok";
        if (minutos < 20) return "tiempo-alerta";
        return "tiempo-critico";
    }

    /** Estado al que pasa el pedido cuando cocina pulsa el boton. */
    public String getSiguienteEstado() {
        if ("PENDIENTE".equals(estado))      return "EN_PREPARACION";
        if ("EN_PREPARACION".equals(estado)) return "LISTO";
        if ("LISTO".equals(estado))          return "ENTREGADO";
        return null;
    }

    /** Texto del boton de avance. */
    public String getTextoBoton() {
        if ("PENDIENTE".equals(estado))      return "Empezar a preparar";
        if ("EN_PREPARACION".equals(estado)) return "Marcar como listo";
        if ("LISTO".equals(estado))          return "Marcar como entregado";
        return "";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getFechaHora() { return fechaHora; }
    public void setFechaHora(Date fechaHora) { this.fechaHora = fechaHora; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public int getMesaNumero() { return mesaNumero; }
    public void setMesaNumero(int mesaNumero) { this.mesaNumero = mesaNumero; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public int getMinutos() { return minutos; }
    public void setMinutos(int minutos) { this.minutos = minutos; }

    public List<DetallePedido> getItems() { return items; }
    public void setItems(List<DetallePedido> items) { this.items = items; }
}
