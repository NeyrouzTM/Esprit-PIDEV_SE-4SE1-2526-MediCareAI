package tn.esprit.tn.medicare_ai.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {

    @Bean
    public OpenAPI medicareOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Medicare AI API")
                        .description("OpenAPI documentation for medicine search, prescriptions, orders, refills, inventory, and drug interaction endpoints.")
                        .version("v1")
                        .contact(new Contact()
                                .name("Medicare AI Team")
                                .email("support@medicare-ai.local")
                                .url("https://medicare-ai.local")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    @Bean
    public GroupedOpenApi pharmacyApi() {
        return GroupedOpenApi.builder()
                .group("e-pharmacy")
                .pathsToMatch("/api/pharmacy/**")
                .build();
    }
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("auth")                  // Nom du groupe dans Swagger UI
                .pathsToMatch("/auth/**")       // Tous les endpoints commençant par /auth
                .build();
    }
    // Groupe pour les endpoints du Forum
    @Bean
    public GroupedOpenApi forumApi() {
        return GroupedOpenApi.builder()
                .group("Forum")
                .pathsToMatch("/api/forum/**")
                .build();
    }
    @Bean
    public GroupedOpenApi meetingApi() {          // ← Groupe pour les Meetings
        return GroupedOpenApi.builder()
                .group("Meetings (Réunions)")
                .pathsToMatch("/api/meetings/**")   // ← Tous les endpoints des réunions
                .build();
    }
    // Groupe pour les endpoints des Abonnements
    @Bean
    public GroupedOpenApi subscriptionApi() {
        return GroupedOpenApi.builder()
                .group("Subscriptions")
                .pathsToMatch("/api/subscriptions/**", "/api/subscription-plans/**")
                .build();
    }
    // Groupe pour les endpoints de Collaboration
    @Bean
    public GroupedOpenApi collaborationApi() {
        return GroupedOpenApi.builder()
                .group("Collaboration")
                .pathsToMatch("/api/collaboration/**")
                .build();
    }

    // Groupe pour tous les endpoints (optionnel)
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("All Endpoints")
                .pathsToMatch("/api/**")
                .build();
    }
}
