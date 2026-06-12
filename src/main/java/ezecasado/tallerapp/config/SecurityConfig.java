package ezecasado.tallerapp.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
/**
 * Clase: SecurityConfig.
 * 
 * Esta clase es responsable de proveer las funcionalidades relacionadas con SecurityConfig
 * dentro del dominio de la aplicación.
 * 
 * @author EzeCasado
 * @version 1.0
 */
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Configuramos CORS de forma explícita para Spring Security
                .cors(Customizer.withDefaults())
                // Desactivamos CSRF porque para APIs REST con tokens o Basic Auth no se necesita
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Permitimos que el frontend consulte el estado (CORS Pre-flight) sin trabas
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Permitimos acceso público a Swagger y a la documentación de OpenAPI
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // El resto de la API requiere usuario y contraseña
                        .anyRequest().authenticated()
                )
                // Usamos HTTP Basic pero modificamos el EntryPoint para evitar que el navegador muestre
                // el molesto cartelito nativo pidiendo usuario y contraseña ante un 401.
                .httpBasic(basic -> basic.authenticationEntryPoint((request, response, authException) -> {
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
                }));

        return http.build();

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite cualquier puerto en localhost (5173, 5174, etc.)
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*", "http://127.0.0.1:*"));
        // Métodos HTTP habilitados
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Permite cualquier cabecera (Content-Type, Authorization, etc.)
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // Permite el manejo de credenciales (Basic Auth / Cookies)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Este va a encargarse de comparar las contraseñas con el hash de la DB
        return new BCryptPasswordEncoder();
    }

}
