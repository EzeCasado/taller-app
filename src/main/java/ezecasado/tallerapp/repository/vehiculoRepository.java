package ezecasado.tallerapp.repository;

import ezecasado.tallerapp.models.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface vehiculoRepository extends JpaRepository<Vehiculo,Long> {

    Optional<Vehiculo> findByPatente(String patente);

    List<Vehiculo> findByActivo(boolean activo);
}
