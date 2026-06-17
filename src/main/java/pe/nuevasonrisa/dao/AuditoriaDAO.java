package pe.nuevasonrisa.dao;

import pe.nuevasonrisa.model.Auditoria;
import java.util.List;

public interface AuditoriaDAO {

    void registrar(String usuario, String accion, String modulo, String detalle);

    List<Auditoria> listar();
}