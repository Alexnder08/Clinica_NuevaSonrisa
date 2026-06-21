package pe.nuevasonrisa.service;

import pe.nuevasonrisa.dao.CitaDAO;
import pe.nuevasonrisa.dao.impl.ServicioDAOImpl;
import pe.nuevasonrisa.model.Cita;
import pe.nuevasonrisa.model.CitaTabla;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

public class CitaService {

    private final CitaDAO dao;
    private final ServicioService servicioService;

    public CitaService(CitaDAO dao) {
        this(dao, new ServicioService(new ServicioDAOImpl()));
    }

    public CitaService(CitaDAO dao, ServicioService servicioService) {
        this.dao = dao;
        this.servicioService = servicioService;
    }

    public List<CitaTabla> obtenerCitas() {
        return dao.listarCitas();
    }

    public boolean crearCita(Cita cita) {
        return dao.crearCita(cita);
    }

    public boolean cancelarCita(int citaId, String motivo) {
        return dao.cambiarEstado(citaId, "Cancelado", motivo);
    }

    public boolean cambiarEstado(int citaId, String estado) {
        return dao.cambiarEstado(citaId, estado, null);
    }

    public String validarCita(Cita cita) {
        try {
            return validarCitaInterna(cita, null);
        } catch (IllegalStateException e) {
            return e.getMessage();
        }
    }

    public String validarEdicionCita(Cita cita) {
        if ("Realizado".equalsIgnoreCase(cita.getEstado())
                || "Cancelado".equalsIgnoreCase(cita.getEstado())
                || "No asistió".equalsIgnoreCase(cita.getEstado())) {
            return null;
        }

        try {
            return validarCitaInterna(cita, cita.getId());
        } catch (IllegalStateException e) {
            return e.getMessage();
        }
    }

    private String validarCitaInterna(Cita cita, Integer citaIdExcluir) {
        if (!servicioService.servicioAsignadoADoctor(cita.getDoctorId(), cita.getServicioId())) {
            return "El servicio seleccionado no está asignado al odontólogo.";
        }

        if (!dao.dentroHorarioDoctor(
                cita.getDoctorId(),
                cita.getFecha(),
                cita.getHora(),
                cita.getDuracion()
        )) {
            return "El odontólogo no atiende en la fecha u hora seleccionada.";
        }

        if (!dao.doctorDisponible(
                cita.getDoctorId(),
                cita.getFecha(),
                cita.getHora(),
                cita.getDuracion(),
                citaIdExcluir
        )) {
            return "El odontólogo ya tiene una cita registrada en ese horario.";
        }

        if (!dao.pacienteDisponible(
                cita.getPacienteId(),
                cita.getFecha(),
                cita.getHora(),
                cita.getDuracion(),
                citaIdExcluir
        )) {
            return "El paciente ya tiene una cita registrada en ese horario.";
        }

        return null;
    }

    public boolean actualizarCita(Cita cita) {
        return dao.actualizarCita(cita);
    }

    public List<LocalTime> obtenerHorasDisponibles(
            int pacienteId,
            int doctorId,
            LocalDate fecha,
            int duracionMinutos
    ) {
        return dao.listarHorasDisponibles(pacienteId, doctorId, fecha, duracionMinutos);
    }
}
