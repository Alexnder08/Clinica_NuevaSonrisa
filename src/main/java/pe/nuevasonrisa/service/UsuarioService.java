package pe.nuevasonrisa.service;

import pe.nuevasonrisa.dao.UsuarioGestionDAO;
import pe.nuevasonrisa.model.UsuarioTabla;
import pe.nuevasonrisa.model.Usuario;

import java.util.List;

public class UsuarioService {

    private final UsuarioGestionDAO dao;

    public UsuarioService(UsuarioGestionDAO dao) {
        this.dao = dao;
    }

    public List<UsuarioTabla> obtenerUsuarios() {
        return dao.listarUsuarios();
    }

    public boolean actualizarUsuario(Usuario usuario) {
        return dao.actualizarUsuario(usuario);
    }

    public boolean cambiarEstadoUsuario(int idUsuario, String estadoActual) {
        String nuevoEstado = "Activo".equalsIgnoreCase(estadoActual)
                ? "Inactivo"
                : "Activo";

        return dao.cambiarEstadoUsuario(idUsuario, nuevoEstado);
    }
}