package ezecasado.tallerapp.models;


import jakarta.persistence.*;

@Entity
@Table(name = "empleado")
/**
 * Clase: Empleado.
 * 
 * Esta clase es responsable de proveer las funcionalidades relacionadas con Empleado
 * dentro del dominio de la aplicación.
 * 
 * @author EzeCasado
 * @version 1.0
 */
public class Empleado {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String nombre;

    @Column(unique = true, nullable = false)
    private String usuario;

    @Column(name = "password_hash", nullable = false)
    private String contrasenia;

    private Boolean activo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    public Empleado(String nombre, String usuario, String contrasenia, Rol rol) {
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasenia = contrasenia;
        this.activo = true;
        this.rol = rol;
    }

    // Constructor sobrecargado para compatibilidad con tests
    public Empleado(String nombre, String usuario, String contrasenia) {
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasenia = contrasenia;
        this.activo = true;
        this.rol = Rol.ADMIN; // Por defecto para los tests
    }


    public Empleado(){}

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

}
