package pe.nuevasonrisa.service;

import pe.nuevasonrisa.dao.DashboardInicioDAO;
import pe.nuevasonrisa.model.DashboardResumen;
import pe.nuevasonrisa.model.Auditoria;
import pe.nuevasonrisa.model.HistorialAcceso;
import pe.nuevasonrisa.model.CitaHoy;
import java.util.List;

public class DashboardInicioService {

    private final DashboardInicioDAO dao;

    public DashboardInicioService(
            DashboardInicioDAO dao
    ) {
        this.dao = dao;
    }

    public DashboardResumen obtenerResumen() {
        return dao.obtenerResumen();
    }

    public List<Auditoria> obtenerUltimasAcciones() {
        return dao.obtenerUltimasAcciones();
    }

    public List<HistorialAcceso> obtenerUltimosAccesos() {
        return dao.obtenerUltimosAccesos();
    }

    public List<CitaHoy> obtenerCitasHoy(){
        return dao.obtenerCitaHoy();
    }

    public int contarCitasHoy() {
        return dao.contarCitasHoy();
    }

    public int contarPendientesHoy() {
        return dao.contarPendientesHoy();
    }

    public int contarRealizadasHoy() {
        return dao.contarRealizadasHoy();
    }

    public int contarCanceladasHoy() {
        return dao.contarCanceladasHoy();
    }

    public List<CitaHoy> obtenerCitaHoy() {
        return dao.obtenerCitaHoy();
    }

}