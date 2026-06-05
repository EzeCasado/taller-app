package ezecasado.tallerapp.repository;

import ezecasado.tallerapp.models.Modificacion;
import ezecasado.tallerapp.models.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModificacionRepository extends JpaRepository<Modificacion,Long> {

    List<Modificacion> findByVehiculoId(Long vehiculoId);

}
