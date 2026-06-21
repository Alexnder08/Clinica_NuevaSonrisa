package pe.nuevasonrisa.service;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;

class BackupServiceTest {

    @Test
    void rechazaRestauracionSinArchivoValido() {
        BackupService.Resultado resultado = new BackupService().restaurarBackup(new File("archivo-inexistente.backup"));
        assertFalse(resultado.exitoso());
    }
}
