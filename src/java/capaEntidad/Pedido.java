package capaEntidad;

import java.sql.Timestamp;

public class Pedido {
    private int id;
    private Timestamp fechaHora;
    private int mesaId;
    private int mesaNumero;
    private int usuarioId;
    private String usuarioNombre;
    private String estado; 
    private double total;
    private String notas;

    public Pedido() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Timestamp getFechaHora() { return fechaHora; }
    public void setFechaHora(Timestamp fechaHora) { this.fechaHora = fechaHora; }

    public int getMesaId() { return mesaId; }
    public void setMesaId(int mesaId) { this.mesaId = mesaId; }

    public int getMesaNumero() { return mesaNumero; }
    public void setMesaNumero(int mesaNumero) { this.mesaNumero = mesaNumero; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
