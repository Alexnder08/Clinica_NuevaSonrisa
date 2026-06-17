package pe.nuevasonrisa.service;

import pe.nuevasonrisa.model.Auditoria;
import pe.nuevasonrisa.dao.AuditoriaDAO;
import pe.nuevasonrisa.dao.impl.AuditoriaDAOImpl;
import pe.nuevasonrisa.model.Usuario;
import pe.nuevasonrisa.util.SessionManager;

import java.util.List;

public class AuditoriaService {

    private final AuditoriaDAO dao = new AuditoriaDAOImpl();

    public void registrar(String accion, String modulo, String detalle) {

        Usuario usuarioActual = SessionManager.getUsuarioActual();

        String usuario = usuarioActual != null
                ? usuarioActual.getUsuario()
                : "SISTEMA";

        dao.registrar(usuario, accion, modulo, detalle);
    }

    public List<Auditoria> listar() {
        return dao.listar();
    }
}