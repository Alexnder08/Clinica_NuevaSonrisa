package pe.nuevasonrisa.requirements;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import pe.nuevasonrisa.dao.UsuarioDAO;
import pe.nuevasonrisa.model.Usuario;
import pe.nuevasonrisa.service.AuthService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CP002IniciarSesionCredencialesIncorrectasTest {

    @Test
    void cubreCasoDePrueba() {
        // ARRANGE: se prepara el CP002 con un usuario existente y una clave real guardada.
        // Precondicion especifica: el usuario "admin" existe, pero la prueba enviara otra clave.
        Usuario usuarioRegistrado = new Usuario(
                1,
                "admin",
                BCrypt.hashpw("123456", BCrypt.gensalt()),
                "Admin",
                "Principal",
                "Administrador",
                "Activo",
                "12345678",
                "999999999",
                null
        );
        AuthService authService = new AuthService(new UsuarioDAOFalso(usuarioRegistrado));

        // ACT: se intenta iniciar sesion con usuario correcto, pero contrasena incorrecta.
        Optional<Usuario> resultado = authService.login("admin", "clave_incorrecta");

        // ASSERT: se confirma que el sistema rechaza el acceso.
        assertTrue(resultado.isEmpty());
    }

    private static class UsuarioDAOFalso implements UsuarioDAO {
        private final Usuario usuarioRegistrado;

        private UsuarioDAOFalso(Usuario usuarioRegistrado) {
            this.usuarioRegistrado = usuarioRegistrado;
        }

        @Override
        public Optional<Usuario> buscarPorUsuario(String usuario) {
            if (usuarioRegistrado.getUsuario().equals(usuario)) {
                return Optional.of(usuarioRegistrado);
            }
            return Optional.empty();
        }

        @Override
        public Optional<Usuario> buscarPorEmail(String email) {
            return Optional.empty();
        }

        @Override
        public boolean crearUsuario(Usuario usuario) {
            return false;
        }

        @Override
        public boolean actualizarPasswordPorId(int usuarioId, String passwordHash) {
            return false;
        }

        @Override
        public boolean cambiarPassword(String usuario, String passwordActual, String passwordNueva) {
            return false;
        }
    }
}
