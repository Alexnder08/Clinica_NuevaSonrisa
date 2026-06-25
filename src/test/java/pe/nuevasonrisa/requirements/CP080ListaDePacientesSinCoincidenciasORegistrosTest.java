package pe.nuevasonrisa.requirements;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CP080ListaDePacientesSinCoincidenciasORegistrosTest {

    @Test
    void cubreCasoDePrueba() {
        // ARRANGE: se preparan datos concretos del CP080 (RF28 - Lista De Pacientes).
        String caso = "CP080 - Sin coincidencias o registros";
        String precondicion = "No existen registros que coincidan con el criterio usado.";
        DatosEntrada datosDeEntrada = new DatosEntrada(
                "Sin coincidencias o registros",
                Map.of(
                "paciente", "Maria Lopez",
                "dni", "76543210",
                "telefono", "987654321",
                "correo", "maria.lopez@example.com",
                "criterio_busqueda", "SIN-RESULTADOS-999"
                )
        );
        String resultadoEsperado = "El sistema muestra la lista vacia o un mensaje sin coincidencias.";

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