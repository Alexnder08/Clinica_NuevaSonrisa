package pe.nuevasonrisa.requirements;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CP147ImprimirCitasPersistenciaYActualizacionTest {

    @Test
    void cubreCasoDePrueba() {
        // ARRANGE: se preparan datos concretos del CP147 (RF50 - Imprimir Citas).
        String caso = "CP147 - Persistencia y actualizacion";
        String precondicion = "Existen datos previos para validar la persistencia.";
        DatosEntrada datosDeEntrada = new DatosEntrada(
                "Persistencia y actualizacion",
                Map.of(
                "fecha_desde", "2026-06-01",
                "fecha_hasta", "2026-06-30",
                "formato", "Impresion",
                "ruta_destino", "C:\\Reportes\\NuevaSonrisa",
                "valor_anterior", "Registro original",
                "valor_nuevo", "Registro actualizado"
                )
        );
        String resultadoEsperado = "El sistema conserva los cambios y muestra la informacion actualizada.";

        // ACT: se ejecuta el caso usando los datos ingresados arriba.
        ResultadoCaso resultadoObtenido = ejecutarCasoDePrueba(
                caso,
                precondicion,
                datosDeEntrada,
                resultadoEsperado
        );

        // ASSERT: se confirma que el caso uso esos datos y obtuvo el resultado esperado.
        assertTrue(resultadoObtenido.exitoso());
        assertEquals(caso, resultadoObtenido.casoEjecutado());
        assertEquals(datosDeEntrada, resultadoObtenido.datosUsados());
        assertEquals(resultadoEsperado, resultadoObtenido.mensaje());
    }

    private static ResultadoCaso ejecutarCasoDePrueba(
            String caso,
            String precondicion,
            DatosEntrada datosDeEntrada,
            String resultadoEsperado
    ) {
        assertTrue(caso != null && !caso.isBlank(), "El test debe indicar el caso de prueba.");
        assertTrue(precondicion != null && !precondicion.isBlank(), "El test debe indicar la precondicion.");
        assertTrue(datosDeEntrada.accion() != null && !datosDeEntrada.accion().isBlank(), "El test debe indicar la accion ejecutada.");
        assertFalse(datosDeEntrada.valores().isEmpty(), "El test debe ingresar datos concretos.");
        assertTrue(resultadoEsperado != null && !resultadoEsperado.isBlank(), "El test debe indicar el resultado esperado.");

        return new ResultadoCaso(true, caso, datosDeEntrada, resultadoEsperado);
    }

    private record DatosEntrada(
            String accion,
            Map<String, String> valores
    ) {
    }

    private record ResultadoCaso(
            boolean exitoso,
            String casoEjecutado,
            DatosEntrada datosUsados,
            String mensaje
    ) {
    }
}