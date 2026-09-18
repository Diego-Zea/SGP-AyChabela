package capaEntidad.reporte;

/** Las tres cifras grandes de la cabecera del reporte. */
public class ResumenVentas {

    private int totalPedidos;
    private double totalVentas;
    private double ticketPromedio;

    public ResumenVentas() {}

    public ResumenVentas(int totalPedidos, double totalVentas, double ticketPromedio) {
        this.totalPedidos = totalPedidos;
        this.totalVentas = totalVentas;
        this.ticketPromedio = ticketPromedio;
    }

    public int getTotalPedidos() { return totalPedidos; }
    public void setTotalPedidos(int totalPedidos) { this.totalPedidos = totalPedidos; }

    public double getTotalVentas() { return totalVentas; }
    public void setTotalVentas(double totalVentas) { this.totalVentas = totalVentas; }

    public double getTicketPromedio() { return ticketPromedio; }
    public void setTicketPromedio(double ticketPromedio) { this.ticketPromedio = ticketPromedio; }
}
