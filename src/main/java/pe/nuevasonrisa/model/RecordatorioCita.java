package pe.nuevasonrisa.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class RecordatorioCita {

    private final int id;
    private final String paciente;
    private final String correo;
    private final String doctor;
    private final String servicio;
    private final LocalDate fecha;
    private final LocalTime hora;

    public RecordatorioCita(
            int id,
            String paciente,
            String correo,
            String doctor,
            String servicio,
            LocalDate fecha,
            LocalTime hora
    ) {
        this.id = id;
        this.paciente = paciente;
        this.correo = correo;
        this.doctor = doctor;
        this.servicio = servicio;
        this.fecha = fecha;
        this.hora = hora;
    }

    public int getId() {
        return id;
    }

    public String getPaciente() {
        return paciente;
    }

    public String getCorreo() {
        return correo;
    }

    public String getDoctor() {
        return doctor;
    }

    public String getServicio() {
        return servicio;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }
}
