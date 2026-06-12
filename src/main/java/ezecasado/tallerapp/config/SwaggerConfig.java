package ezecasado.tallerapp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
/**
 * Clase: SwaggerConfig.
 * 
 * Esta clase es responsable de proveer las funcionalidades relacionadas con SwaggerConfig
 * dentro del dominio de la aplicación.
 * 
 * @author EzeCasado
 * @version 1.0
 */
public class SwaggerConfig {

    @Bean
    public OpenAPI tallerAppOpenAPI() {
        
        // Configuramos el esquema de seguridad (Basic Auth)
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("basic")
                .description("Inicie sesión con su usuario y contraseña del taller");

        return new OpenAPI()
                .info(new Info()
                        .title("TallerApp API")
                        .description("Documentación oficial de la API REST para el sistema de gestión TallerApp.")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Ezequiel Casado")
                                .email("contacto@tallerapp.com")))
                // Agregamos el requerimiento de seguridad de forma global
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
                .components(new Components().addSecuritySchemes("basicAuth", securityScheme));
    }
}
