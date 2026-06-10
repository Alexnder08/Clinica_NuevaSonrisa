package pe.nuevasonrisa.service;

import pe.nuevasonrisa.dao.CitaDoctorDAO;
import pe.nuevasonrisa.model.CitaDoctorTabla;

import java.util.List;

public class CitaDoctorService {

    private final CitaDoctorDAO dao;

    public CitaDoctorService(
            CitaDoctorDAO dao
    ) {
        this.dao = dao;
    }

    public List<CitaDoctorTabla> obtenerMisCitas(
            int doctorId
    ) {
        return dao.obtenerMisCitas(
                doctorId
        );
    }

    public List<CitaDoctorTabla> obtenerCitasHoy(
            int doctorId
    ) {
        return dao.obtenerCitasHoy(
                doctorId
        );
    }

    public boolean actualizarNotas(
            int citaId,
            int doctorId,
            String notas
    ) {
        return dao.actualizarNotas(
                citaId,
                doctorId,
                notas
        );
    }

}