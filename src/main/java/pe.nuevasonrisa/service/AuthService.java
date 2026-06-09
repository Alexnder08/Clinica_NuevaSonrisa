package pe.nuevasonrisa.service;

import org.mindrot.jbcrypt.BCrypt;
import pe.nuevasonrisa.dao.UsuarioDAO;
import pe.nuevasonrisa.model.Usuario;

import java.util.Optional;

public class AuthService {

    private final UsuarioDAO usuarioDAO;

    public AuthService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public Optional<Usuario> login(String usuario, String password) {
        if (usuario == null || usuario.isBlank() || password == null || password.isBlank()) {
            return Optional.empty();
        }

        Optional<Usuario> encontrado = usuarioDAO.buscarPorUsuario(usuario.trim());

        if (encontrado.isEmpty()) {
            return Optional.empty();
        }

        Usuario user = encontrado.get();

        String hash = user.getPassword();
        boolean valido;

        if (hash.startsWith("$2b$") || hash.startsWith("$2y$")) {
            hash = "$2a$" + hash.substring(4);
        }

        if (hash.startsWith("$2a$")) {
            valido = BCrypt.checkpw(password, hash);
        } else {
            valido = password.equals(hash);
        }

        return valido ? Optional.of(user) : Optional.empty();
    }

    public boolean cambiarPassword(
            String usuario,
            String actual,
            String nueva
    ) {
        return usuarioDAO.cambiarPassword(
                usuario,
                actual,
                nueva
        );
    }

}