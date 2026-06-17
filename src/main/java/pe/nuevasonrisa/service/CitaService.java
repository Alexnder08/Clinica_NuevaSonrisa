package pe.nuevasonrisa.service;

import pe.nuevasonrisa.dao.CitaDAO;
import pe.nuevasonrisa.dao.impl.ServicioDAOImpl;
import pe.nuevasonrisa.model.Cita;
import pe.nuevasonrisa.model.CitaTabla;

import java.util.List;

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

    public String validarCita(Cita cita) {
        return validarCitaInterna(cita, null);
    }

    public String validarEdicionCita(Cita cita) {
        if ("Realizado".equalsIgnoreCase(cita.getEstado())
                || "Cancelado".equalsIgnoreCase(cita.getEstado())
                || "No asistió".equalsIgnoreCase(cita.getEstado())) {
            return null;
        }

        return validarCitaInterna(cita, cita.getId());
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
}
