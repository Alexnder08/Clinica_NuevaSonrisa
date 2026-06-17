package pe.nuevasonrisa.model;

public class ReporteEstado {

    private String estado;
    private int total;

    public ReporteEstado(String estado, int total) {
        this.estado = estado;
        this.total = total;
    }

    public String getEstado() { return estado; }
    public int getTotal() { return total; }
}