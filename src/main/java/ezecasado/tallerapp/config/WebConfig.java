package ezecasado.tallerapp.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/api/**") // Aplica a todos los endpoints que empiecen con /api
                .allowedOrigins("http://localhost:5173", "http://localhost:3000") // Direcciones permitidas del frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP habilitados
                .allowedHeaders("*") // Permite cualquier cabecera (Content-Type, Authorization, etc.)
                .allowCredentials(true); // Permite el manejo de cookies o sesiones si a futuro lo agregas

    }

}
