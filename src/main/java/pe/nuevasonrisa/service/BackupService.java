package pe.nuevasonrisa.service;

import pe.nuevasonrisa.config.DatabaseConnection;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import pe.nuevasonrisa.util.AppLogger;

public class BackupService {

    private static final Logger LOGGER = AppLogger.getLogger(BackupService.class);

    public Resultado crearBackup(File destino) {
        return ejecutar("pg_dump.exe", List.of("--format=custom", "--no-owner", "--file=" + destino.getAbsolutePath()));
    }

    public Resultado restaurarBackup(File origen) {
        if (origen == null || !origen.isFile()) {
            return new Resultado(false, "Seleccione una copia de seguridad valida.");
        }
        return ejecutar("pg_restore.exe", List.of(
                "--clean", "--if-exists", "--no-owner", "--exit-on-error", origen.getAbsolutePath()));
    }

    private Resultado ejecutar(String herramienta, List<String> argumentosEspecificos) {
        try {
            Configuracion config = configuracion();
            Path ejecutable = resolverEjecutable(herramienta);
            if (ejecutable == null) {
                LOGGER.warn("No se encontró el ejecutable de respaldo de PostgreSQL.");
                return new Resultado(false, "No se encontro " + herramienta + ". Configure PG_BIN_DIR.");
            }
            List<String> comando = new ArrayList<>();
            comando.add(ejecutable.toString());
            comando.add("--host=" + config.host());
            comando.add("--port=" + config.port());
            comando.add("--username=" + DatabaseConnection.getUsername());
            comando.add("--dbname=" + config.database());
            comando.addAll(argumentosEspecificos);

            ProcessBuilder builder = new ProcessBuilder(comando).redirectErrorStream(true);
            builder.environment().put("PGPASSWORD", DatabaseConnection.getPassword());
            Process proceso = builder.start();
            if (!proceso.waitFor(180, TimeUnit.SECONDS)) {
                proceso.destroyForcibly();
                LOGGER.error("La operación de respaldo excedió el tiempo máximo de 3 minutos.");
                return new Resultado(false, "La operacion excedio el tiempo maximo de 3 minutos.");
            }
            String salida = new String(proceso.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            if (proceso.exitValue() == 0) {
                LOGGER.info("La operación de respaldo PostgreSQL finalizó correctamente.");
                return new Resultado(true, "Operacion completada correctamente.");
            }
            LOGGER.error("La operación de respaldo PostgreSQL falló con código {}.", proceso.exitValue());
            return new Resultado(false, salida.isBlank() ? "PostgreSQL devolvio un error." : salida);
        } catch (Exception e) {
            LOGGER.error("No se pudo ejecutar la operación de respaldo PostgreSQL.");
            return new Resultado(false, "No se pudo ejecutar la operacion: " + e.getMessage());
        }
    }

    private Configuracion configuracion() {
        URI uri = URI.create(DatabaseConnection.getJdbcUrl().replaceFirst("^jdbc:", ""));
        return new Configuracion(uri.getHost(), uri.getPort() > 0 ? uri.getPort() : 5432,
                uri.getPath().replaceFirst("^/", ""));
    }

    private Path resolverEjecutable(String nombre) {
        String pgBin = System.getenv("PG_BIN_DIR");
        if (pgBin != null && !pgBin.isBlank()) {
            Path ruta = Path.of(pgBin, nombre);
            if (Files.isRegularFile(ruta)) return ruta;
        }
        Path rutaComun = Path.of("C:\\Program Files\\PostgreSQL\\18\\bin", nombre);
        return Files.isRegularFile(rutaComun) ? rutaComun : null;
    }

    private record Configuracion(String host, int port, String database) {}
    public record Resultado(boolean exitoso, String mensaje) {}
}
