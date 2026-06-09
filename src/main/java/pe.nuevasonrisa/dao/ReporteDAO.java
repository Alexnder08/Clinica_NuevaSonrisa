package pe.nuevasonrisa.dao;

import pe.nuevasonrisa.model.ReporteCitasDoctor;
import pe.nuevasonrisa.model.ReporteServicio;
import pe.nuevasonrisa.model.ReporteEstado;

import java.util.List;

public interface ReporteDAO {

    List<ReporteCitasDoctor> reporteCitasDoctor();

    List<ReporteServicio> reporteServicios();

    List<ReporteEstado> reporteEstados();
}