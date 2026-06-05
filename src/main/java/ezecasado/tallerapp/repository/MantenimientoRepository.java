package ezecasado.tallerapp.repository;

import ezecasado.tallerapp.models.Mantenimiento;
import ezecasado.tallerapp.models.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MantenimientoRepository extends JpaRepository<Mantenimiento,Long> {

    List<Mantenimiento> findByVehiculoId(Long vehiculoId);

}


