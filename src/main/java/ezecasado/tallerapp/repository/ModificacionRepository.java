package ezecasado.tallerapp.repository;

import ezecasado.tallerapp.models.Modificacion;
import ezecasado.tallerapp.models.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ModificacionRepository extends JpaRepository<Modificacion,Long> {

    List<Modificacion> findByVehiculoId(Long vehiculoId);

    @Query("SELECT SUM(mo.costo) FROM Modificacion mo where mo.vehiculo.id = :idDelVehiculo")
    BigDecimal sumarCostoTotalModificacion(@Param("idDelVehiculo")  Long vehiculoId);

}
