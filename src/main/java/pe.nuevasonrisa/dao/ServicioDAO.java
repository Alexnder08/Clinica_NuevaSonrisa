package pe.nuevasonrisa.dao;

import pe.nuevasonrisa.model.Servicio;
import pe.nuevasonrisa.model.ServicioTabla;

import java.util.List;

public interface ServicioDAO {

    List<ServicioTabla> listarServicios();

    boolean crearServicio(Servicio servicio);

    boolean actualizarServicio(Servicio servicio);
}