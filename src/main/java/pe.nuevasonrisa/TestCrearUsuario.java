package pe.nuevasonrisa;

import pe.nuevasonrisa.dao.impl.UsuarioGestionDAOImpl;
import pe.nuevasonrisa.model.Usuario;

public class TestCrearUsuario {

    public static void main(String[] args) {

        Usuario usuario = new Usuario();

        usuario.setUsuario("doctor_test");
        usuario.setPassword("123456");
        usuario.setNombre("Carlos");
        usuario.setApellido("Perez");
        usuario.setDni("98765432");
        usuario.setCelular("999888777");
        usuario.setRol("Doctor");

        boolean creado =
                new UsuarioGestionDAOImpl()
                        .crearUsuario(usuario);

        System.out.println("Creado: " + creado);
    }
}