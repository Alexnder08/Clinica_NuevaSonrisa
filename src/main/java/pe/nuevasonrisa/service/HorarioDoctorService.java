package pe.nuevasonrisa.service;

import pe.nuevasonrisa.dao.HorarioDoctorDAO;
import pe.nuevasonrisa.model.HorarioDoctorTabla;

import java.time.LocalTime;
import java.util.List;

public class HorarioDoctorService {

    private final HorarioDoctorDAO dao;

    public HorarioDoctorService(HorarioDoctorDAO dao) {
        this.dao = dao;
    }

    public List<HorarioDoctorTabla> listarPorDoctor(int doctorId) {
        return dao.listarPorDoctor(doctorId);
    }

    public boolean actualizarHorario(int id, int diaSemana, String horaInicio, String horaFin) {
        return dao.actualizarHorario(id, diaSemana, horaInicio, horaFin);
    }

    public boolean eliminarHorario(int id) {
        return dao.eliminarHorario(id);
    }

    public String validarNuevoHorario(int doctorId, int diaSemana, String horaInicio, String horaFin) {
        String validacionFormato = validarFormatoHorario(doctorId, diaSemana, horaInicio, horaFin);
        if (validacionFormato != null) {
            return validacionFormato;
        }

        if (dao.existeCruceHorario(doctorId, diaSemana, horaInicio, horaFin)) {
            return "El horario se cruza con otro horario existente del odontólogo.";
        }

        return null;
    }

    public String validarFormatoHorario(int doctorId, int diaSemana, String horaInicio, String horaFin) {
        if (doctorId <= 0) {
            return "Seleccione un odontólogo.";
        }

        if (diaSemana < 1 || diaSemana > 7) {
            return "Seleccione un día válido.";
        }

        if (horaInicio == null || horaInicio.isBlank() || horaFin == null || horaFin.isBlank()) {
            return "Seleccione hora de inicio y hora de fin.";
        }

        try {
            LocalTime.parse(horaInicio);
            LocalTime.parse(horaFin);
        } catch (Exception e) {
            return "Las horas deben tener formato HH:mm. Ejemplo: 09:00";
        }

        if (horaInicio.compareTo(horaFin) >= 0) {
            return "La hora de inicio debe ser menor que la hora de fin.";
        }

        return null;
    }

    public boolean crearHorario(int doctorId, int diaSemana, String horaInicio, String horaFin) {
        return dao.crearHorario(doctorId, diaSemana, horaInicio, horaFin);
    }
}
