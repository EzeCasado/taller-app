package ezecasado.tallerapp.repository;

import ezecasado.tallerapp.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente,Long> {

    Optional<Cliente> findByEmail(String email);

    List<Cliente> findByActivoTrue();


}
