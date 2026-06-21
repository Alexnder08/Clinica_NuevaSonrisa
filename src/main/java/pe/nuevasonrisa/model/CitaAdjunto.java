package pe.nuevasonrisa.model;

import java.time.LocalDateTime;

public class CitaAdjunto {
    private final long id;
    private final int citaId;
    private final String nombreArchivo;
    private final String tipoContenido;
    private final long tamanoBytes;
    private final LocalDateTime creadoEn;

    public CitaAdjunto(long id, int citaId, String nombreArchivo, String tipoContenido, long tamanoBytes, LocalDateTime creadoEn) {
        this.id = id;
        this.citaId = citaId;
        this.nombreArchivo = nombreArchivo;
        this.tipoContenido = tipoContenido;
        this.tamanoBytes = tamanoBytes;
        this.creadoEn = creadoEn;
    }

    public long getId() { return id; }
    public int getCitaId() { return citaId; }
    public String getNombreArchivo() { return nombreArchivo; }
    public String getTipoContenido() { return tipoContenido; }
    public long getTamanoBytes() { return tamanoBytes; }
    public LocalDateTime getCreadoEn() { return creadoEn; }

    @Override
    public String toString() {
        return nombreArchivo + " (" + Math.max(1, tamanoBytes / 1024) + " KB)";
    }
}
