package pe.nuevasonrisa.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificacionCitaServiceTest {

    @Test
    void generaPlantillaConEstilosYDatosEscapados() {
        String html = NotificacionCitaService.construirHtml(
                "Recordatorio",
                "Tu cita se acerca",
                "#0f766e",
                "#f0fdfa",
                "#99f6e4",
                "Joel <script>",
                "Maria Perez",
                "Limpieza dental",
                LocalDate.of(2026, 6, 22),
                LocalTime.of(9, 30),
                "Te esperamos.",
                "Llega 10 minutos antes."
        );

        assertTrue(html.contains("<!doctype html>"));
        assertTrue(html.contains("background:#0f766e"));
        assertTrue(html.contains("Joel &lt;script&gt;"));
        assertTrue(html.contains("09:30"));
        assertFalse(html.contains("Joel <script>"));
    }
}
