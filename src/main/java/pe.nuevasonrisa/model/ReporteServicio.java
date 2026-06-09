package pe.nuevasonrisa.model;

public class ReporteServicio {

    private int servicioId;
    private String servicio;
    private int totalCitas;
    private int realizadas;
    private int canceladas;

    public ReporteServicio(int servicioId, String servicio, int totalCitas,
                           int realizadas, int canceladas) {
        this.servicioId = servicioId;
        this.servicio = servicio;
        this.totalCitas = totalCitas;
        this.realizadas = realizadas;
        this.canceladas = canceladas;
    }

    public int getServicioId() { return servicioId; }
    public String getServicio() { return servicio; }
    public int getTotalCitas() { return totalCitas; }
    public int getRealizadas() { return realizadas; }
    public int getCanceladas() { return canceladas; }
}