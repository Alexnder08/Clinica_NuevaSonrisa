package pe.nuevasonrisa.dao;

import pe.nuevasonrisa.model.Usuario;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;

public interface UsuarioDAO {
    Optional<Usuario> buscarPorUsuario(String usuario);
    boolean crearUsuario(Usuario usuario);
    boolean cambiarPassword(
            String usuario,
            String passwordActual,
            String passwordNueva
    );
}