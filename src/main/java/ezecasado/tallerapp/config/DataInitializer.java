package ezecasado.tallerapp.config;

import ezecasado.tallerapp.models.Empleado;
import ezecasado.tallerapp.repository.EmpleadoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Inicializador de datos para el primer arranque.
 * Si no existe ningún empleado en la base de datos,
 * crea un usuario administrador por defecto para poder
 * ingresar al sistema por primera vez.
 *
 * Credenciales por defecto:
 *   Usuario:admin
 *   Contraseña: admin123
 *
 * ⚠️ Se recomienda cambiarla desde el panel de Empleados
 *    luego del primer inicio de sesión.
 */
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(EmpleadoRepository empleadoRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            if (empleadoRepository.count() == 0) {
                Empleado admin = new Empleado(
                        "Administrador",
                        "admin",
                        passwordEncoder.encode("admin123")
                );
                admin.setActivo(true);
                empleadoRepository.save(admin);

                System.out.println("==============================================");
                System.out.println("  TallerApp — Usuario administrador creado");
                System.out.println("  Usuario:    admin");
                System.out.println("  Contraseña: admin123");
                System.out.println("  ⚠️  Cambiá la contraseña luego del primer login");
                System.out.println("==============================================");
            }
        };
    }
}
