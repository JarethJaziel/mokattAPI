package com.mokatta.mokatta_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI mokattaOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, jwtSecurityScheme()));
    }

    private Info apiInfo() {
        return new Info()
                .title("Mokatta API")
                .description("""
                        API REST para el sistema de gestión de cafetería.

                        **Módulos disponibles:**
                        - 🔐 Auth — Login y gestión de tokens JWT
                        - 🍽️ Products — Catálogo de productos y categorías
                        - 📦 Inventory — Control de stock e ingresos
                        - 🧾 Orders — POS: pedidos y cobro
                        - 📊 Reports — Ventas, corte de caja, estadísticas

                        Para autenticarte: usa `/api/auth/login`, copia el `accessToken`
                        y pégalo en el botón **Authorize** arriba.
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("Mokatta")
                        .email("[EMAIL_ADDRESS]"));
    }

    private SecurityScheme jwtSecurityScheme() {
        return new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Ingresa el token JWT obtenido en /api/auth/login");
    }
}
