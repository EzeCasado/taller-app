package ezecasado.tallerapp.repository;

import ezecasado.tallerapp.models.Modificacion;
import ezecasado.tallerapp.models.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interfaz: ModificacionRepository.
 * 
 * Esta clase es responsable de proveer las funcionalidades relacionadas con ModificacionRepository
 * dentro del dominio de la aplicación.
 * 
 * @author EzeCasado
 * @version 1.0
 */
public interface ModificacionRepository extends JpaRepository<Modificacion,Long> {

    List<Modificacion> findByVehiculoId(Long vehiculoId);

    @Query("SELECT SUM(mo.costo) FROM Modificacion mo where mo.vehiculo.id = :idDelVehiculo")
    BigDecimal sumarCostoTotalModificacion(@Param("idDelVehiculo")  Long vehiculoId);

    List<Modificacion> findByActiva(boolean activa);
}
