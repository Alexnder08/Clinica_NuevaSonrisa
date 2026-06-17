package pe.nuevasonrisa.model;

public class Usuario {

    private int id;
    private String usuario;
    private String password;
    private String nombre;
    private String apellido;
    private String rol;
    private String estado;
    private String dni;
    private String celular;
    private String servicio;
    private String email;

    public Usuario() {
    }

    public Usuario(int id, String usuario, String password, String nombre, String apellido,
                   String rol, String estado, String dni, String celular, String servicio) {
        this(id, usuario, password, nombre, apellido, rol, estado, dni, celular, servicio, null);
    }

    public Usuario(int id, String usuario, String password, String nombre, String apellido,
                   String rol, String estado, String dni, String celular, String servicio, String email) {
        this.id = id;
        this.usuario = usuario;
        this.password = password;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
        this.estado = estado;
        this.dni = dni;
        this.celular = celular;
        this.servicio = servicio;
        this.email = email;
    }

    public int getId() { return id; }
    public String getUsuario() { return usuario; }
    public String getPassword() { return password; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getRol() { return rol; }
    public String getEstado() { return estado; }
    public String getDni() { return dni; }
    public String getCelular() { return celular; }
    public String getServicio() { return servicio; }
    public String getEmail() { return email; }

    public void setUsuario(String usuario) { this.usuario = usuario; }
    public void setPassword(String password) { this.password = password; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setRol(String rol) { this.rol = rol; }
    public void setDni(String dni) { this.dni = dni; }
    public void setCelular(String celular) { this.celular = celular; }
    public void setEmail(String email) { this.email = email; }
    public void setId(int id){ this.id = id; }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
