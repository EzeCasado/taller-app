package ezecasado.tallerapp.service;

import ezecasado.tallerapp.models.Empleado;
import ezecasado.tallerapp.repository.EmpleadoRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
/**
 * Clase: CustomUserDetailsService.
 * 
 * Esta clase es responsable de proveer las funcionalidades relacionadas con CustomUserDetailsService
 * dentro del dominio de la aplicación.
 * 
 * @author EzeCasado
 * @version 1.0
 */
public class CustomUserDetailsService implements UserDetailsService {

    private final EmpleadoRepository empleadoRepository;

    public CustomUserDetailsService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Empleado empleado = empleadoRepository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        if (!empleado.getActivo()) {
            throw new UsernameNotFoundException("El usuario " + username + " está inactivo");
        }

        return new User(
                empleado.getUsuario(),
                empleado.getContrasenia(),
                org.springframework.security.core.authority.AuthorityUtils.createAuthorityList("ROLE_" + empleado.getRol().name())
        );
    }
}
