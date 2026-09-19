package capaEntidad;

public class Usuario {
    private int id;
    private String dni;
    private String nombres;
    private String apellidos;
    private String nombre;
    private String usuario;
    private String password;
    private String rol;
    private boolean activo;

    public Usuario() {}

    public Usuario(int id, String dni, String nombres, String apellidos, String nombre,
                   String usuario, String password, String rol, boolean activo) {
        this.id = id;
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.nombre = nombre;
        this.usuario = usuario;
        this.password = password;
        this.rol = rol;
        this.activo = activo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
