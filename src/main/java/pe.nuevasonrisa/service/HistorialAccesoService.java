package pe.nuevasonrisa.service;

import pe.nuevasonrisa.dao.impl.HistorialAccesoDAOImpl;
import pe.nuevasonrisa.model.Usuario;
import pe.nuevasonrisa.util.SessionManager;
import pe.nuevasonrisa.model.HistorialAcceso;
import java.util.List;

public class HistorialAccesoService {

    private final HistorialAccesoDAOImpl dao =
            new HistorialAccesoDAOImpl();

    public List<HistorialAcceso> listar() {
        return dao.listar();
    }

    public void registrarLogin() {

        Usuario usuario =
                SessionManager.getUsuarioActual();

        if (usuario == null) return;

        dao.registrar(
                usuario.getUsuario(),
                usuario.getRol(),
                "LOGIN"
        );
    }
}