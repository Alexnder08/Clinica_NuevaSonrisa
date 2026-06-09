package pe.nuevasonrisa.dao;

import pe.nuevasonrisa.model.Cita;
import pe.nuevasonrisa.model.CitaTabla;

import java.util.List;

public interface CitaDAO {

    List<CitaTabla> listarCitas();

    boolean crearCita(Cita cita);

    boolean actualizarCita(Cita cita);

    boolean doctorDisponible(int doctorId, java.time.LocalDate fecha, java.time.LocalTime hora);

    boolean pacienteDisponible(int pacienteId, java.time.LocalDate fecha, java.time.LocalTime hora);

    boolean dentroHorarioDoctor(int doctorId, java.time.LocalDate fecha, java.time.LocalTime hora);

    boolean cambiarEstado(int citaid, String estado, String motivoCancelacion);

}