package pe.nuevasonrisa.service;

import pe.nuevasonrisa.dao.DoctorServicioDAO;

import java.util.List;

public class DoctorServicioService {

    private final DoctorServicioDAO dao;

    public DoctorServicioService(
            DoctorServicioDAO dao
    ) {
        this.dao = dao;
    }

    public List<Integer> obtenerServiciosDoctor(
            int doctorId
    ) {
        return dao.obtenerServiciosDoctor(doctorId);
    }

    public boolean guardarServiciosDoctor(
            int doctorId,
            List<Integer> serviciosIds
    ) {
        return dao.guardarServiciosDoctor(
                doctorId,
                serviciosIds
        );
    }
}