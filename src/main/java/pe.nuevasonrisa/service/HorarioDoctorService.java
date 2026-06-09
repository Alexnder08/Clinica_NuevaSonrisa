package pe.nuevasonrisa.service;

import pe.nuevasonrisa.dao.HorarioDoctorDAO;
import pe.nuevasonrisa.model.HorarioDoctorTabla;

import java.util.List;

public class HorarioDoctorService {

    private final HorarioDoctorDAO dao;

    public HorarioDoctorService(HorarioDoctorDAO dao) {
        this.dao = dao;
    }

    public List<HorarioDoctorTabla> listarPorDoctor(int doctorId) {
        return dao.listarPorDoctor(doctorId);
    }

    public boolean actualizarHorario(int id, int diaSemana, String horaInicio, String horaFin){
        return dao.actualizarHorario(id, diaSemana, horaInicio, horaFin);
    }
    public boolean eliminarHorario(int id){
        return dao.eliminarHorario(id);
    }

    public String validarNuevoHorario(int doctorId, int diaSemana, String horaInicio, String horaFin) {

        if (horaInicio == null || horaFin == null) {
            return "Seleccione hora de inicio y hora de fin.";
        }

        if (horaInicio.compareTo(horaFin) >= 0) {
            return "La hora de inicio debe ser menor que la hora de fin.";
        }

        if (dao.existeCruceHorario(doctorId, diaSemana, horaInicio, horaFin)) {
            return "El horario se cruza con otro horario existente del doctor.";
        }

        return null;
    }

    public boolean crearHorario(int doctorId, int diaSemana, String horaInicio, String horaFin) {
        return dao.crearHorario(doctorId, diaSemana, horaInicio, horaFin);
    }
}