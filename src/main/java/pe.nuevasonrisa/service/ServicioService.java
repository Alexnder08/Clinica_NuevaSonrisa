package pe.nuevasonrisa.service;

import pe.nuevasonrisa.dao.ServicioDAO;
import pe.nuevasonrisa.model.Servicio;
import pe.nuevasonrisa.model.ServicioTabla;

import java.util.List;

public class ServicioService {

    private final ServicioDAO dao;

    public ServicioService(ServicioDAO dao) {
        this.dao = dao;
    }

    public List<ServicioTabla> obtenerServicios() {
        return dao.listarServicios();
    }

    public List<ServicioTabla> obtenerServiciosPorDoctor(int doctorid) {
        return dao.listarServiciosPorDoctor(doctorid);
    }

    public boolean crearServicio(Servicio servicio) {
        return dao.crearServicio(servicio);
    }

    public boolean actualizarServicio(Servicio servicio) {
        return dao.actualizarServicio(servicio);
    }
}