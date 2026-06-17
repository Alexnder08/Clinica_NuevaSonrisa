package pe.nuevasonrisa.model;

import java.time.LocalTime;

public class HorarioDoctorTabla {

    private int id;
    private int doctorId;

    private String doctor;
    private String dia;

    private LocalTime horaInicio;
    private LocalTime horaFin;

    public HorarioDoctorTabla(
            int id,
            int doctorId,
            String doctor,
            String dia,
            LocalTime horaInicio,
            LocalTime horaFin
    ) {
        this.id = id;
        this.doctorId = doctorId;
        this.doctor = doctor;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public int getId() { return id; }

    public int getDoctorId() { return doctorId; }

    public String getDoctor() { return doctor; }

    public String getDia() { return dia; }

    public LocalTime getHoraInicio() { return horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }

}
