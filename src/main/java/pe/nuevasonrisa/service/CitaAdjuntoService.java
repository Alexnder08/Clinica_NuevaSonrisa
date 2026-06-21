package pe.nuevasonrisa.service;

import pe.nuevasonrisa.dao.CitaAdjuntoDAO;
import pe.nuevasonrisa.model.CitaAdjunto;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

public class CitaAdjuntoService {
    private static final long MAXIMO_BYTES = 10L * 1024 * 1024;
    private static final Set<String> EXTENSIONES = Set.of("pdf", "png", "jpg", "jpeg", "doc", "docx");
    private final CitaAdjuntoDAO dao;

    public CitaAdjuntoService(CitaAdjuntoDAO dao) {
        this.dao = dao;
    }

    public String adjuntar(int citaId, int doctorId, File archivo) {
        if (archivo == null || !archivo.isFile()) return "Seleccione un archivo valido.";
        if (archivo.length() <= 0 || archivo.length() > MAXIMO_BYTES) return "El archivo debe pesar entre 1 byte y 10 MB.";
        String extension = extension(archivo.getName());
        if (!EXTENSIONES.contains(extension)) return "Formato no permitido. Use PDF, imagen o documento Word.";
        try {
            byte[] contenido = Files.readAllBytes(archivo.toPath());
            String tipo = Files.probeContentType(archivo.toPath());
            return dao.guardar(citaId, doctorId, archivo.getName(), tipo, contenido)
                    ? null : "No se pudo adjuntar el archivo. Verifique que la migracion 002 este aplicada.";
        } catch (Exception e) {
            return "No se pudo leer el archivo seleccionado.";
        }
    }

    public List<CitaAdjunto> listar(int citaId, int doctorId) {
        return dao.listarPorCita(citaId, doctorId);
    }

    public boolean descargar(CitaAdjunto adjunto, int doctorId, File destino) {
        byte[] contenido = dao.obtenerContenido(adjunto.getId(), doctorId);
        if (contenido == null) return false;
        try {
            Files.write(destino.toPath(), contenido);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String extension(String nombre) {
        int punto = nombre.lastIndexOf('.');
        return punto < 0 ? "" : nombre.substring(punto + 1).toLowerCase();
    }
}
