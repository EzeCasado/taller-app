package ezecasado.tallerapp.models;


import jakarta.persistence.*;

@Entity
@Table(name = "empleado")
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

    public Empleado(String nombre, String usuario, String contrasenia) {

        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasenia = contrasenia;
        this.activo = true;


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

}
