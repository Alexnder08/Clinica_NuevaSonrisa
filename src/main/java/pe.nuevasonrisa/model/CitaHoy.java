package pe.nuevasonrisa.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CitaHoy {

    private String paciente;
    private String doctor;
    private String estado;
    private LocalTime hora;

    public CitaHoy(
            String paciente,
            String doctor,
            String estado,
            LocalTime hora
    ) {
        this.paciente = paciente;
        this.doctor = doctor;
        this.estado = estado;
        this.hora = hora;
    }

    public String getPaciente() {
        return paciente;
    }

    public String getDoctor() {
        return doctor;
    }

    public String getEstado() {
        return estado;
    }

    public String getHora() {

        return hora.format(
                DateTimeFormatter.ofPattern("HH:mm")
        );
    }
}