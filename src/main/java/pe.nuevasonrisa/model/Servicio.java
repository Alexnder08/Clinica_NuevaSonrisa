package pe.nuevasonrisa.model;

import java.math.BigDecimal;

public class Servicio {

    private int id;
    private String nombre;
    private int duracion;
    private BigDecimal costo;

    public Servicio() {
    }

    public Servicio(int id,
                    String nombre,
                    int duracion,
                    BigDecimal costo) {

        this.id = id;
        this.nombre = nombre;
        this.duracion = duracion;
        this.costo = costo;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getDuracion() { return duracion; }
    public BigDecimal getCosto() { return costo; }

    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDuracion(int duracion) { this.duracion = duracion; }
    public void setCosto(BigDecimal costo) { this.costo = costo; }
}