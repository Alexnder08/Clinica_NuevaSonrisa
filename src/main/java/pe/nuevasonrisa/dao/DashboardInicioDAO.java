package pe.nuevasonrisa.dao;

import pe.nuevasonrisa.model.Auditoria;
import pe.nuevasonrisa.model.DashboardResumen;
import pe.nuevasonrisa.model.HistorialAcceso;
import pe.nuevasonrisa.model.CitaHoy;

import java.util.List;

public interface DashboardInicioDAO {

    int contarCitasHoy();
    int contarPendientesHoy();
    int contarRealizadasHoy();
    int contarCanceladasHoy();

    DashboardResumen obtenerResumen();

    List<Auditoria> obtenerUltimasAcciones();

    List<HistorialAcceso> obtenerUltimosAccesos();

    List<CitaHoy> obtenerCitaHoy();
}