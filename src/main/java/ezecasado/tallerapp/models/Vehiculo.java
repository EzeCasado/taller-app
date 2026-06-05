package ezecasado.tallerapp.models;

import jakarta.persistence.*;

@Entity
@Table(name = "vehiculo")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String patente;

    private String marca;
    private String modelo;
    private int anio;
    private int kilometraje;
    private String motor;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String comentarios;

    private boolean activo;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    public Vehiculo(boolean activo, int anio, Cliente cliente, String comentarios,int kilometraje, String marca, String modelo, String motor, String patente) {
        this.activo = true;
        this.anio = anio;
        this.cliente = cliente;
        this.comentarios = comentarios;
        this.kilometraje = kilometraje;
        this.marca = marca;
        this.modelo = modelo;
        this.motor = motor;
        this.patente = patente;
    }

    public Vehiculo() {}

    public Long getId() {
        return id;
    }

    public boolean isActivo() {
        return activo;
    }

    public int getAnio() {
        return anio;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public String getComentarios() {
        return comentarios;
    }

    public int getKilometraje() {
        return kilometraje;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public String getMotor() {
        return motor;
    }

    public String getPatente() {
        return patente;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public void setKilometraje(int kilometraje) {
        this.kilometraje = kilometraje;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public void setMotor(String motor) {
        this.motor = motor;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }
}
