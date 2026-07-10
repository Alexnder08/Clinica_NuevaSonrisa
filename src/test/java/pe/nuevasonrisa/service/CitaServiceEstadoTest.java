package pe.nuevasonrisa.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CitaServiceEstadoTest {

    private final CitaService service = new CitaService(null, null);

    @Test
    void permitePasarDePendienteAEnEspera() {
        LocalDate hoy = LocalDate.now(ZoneId.of("America/Lima"));
        assertNull(service.validarTransicionEstado("Pendiente", "En espera", hoy, null));
    }

    @Test
    void rechazaVolverApendienteDesdeEnEspera() {
        assertNotNull(service.validarTransicionEstado("En espera", "Pendiente"));
    }

    @Test
    void rechazaPasarAEnEsperaSiNoEsLaFechaActual() {
        LocalDate manana = LocalDate.now(ZoneId.of("America/Lima")).plusDays(1);
        assertNotNull(service.validarTransicionEstado("Pendiente", "En espera", manana, null));
    }

    @Test
    void rechazaRealizadoSinNotaClinica() {
        assertNotNull(service.validarTransicionEstado("En espera", "Realizado", null, ""));
    }

    @Test
    void permitePasarDeEnEsperaARealizadoConNotaClinica() {
        assertNull(service.validarTransicionEstado("En espera", "Realizado", null, "Atencion completada."));
    }

    @Test
    void muestraSoloEstadosPermitidosDesdePendiente() {
        assertEquals(
                java.util.List.of("Pendiente", "En espera"),
                service.estadosPermitidosDesde("Pendiente")
        );
    }

    @Test
    void muestraSoloEstadosPermitidosDesdeEnEspera() {
        assertEquals(
                java.util.List.of("En espera", "Realizado"),
                service.estadosPermitidosDesde("En espera")
        );
    }

    @Test
    void cancelarCitaRealizadaDevuelveMensajeCorrecto() {
        ResultadoOperacion resultado = service.cancelarCitaConResultado(1, "Realizado", "Paciente llama");

        assertFalse(resultado.exitoso());
        assertTrue(resultado.mensaje().contains("Solo se puede cancelar"));
    }

    @Test
    void cancelarCitaSinMotivoDevuelveMensajeCorrecto() {
        ResultadoOperacion resultado = service.cancelarCitaConResultado(1, "Pendiente", " ");

        assertFalse(resultado.exitoso());
        assertEquals("El motivo de cancelacion es obligatorio.", resultado.mensaje());
    }
}
