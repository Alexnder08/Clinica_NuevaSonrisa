package pe.nuevasonrisa.util;

import pe.nuevasonrisa.model.Usuario;
import org.slf4j.Logger;
import pe.nuevasonrisa.service.AuditoriaService;

public class SessionManager {

    private static Usuario usuarioActual;
    private static final Logger LOGGER = AppLogger.getLogger(SessionManager.class);

    private SessionManager() {}

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static void cerrarSesion() {
        if (usuarioActual != null) {
            new AuditoriaService().registrar("LOGOUT", "SEGURIDAD", "Sesion cerrada.");
            LOGGER.info("Sesion de usuario cerrada.");
        }
        usuarioActual = null;
    }
}
