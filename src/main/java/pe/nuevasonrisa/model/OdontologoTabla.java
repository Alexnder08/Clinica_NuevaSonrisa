package pe.nuevasonrisa.model;

public class OdontologoTabla {

    private int id;
    private String usuario;
    private String nombre;
    private String apellido;
    private String dni;
    private String celular;
    private String servicio;
    private String estado;

    public OdontologoTabla(
            int id,
            String usuario,
            String nombre,
            String apellido,
            String dni,
            String celular,
            String servicio,
            String estado
    ) {
        this.id = id;
        this.usuario = usuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.celular = celular;
        this.servicio = servicio;
        this.estado = estado;
    }

    public int getId() { return id; }
    public String getUsuario() { return usuario; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getDni() { return dni; }
    public String getCelular() { return celular; }
    public String getServicio() { return servicio; }
    public String getEstado() { return estado; }
}