package pe.nuevasonrisa.model;

public class UsuarioTabla {

    private int id;
    private String usuario;
    private String nombre;
    private String apellido;
    private String rol;
    private String estado;
    private String dni;
    private String celular;
    private String email;

    public UsuarioTabla(int id, String usuario, String nombre, String apellido, String rol, String estado, String dni, String celular) {
        this(id, usuario, nombre, apellido, rol, estado, dni, celular, null);
    }

    public UsuarioTabla(int id, String usuario, String nombre, String apellido, String rol, String estado, String dni, String celular, String email) {
        this.id = id;
        this.usuario = usuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
        this.estado = estado;
        this.dni = dni;
        this.celular = celular;
        this.email = email;
    }

    public int getId() { return id; }
    public String getUsuario() { return usuario; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getRol() { return rol; }
    public String getEstado() { return estado; }
    public String getDni(){ return dni; }
    public String getCelular(){ return celular; }
    public String getEmail(){ return email; }
}
