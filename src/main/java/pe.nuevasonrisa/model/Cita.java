package pe.nuevasonrisa.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Cita {

    private int id;
    private int pacienteId;
    private int doctorId;
    private int servicioId;
    private LocalDate fecha;
    private LocalTime hora;
    private int duracion;
    private String estado;
    private String notas;
    private String motivoConsulta;
    private String motivoCancelacion;

    public Cita() {}

    public int getId() { return id; }
    public int getPacienteId() { return pacienteId; }
    public int getDoctorId() { return doctorId; }
    public int getServicioId() { return servicioId; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHora() { return hora; }
    public int getDuracion() { return duracion; }
    public String getEstado() { return estado; }
    public String getNotas() { return notas; }
    public String getMotivoConsulta() { return motivoConsulta; }
    public String getMotivoCancelacion() { return motivoCancelacion; }

    public void setId(int id) { this.id = id; }
    public void setPacienteId(int pacienteId) { this.pacienteId = pacienteId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }
    public void setServicioId(int servicioId) { this.servicioId = servicioId; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public void setDuracion(int duracion) { this.duracion = duracion; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setNotas(String notas) { this.notas = notas; }
    public void setMotivoConsulta(String motivoConsulta) { this.motivoConsulta = motivoConsulta; }
    public void setMotivoCancelacion(String motivoCancelacion) { this.motivoCancelacion = motivoCancelacion; }
}