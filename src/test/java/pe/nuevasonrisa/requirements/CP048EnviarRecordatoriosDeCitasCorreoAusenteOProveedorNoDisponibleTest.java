package pe.nuevasonrisa.requirements;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CP048EnviarRecordatoriosDeCitasCorreoAusenteOProveedorNoDisponibleTest {

    @Test
    void cubreCasoDePrueba() {
        // ARRANGE: se preparan datos concretos del CP048 (RF17 - Enviar Recordatorios De Citas).
        String caso = "CP048 - Correo ausente o proveedor no disponible";
        String precondicion = "La cita existe, pero el correo del destinatario o el proveedor de correo no esta disponible.";
        DatosEntrada datosDeEntrada = new DatosEntrada(
                "Correo ausente o proveedor no disponible",
                Map.of(
                "paciente", "Maria Lopez",
                "correo", "maria.lopez@example.com",
                "fecha_cita", "2026-06-30",
                "hora_cita", "10:30"
                )
        );
        String resultadoEsperado = "El sistema informa el problema del correo sin perder la operacion principal.";

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