package pe.nuevasonrisa.dao;

import pe.nuevasonrisa.model.CitaDoctorTabla;

import java.util.List;

public interface CitaDoctorDAO {

    List<CitaDoctorTabla> obtenerMisCitas(
            int doctorId
    );

    List<CitaDoctorTabla> obtenerCitasHoy(
            int doctorId
    );

    boolean actualizarNotas(
            int citaId,
            int doctorId,
            String notas
    );
}