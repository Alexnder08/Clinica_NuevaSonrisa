package pe.nuevasonrisa.dao;

import java.util.List;

public interface DoctorServicioDAO {

    List<Integer> obtenerServiciosDoctor(int doctorId);

    boolean guardarServiciosDoctor(
            int doctorId,
            List<Integer> serviciosIds
    );

    boolean existeOtroDoctorConMismosServicios(
            int doctorId,
            List<Integer> serviciosIds
    );
}
