package ezecasado.tallerapp.repository;

import ezecasado.tallerapp.models.Mantenimiento;
import ezecasado.tallerapp.models.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interfaz: MantenimientoRepository.
 * 
 * Esta clase es responsable de proveer las funcionalidades relacionadas con MantenimientoRepository
 * dentro del dominio de la aplicación.
 * 
 * @author EzeCasado
 * @version 1.0
 */
public interface MantenimientoRepository extends JpaRepository<Mantenimiento,Long> {

    List<Mantenimiento> findByVehiculoId(Long vehiculoId);

    @Query("SELECT SUM(ma.costo) FROM Mantenimiento ma where ma.vehiculo.id = :idDelVehiculo")
    BigDecimal sumarCostoTotalMantenimiento(@Param("idDelVehiculo") Long idDelVehiculo);



}


