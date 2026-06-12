package ezecasado.tallerapp.models;

import jakarta.persistence.*;

@Entity
@Table(name = "cliente")
/**
 * Clase: Cliente.
 * 
 * Esta clase es responsable de proveer las funcionalidades relacionadas con Cliente
 * dentro del dominio de la aplicación.
 * 
 * @author EzeCasado
 * @version 1.0
 */
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    private String telefono;
    private String email;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String observaciones;
    private Boolean activo;

    public Cliente(){}

    public Cliente(Boolean activo, String apellido, String email,String nombre, String observaciones, String telefono) {
        this.activo = true;
        this.apellido = apellido;
        this.email = email;
        this.nombre = nombre;
        this.observaciones = observaciones;
        this.telefono = telefono;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setId(Long id) {

        this.id = id;
    }
}
