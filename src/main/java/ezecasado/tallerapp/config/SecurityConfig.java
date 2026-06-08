package ezecasado.tallerapp.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Desactivamos CSRF porque para APIs REST con tokens o Basic Auth no se necesita
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Permitimos que el frontend consulte el estado (CORS Pre-flight) sin trabas
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // El resto de la API requiere usuario y contraseña
                        .anyRequest().authenticated()
                )
                // Activamos la ventana/alerta clásica de autenticación básica
                .httpBasic(Customizer.withDefaults());

        return http.build();


    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Este va a encargarse de comparar las contraseñas con el hash de la DB
        return new BCryptPasswordEncoder();
    }

}
