package pe.nuevasonrisa.dao;

import pe.nuevasonrisa.model.Usuario;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;

public interface UsuarioDAO {
    Optional<Usuario> buscarPorUsuario(String usuario);
    Optional<Usuario> buscarPorEmail(String email);
    boolean crearUsuario(Usuario usuario);
    boolean actualizarPasswordPorId(int usuarioId, String passwordHash);
    boolean cambiarPassword(
            String usuario,
            String passwordActual,
            String passwordNueva
    );
}
