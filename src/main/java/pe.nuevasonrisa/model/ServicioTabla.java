package pe.nuevasonrisa.model;

import java.math.BigDecimal;

public class ServicioTabla {

    private int id;
    private String nombre;
    private int duracion;
    private BigDecimal costo;

    public ServicioTabla(int id,
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
}