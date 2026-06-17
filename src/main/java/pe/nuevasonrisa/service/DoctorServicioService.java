package pe.nuevasonrisa.service;

import pe.nuevasonrisa.dao.DoctorServicioDAO;

import java.util.List;

public class DoctorServicioService {

    private final DoctorServicioDAO dao;

    public DoctorServicioService(DoctorServicioDAO dao) {
        this.dao = dao;
    }

    public List<Integer> obtenerServiciosDoctor(int doctorId) {
        return dao.obtenerServiciosDoctor(doctorId);
    }

    public boolean guardarServiciosDoctor(int doctorId, List<Integer> serviciosIds) {
        return dao.guardarServiciosDoctor(doctorId, serviciosIds);
    }

    public String validarServiciosDoctor(int doctorId, List<Integer> serviciosIds) {
        if (serviciosIds == null || serviciosIds.isEmpty()) {
            return "Debe asignar al menos un servicio al odontólogo.";
        }

        if (dao.existeOtroDoctorConMismosServicios(doctorId, serviciosIds)) {
            return "Otro odontólogo ya tiene exactamente los mismos servicios asignados.";
        }

        return null;
    }
}
