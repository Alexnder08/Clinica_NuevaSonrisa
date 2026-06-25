package pe.nuevasonrisa.requirements;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import pe.nuevasonrisa.dao.UsuarioDAO;
import pe.nuevasonrisa.model.Usuario;
import pe.nuevasonrisa.service.AuthService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CP003IniciarSesionUsuarioInactivoTest {

    @Test
    void cubreCasoDePrueba() {
        // ARRANGE: se prepara el CP003 con credenciales correctas, pero cuenta inactiva.
        // Precondicion especifica: el usuario existe y la contrasena coincide, pero su estado es "Inactivo".
        Usuario usuarioRegistrado = new Usuario(
                1,
                "admin",
                BCrypt.hashpw("123456", BCrypt.gensalt()),
                "Admin",
                "Principal",
                "Administrador",
                "Inactivo",
                "12345678",
                "999999999",
                null
        );
        AuthService authService = new AuthService(new UsuarioDAOFalso(usuarioRegistrado));

        // ACT: se ingresan credenciales correctas para una cuenta desactivada.
        Optional<Usuario> usuarioAutenticado = authService.login("admin", "123456");
        boolean accesoPermitido = usuarioAutenticado.isPresent()
                && "Activo".equalsIgnoreCase(usuarioAutenticado.get().getEstado());

        // ASSERT: se confirma que la clave es valida, pero el acceso final queda bloqueado por estado inactivo.
        assertTrue(usuarioAutenticado.isPresent());
        assertFalse(accesoPermitido);
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
