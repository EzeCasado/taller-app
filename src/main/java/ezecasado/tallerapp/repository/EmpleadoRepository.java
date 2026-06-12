package ezecasado.tallerapp.repository;

import ezecasado.tallerapp.models.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz: EmpleadoRepository.
 * 
 * Esta clase es responsable de proveer las funcionalidades relacionadas con EmpleadoRepository
 * dentro del dominio de la aplicación.
 * 
 * @author EzeCasado
 * @version 1.0
 */
public interface EmpleadoRepository extends JpaRepository<Empleado,Long> {

    Optional<Empleado> findByUsuario(String usuario);

    List<Empleado> getEmpleadosByUsuario(String usuario);

    List<Empleado> findByActivoTrue(boolean activo);

    List<Empleado> findByActivo(boolean activo);
}
