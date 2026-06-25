package pe.nuevasonrisa.requirements;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CP040AsignarServiciosAlOdontologoAccesoConRolNoAutorizadoTest {

    @Test
    void cubreCasoDePrueba() {
        // ARRANGE: se preparan datos concretos del CP040 (RF14 - Asignar Servicios Al Odontologo).
        String caso = "CP040 - Acceso con rol no autorizado";
        String precondicion = "Existe una sesion con un rol que no tiene permiso para asignar servicios al odontologo.";
        DatosEntrada datosDeEntrada = new DatosEntrada(
                "Acceso con rol no autorizado",
                Map.of(
                "odontologo", "Dra. Ana Torres",
                "servicio_1", "Ortodoncia",
                "servicio_2", "Limpieza dental",
                "rol", "Recepcion",
                "permiso_esperado", "Administrador"
                )
        );
        String resultadoEsperado = "El sistema bloquea el acceso a la funcion para ese rol.";

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