package pe.nuevasonrisa.service;

public record ResultadoOperacion(boolean exitoso, String mensaje) {

    public static ResultadoOperacion exito(String mensaje) {
        return new ResultadoOperacion(true, mensaje);
    }

    public static ResultadoOperacion error(String mensaje) {
        return new ResultadoOperacion(false, mensaje);
    }
}
