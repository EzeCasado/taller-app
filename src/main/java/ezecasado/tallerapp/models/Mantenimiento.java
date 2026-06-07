package ezecasado.tallerapp.models;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "mantenimiento")
public class Mantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;

    @Column(columnDefinition = "LONGTEXT")
    private String descripcion;

    private BigDecimal costo;

    private int kilometraje;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String comentario;

    private boolean activo;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id")
    private Vehiculo vehiculo;

    @ManyToOne
    @JoinColumn(name = "empleado_id")
    private Empleado empleado;

    public Mantenimiento() {}

    public Mantenimiento(boolean activo, String comentario, BigDecimal costo, String descripcion, Empleado empleado, LocalDate fecha, int kilometraje, Vehiculo vehiculo) {
        this.activo = true;
        this.comentario = comentario;
        this.costo = costo;
        this.descripcion = descripcion;
        this.empleado = empleado;
        this.fecha = fecha;
        this.kilometraje = kilometraje;
        this.vehiculo = vehiculo;
    }

    public Long getId() {
        return id;
    }

    public boolean isActivo() {
        return activo;
    }

    public String getComentario() {
        return comentario;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public int getKilometraje() {
        return kilometraje;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }


    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setKilometraje(int kilometraje) {
        this.kilometraje = kilometraje;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public  void setId(Long id) {
        this.id = id;
    }

}
