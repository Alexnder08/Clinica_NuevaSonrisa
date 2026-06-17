package pe.nuevasonrisa.service;

import pe.nuevasonrisa.dao.ReporteDAO;
import pe.nuevasonrisa.model.ReporteCitasDoctor;
import pe.nuevasonrisa.model.ReporteEstado;
import pe.nuevasonrisa.model.ReporteServicio;

import java.util.List;

public class ReporteService {

    private final ReporteDAO dao;

    public ReporteService(ReporteDAO dao) {
        this.dao = dao;
    }

    public List<ReporteCitasDoctor> obtenerReporteCitasDoctor() {
        return dao.reporteCitasDoctor();
    }

    public List<ReporteServicio> obtenerReporteServicios() {
        return dao.reporteServicios();
    }

    public List<ReporteEstado> obtenerReporteEstados() {
        return dao.reporteEstados();
    }
}