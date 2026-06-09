package pe.nuevasonrisa.model;

public class ReporteCitasDoctor {

    private int doctorId;
    private String doctor;
    private int totalCitas;
    private int realizadas;
    private int canceladas;
    private int noAsistio;

    public ReporteCitasDoctor(int doctorId, String doctor, int totalCitas,
                              int realizadas, int canceladas, int noAsistio) {
        this.doctorId = doctorId;
        this.doctor = doctor;
        this.totalCitas = totalCitas;
        this.realizadas = realizadas;
        this.canceladas = canceladas;
        this.noAsistio = noAsistio;
    }

    public int getDoctorId() { return doctorId; }
    public String getDoctor() { return doctor; }
    public int getTotalCitas() { return totalCitas; }
    public int getRealizadas() { return realizadas; }
    public int getCanceladas() { return canceladas; }
    public int getNoAsistio() { return noAsistio; }
}