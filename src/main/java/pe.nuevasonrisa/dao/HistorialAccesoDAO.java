package pe.nuevasonrisa.dao;

import pe.nuevasonrisa.model.HistorialAcceso;
import java.util.List;

public interface HistorialAccesoDAO {

    void registrar(
            String usuario,
            String rol,
            String estado
    );

    List<HistorialAcceso> listar();

}