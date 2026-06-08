package ezecasado.tallerapp.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "modificacion")
public class Modificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private LocalDate fecha;

    private BigDecimal costo;

    private boolean activa;

    private boolean sigueInstalada;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id")
    private Vehiculo vehiculo;

    @ManyToOne
    @JoinColumn(name = "empleado_id")
    private Empleado empleado;


    public Modificacion(boolean activa, BigDecimal costo, Empleado empleado, LocalDate fecha, String nombre, boolean sigueInstalada, Vehiculo vehiculo) {
        this.activa = true;
        this.costo = costo;
        this.empleado = empleado;
        this.fecha = fecha;
        this.nombre = nombre;
        this.sigueInstalada = sigueInstalada;
        this.vehiculo = vehiculo;
    }

    public Modificacion() {}


    public boolean isActiva() {
        return activa;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isSigueInstalada() {
        return sigueInstalada;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setSigueInstalada(boolean sigueInstalada) {
        this.sigueInstalada = sigueInstalada;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
