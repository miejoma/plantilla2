package com.example.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger + JWT: agrega el botón "Authorize" en Swagger UI para pegar el token y que se envíe
 * como "Authorization: Bearer <token>" en cada prueba. (Esta clase NO viene en el paquete de seguridad
 * del taller; se agrega aparte. Si no la usas, igual puedes probar con Postman.)
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI e2_openAPI() {
        return new OpenAPI()
                // Exige el esquema "bearerAuth" en los endpoints
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                // Define el esquema: HTTP Bearer con formato JWT
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
