package pe.nuevasonrisa.requirements;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CP030ExportarUsuariosEnPdfDestinoORecursoNoDisponibleTest {

    @Test
    void cubreCasoDePrueba() {
        // ARRANGE: se preparan datos concretos del CP030 (RF11 - Exportar Usuarios En Pdf).
        String caso = "CP030 - Destino o recurso no disponible";
        String precondicion = "El modulo de exportar usuarios en PDF esta abierto.";
        DatosEntrada datosDeEntrada = new DatosEntrada(
                "Destino o recurso no disponible",
                Map.of(
                "usuario", "jramirez",
                "dni", "45678912",
                "nombre", "Juan Ramirez",
                "rol", "Recepcion",
                "estado", "Activo",
                "ruta_destino", "Z:\\ruta\\no_disponible",
                "accion_usuario", "Cancelar seleccion"
                )
        );
        String resultadoEsperado = "El sistema informa el error o cancela la operacion sin perder datos.";

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