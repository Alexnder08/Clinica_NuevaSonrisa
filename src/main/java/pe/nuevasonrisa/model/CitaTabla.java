package pe.nuevasonrisa.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class CitaTabla {

    private int id;

    private int pacienteId;
    private int doctorId;
    private int servicioId;

    private String paciente;
    private String doctor;
    private String servicio;

    private LocalDate fecha;
    private LocalTime hora;

    private String estado;
    private String motivoConsulta;
    private String notas;

    public CitaTabla(int id,
                     int pacienteId,
                     int doctorId,
                     int servicioId,
                     String paciente,
                     String doctor,
                     String servicio,
                     LocalDate fecha,
                     LocalTime hora,
                     String estado,
                     String motivoConsulta,
                     String notas) {

        this.id = id;
        this.pacienteId = pacienteId;
        this.doctorId = doctorId;
        this.servicioId = servicioId;
        this.paciente = paciente;
        this.doctor = doctor;
        this.servicio = servicio;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.motivoConsulta = motivoConsulta;
        this.notas = notas;
    }

    public int getId() { return id; }

    public int getPacienteId() { return pacienteId; }
    public int getDoctorId() { return doctorId; }
    public int getServicioId() { return servicioId; }

    public String getPaciente() { return paciente; }
    public String getDoctor() { return doctor; }
    public String getServicio() { return servicio; }

    public LocalDate getFecha() { return fecha; }
    public LocalTime getHora() { return hora; }

    public String getEstado() { return estado; }
    public String getMotivoConsulta() { return motivoConsulta; }
    public String getNotas() { return notas; }
}