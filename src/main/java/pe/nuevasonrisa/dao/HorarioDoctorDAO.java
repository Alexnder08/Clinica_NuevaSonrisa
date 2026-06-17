package pe.nuevasonrisa.dao;

import pe.nuevasonrisa.model.HorarioDoctorTabla;

import java.util.List;

public interface HorarioDoctorDAO {

    List<HorarioDoctorTabla> listarPorDoctor(int doctorId);

    boolean actualizarHorario(int id, int diaSemana, String horaInicio, String horaFin);

    boolean eliminarHorario(int id);

    boolean crearHorario(int doctorId, int diaSemana, String horaInicio, String horaFin);

    boolean existeCruceHorario(int doctorId, int diaSemana, String horaInicio, String horaFin);
}