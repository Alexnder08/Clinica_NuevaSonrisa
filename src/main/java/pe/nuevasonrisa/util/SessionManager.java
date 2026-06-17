package pe.nuevasonrisa.util;

import pe.nuevasonrisa.model.Usuario;

public class SessionManager {

    private static Usuario usuarioActual;

    private SessionManager() {}

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }
}