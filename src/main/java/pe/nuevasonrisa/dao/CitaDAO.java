package pe.nuevasonrisa.dao;

import pe.nuevasonrisa.model.Cita;
import pe.nuevasonrisa.model.CitaTabla;
import pe.nuevasonrisa.model.RecordatorioCita;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface CitaDAO {

    List<CitaTabla> listarCitas();

    boolean crearCita(Cita cita);

    boolean actualizarCita(Cita cita);

    boolean doctorDisponible(int doctorId, LocalDate fecha, LocalTime hora, int duracionMinutos, Integer citaIdExcluir);

    boolean pacienteDisponible(int pacienteId, LocalDate fecha, LocalTime hora, int duracionMinutos, Integer citaIdExcluir);

    boolean dentroHorarioDoctor(int doctorId, LocalDate fecha, LocalTime hora, int duracionMinutos);

    boolean cambiarEstado(int citaid, String estado, String motivoCancelacion);

    List<RecordatorioCita> listarCitasPendientesParaRecordatorio(LocalDate desde, LocalDate hasta);

    boolean marcarRecordatorioEnviado(int citaId, LocalDateTime enviadoEn);

}
