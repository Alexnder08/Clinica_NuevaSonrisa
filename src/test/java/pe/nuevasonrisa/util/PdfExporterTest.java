package pe.nuevasonrisa.util;

import org.junit.jupiter.api.Test;
import pe.nuevasonrisa.model.CitaTabla;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfExporterTest {

    @Test
    void generaListadoDeCitasLegible() throws Exception {
        File salida = new File("target/pdf-verification/citas-prueba.pdf");
        assertTrue(salida.getParentFile().mkdirs() || salida.getParentFile().isDirectory());

        List<CitaTabla> citas = List.of(
                new CitaTabla(1, 1, 2, 3, "Ana Torres", "Carlos Ruiz", "Limpieza dental",
                        LocalDate.of(2026, 6, 22), LocalTime.of(9, 0), "En espera", "Control", "Sin alergias"),
                new CitaTabla(2, 4, 5, 6, "Luis Mendoza", "Maria Perez", "Ortodoncia",
                        LocalDate.of(2026, 6, 22), LocalTime.of(11, 30), "Pendiente", "Evaluacion", "Primera visita")
        );

        PdfExporter.generarCitas(citas, salida);

        assertTrue(salida.isFile());
        assertTrue(salida.length() > 1_000);
    }
}
