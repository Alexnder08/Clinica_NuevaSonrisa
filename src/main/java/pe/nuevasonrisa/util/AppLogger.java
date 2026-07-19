package pe.nuevasonrisa.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Punto comun para registrar fallos tecnicos sin exponer informacion sensible.
 */
public final class AppLogger {

    private static final Map<String, String> MENSAJES_EN_ESPANOL = Map.ofEntries(
            Map.entry("Database connection test failed.", "Falló la prueba de conexión a la base de datos."),
            Map.entry("Could not export Excel file.", "No se pudo exportar el archivo Excel."),
            Map.entry("Unhandled error while completing the operation.", "No se pudo completar la operación."),
            Map.entry("Could not save appointment attachment.", "No se pudo guardar el adjunto de la cita."),
            Map.entry("Could not list appointment attachments.", "No se pudieron listar los adjuntos de la cita."),
            Map.entry("Could not download appointment attachment.", "No se pudo descargar el adjunto de la cita."),
            Map.entry("Could not calculate available appointment times.", "No se pudieron calcular los horarios disponibles."),
            Map.entry("Could not delete service.", "No se pudo eliminar el servicio."),
            Map.entry("Could not look up user by email.", "No se pudo buscar el usuario por correo.")
    );

    private AppLogger() {
    }

    public static Logger getLogger(Class<?> source) {
        return LoggerFactory.getLogger(source);
    }

    public static void error(Class<?> source, String message, Throwable error) {
        getLogger(source).error(MENSAJES_EN_ESPANOL.getOrDefault(message, message));
    }
}
