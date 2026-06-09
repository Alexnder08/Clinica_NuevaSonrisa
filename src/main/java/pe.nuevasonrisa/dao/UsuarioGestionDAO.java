package pe.nuevasonrisa.dao;

import pe.nuevasonrisa.model.Usuario;
import pe.nuevasonrisa.model.UsuarioTabla;
import java.util.List;

public interface UsuarioGestionDAO {

    List<UsuarioTabla> listarUsuarios();

    boolean crearUsuario(Usuario usuario);

    boolean actualizarUsuario(Usuario usuario);

    boolean cambiarEstadoUsuario(int idUsuario, String nuevoEstado);

}