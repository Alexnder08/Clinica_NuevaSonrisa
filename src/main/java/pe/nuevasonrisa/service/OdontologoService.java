package pe.nuevasonrisa.service;

import pe.nuevasonrisa.dao.OdontologoDAO;
import pe.nuevasonrisa.model.OdontologoTabla;

import java.util.List;

public class OdontologoService {

    private final OdontologoDAO dao;

    public OdontologoService(OdontologoDAO dao) {
        this.dao = dao;
    }

    public List<OdontologoTabla> obtenerOdontologos() {
        return dao.listarOdontologos();
    }

    public List<OdontologoTabla> obtenerDisponiblesParaCita() {
        return dao.listarDisponiblesParaCita();
    }
}
