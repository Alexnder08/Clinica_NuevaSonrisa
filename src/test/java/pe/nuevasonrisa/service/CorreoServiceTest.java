package pe.nuevasonrisa.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CorreoServiceTest {

    @Test
    void normalizaUnDestinatarioValido() {
        assertEquals(
                "paciente@correo.com",
                CorreoService.normalizarDestinatario("  paciente@correo.com  ")
        );
    }

    @Test
    void rechazaDestinatariosInvalidos() {
        assertNull(CorreoService.normalizarDestinatario(null));
        assertNull(CorreoService.normalizarDestinatario(""));
        assertNull(CorreoService.normalizarDestinatario("Nombre sin correo"));
    }
}
