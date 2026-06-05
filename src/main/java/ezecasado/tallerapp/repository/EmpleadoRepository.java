package ezecasado.tallerapp.repository;

import ezecasado.tallerapp.models.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado,Long> {

    Optional<Empleado> findByUsuario(String usuario);

    List<Empleado> getEmpleadosByUsuario(String usuario);

    List<Empleado> findByActivoTrue(boolean activo);

    List<Empleado> findByActivo(boolean activo);
}
