package pe.nuevasonrisa.service;

import pe.nuevasonrisa.dao.CitaDAO;
import pe.nuevasonrisa.model.Cita;
import pe.nuevasonrisa.model.CitaTabla;

import java.util.List;

public class CitaService {

    private final CitaDAO dao;

    public CitaService(CitaDAO dao) {
        this.dao = dao;
    }

    public List<CitaTabla> obtenerCitas() {
        return dao.listarCitas();
    }

    public boolean crearCita(Cita cita) {
        return dao.crearCita(cita);
    }

    public boolean cancelarCita(
            int citaId,
            String motivo
    ) {
        return dao.cambiarEstado(
                citaId,
                "Cancelado",
                motivo
        );
    }

    public String validarCita(Cita cita) {

        if (!dao.dentroHorarioDoctor(cita.getDoctorId(), cita.getFecha(), cita.getHora())) {
            return "El doctor no atiende en ese día u horario.";
        }

        if (!dao.doctorDisponible(cita.getDoctorId(), cita.getFecha(), cita.getHora())) {
            return "El doctor ya tiene una cita registrada en esa fecha y hora.";
        }

        if (!dao.pacienteDisponible(cita.getPacienteId(), cita.getFecha(), cita.getHora())) {
            return "El paciente ya tiene una cita registrada en esa fecha y hora.";
        }

        return null;
    }

    public String validarEdicionCita(Cita cita) {

        if (
                cita.getEstado().equalsIgnoreCase("Realizado")
                        || cita.getEstado().equalsIgnoreCase("Cancelado")
                        || cita.getEstado().equalsIgnoreCase("No asistió")
        ) {
            return null;
        }

        if (!dao.dentroHorarioDoctor(
                cita.getDoctorId(),
                cita.getFecha(),
                cita.getHora()
        )) {
            return "El doctor no atiende en ese día u horario.";
        }

        return null;
    }

    public boolean actualizarCita(Cita cita) {
        return dao.actualizarCita(cita);
    }

}