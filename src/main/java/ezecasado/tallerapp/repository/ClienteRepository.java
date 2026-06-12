package ezecasado.tallerapp.repository;

import ezecasado.tallerapp.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz: ClienteRepository.
 * 
 * Esta clase es responsable de proveer las funcionalidades relacionadas con ClienteRepository
 * dentro del dominio de la aplicación.
 * 
 * @author EzeCasado
 * @version 1.0
 */
public interface ClienteRepository extends JpaRepository<Cliente,Long> {

    Optional<Cliente> findByEmail(String email);

    List<Cliente> findByActivoTrue();


}
