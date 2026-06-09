package pe.nuevasonrisa;

import pe.nuevasonrisa.dao.UsuarioDAO;
import pe.nuevasonrisa.dao.impl.UsuarioDAOImpl;
import pe.nuevasonrisa.model.Usuario;
import pe.nuevasonrisa.service.AuthService;

import java.util.Optional;

public class TestLogin {

    public static void main(String[] args) {
        UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
        AuthService authService = new AuthService(usuarioDAO);

        Optional<Usuario> usuario = authService.login("admin", "123");

        if (usuario.isPresent()) {
            System.out.println("Login correcto");
            System.out.println("Usuario: " + usuario.get().getUsuario());
            System.out.println("Rol: " + usuario.get().getRol());
            System.out.println("Nombre: " + usuario.get().getNombreCompleto());
        } else {
            System.out.println("Login incorrecto");
        }
    }
}