package pe.nuevasonrisa.model;

public class Paciente {

    private int id;
    private String dni;
    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;

    public Paciente() {
    }

    public Paciente(int id, String dni, String nombre,
                    String apellido, String telefono,
                    String correo) {

        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
    }

    public int getId() { return id; }

    public String getDni() { return dni; }

    public String getNombre() { return nombre; }

    public String getApellido() { return apellido; }

    public String getTelefono() { return telefono; }

    public String getCorreo() { return correo; }

    public void setId(int id) { this.id = id; }

    public void setDni(String dni) { this.dni = dni; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public void setApellido(String apellido) { this.apellido = apellido; }

    public void setTelefono(String telefono) { this.telefono = telefono; }

    public void setCorreo(String correo) { this.correo = correo; }
}