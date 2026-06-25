package pe.nuevasonrisa.requirements;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import pe.nuevasonrisa.dao.UsuarioDAO;
import pe.nuevasonrisa.model.Usuario;
import pe.nuevasonrisa.service.AuthService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CP001IniciarSesionIniciarSesionCorrectamenteTest {

    @Test
    void cubreCasoDePrueba() {
        // ARRANGE: se prepara el CP001 para iniciar sesion correctamente.
        // Precondicion especifica: existe un usuario registrado, activo y con clave valida.
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

        // ACT: se ingresan datos correctos como lo haria el usuario en la pantalla de login.
        Optional<Usuario> resultado = authService.login("admin", "123456");

        // ASSERT: se confirma que el sistema autentica al usuario y conserva su rol.
        assertTrue(resultado.isPresent());
        assertEquals("Administrador", resultado.get().getRol());
        assertEquals("Activo", resultado.get().getEstado());
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
