package pe.nuevasonrisa.model;

public class DashboardResumen {

    private int pacientes;
    private int citasPendientes;
    private int citasHoy;
    private int odontologos;

    public DashboardResumen(
            int pacientes,
            int citasPendientes,
            int citasHoy,
            int odontologos
    ) {
        this.pacientes = pacientes;
        this.citasPendientes = citasPendientes;
        this.citasHoy = citasHoy;
        this.odontologos = odontologos;
    }

    public int getPacientes() {
        return pacientes;
    }

    public int getCitasPendientes() {
        return citasPendientes;
    }

    public int getCitasHoy() {
        return citasHoy;
    }

    public int getOdontologos() {
        return odontologos;
    }
}