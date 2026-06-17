package pe.nuevasonrisa.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HistorialAcceso {

    private int id;
    private String usuario;
    private String rol;
    private String estado;
    private LocalDateTime fechaAcceso;

    public HistorialAcceso(int id, String usuario, String rol, String estado, LocalDateTime fechaAcceso) {
        this.id = id;
        this.usuario = usuario;
        this.rol = rol;
        this.estado = estado;
        this.fechaAcceso = fechaAcceso;
    }

    public int getId() { return id; }
    public String getUsuario() { return usuario; }
    public String getRol() { return rol; }
    public String getEstado() { return estado; }
    public LocalDateTime getFechaAcceso() { return fechaAcceso; }
    public String getFechaFormateada() {

        return fechaAcceso.format(
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm"
                )
        );
    }
}