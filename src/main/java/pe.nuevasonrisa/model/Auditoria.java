package pe.nuevasonrisa.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Auditoria {

    private int id;
    private String usuario;
    private String accion;
    private String modulo;
    private String detalle;
    private LocalDateTime fecha;

    public Auditoria(int id, String usuario, String accion, String modulo, String detalle, LocalDateTime fecha) {
        this.id = id;
        this.usuario = usuario;
        this.accion = accion;
        this.modulo = modulo;
        this.detalle = detalle;
        this.fecha = fecha;
    }

    public int getId() { return id; }
    public String getUsuario() { return usuario; }
    public String getAccion() { return accion; }
    public String getModulo() { return modulo; }
    public String getDetalle() { return detalle; }
    public LocalDateTime getFecha() { return fecha; }

    public String getFechaFormateada() {
        return fecha.format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        );
    }

}