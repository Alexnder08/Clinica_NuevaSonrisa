package pe.nuevasonrisa.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CitaDoctorTabla {

    private int id;
    private String paciente;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado;
    private String servicio;
    private String motivoConsulta;
    private String notas;

    public CitaDoctorTabla(
            int id,
            String paciente,
            LocalDate fecha,
            LocalTime hora,
            String estado,
            String servicio,
            String motivoConsulta,
            String notas
    ) {
        this.id = id;
        this.paciente = paciente;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.servicio = servicio;
        this.motivoConsulta = motivoConsulta;
        this.notas = notas;
    }

    public int getId() { return id; }
    public String getPaciente() { return paciente; }
    public LocalDate getFecha() { return fecha; }

    public String getHora() {
        return hora.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String getEstado() { return estado; }
    public String getServicio() { return servicio; }
    public String getMotivoConsulta() { return motivoConsulta; }
    public String getNotas() { return notas; }
}