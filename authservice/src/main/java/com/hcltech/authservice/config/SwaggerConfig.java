package com.hcltech.authservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Swagger/OpenAPI documentation setup.
 * This class configures the OpenAPI specification for the Auth Service API
 * including API information, server configurations, and security scheme definitions.
 *
 * <p>The configuration provides:
 * <ul>
 *   <li>API metadata (title, version, description)</li>
 *   <li>Multiple server configurations for different environments</li>
 *   <li>JWT Bearer token security scheme</li>
 *   <li>Global security requirement for all endpoints</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>
 * {@code
 * // Access the API documentation at:
 * // http://localhost:8080/swagger-ui/index.html (via API Gateway)
 * // http://localhost:8081/swagger-ui/index.html (direct to Auth Service)
 * }
 * </pre>
 *
 * @author Boobesh Kumar S
 * @version 1.0
 * @since 2025-11-17
 * @see OpenAPI
 * @see Configuration
 */
@Configuration
public class SwaggerConfig {

    /**
     * Creates and configures a custom OpenAPI specification for the Auth Service.
     *
     * <p>This method configures:
     * <ul>
     *   <li>API information including title, version, and description</li>
     *   <li>Multiple server configurations for API Gateway and direct service access</li>
     *   <li>JWT Bearer token security scheme for authentication</li>
     *   <li>Global security requirement applying to all API endpoints</li>
     * </ul>
     *
     * <p>The security scheme is configured as HTTP Bearer authentication with JWT format,
     * requiring clients to include a valid JWT token in the Authorization header for all requests.
     *
     * @return Fully configured {@link OpenAPI} instance with:
     *         <ul>
     *           <li>API info: "Auth Service API" version 1.0</li>
     *           <li>Servers: API Gateway (8080) and Auth Service direct (8081)</li>
     *           <li>Security: JWT Bearer token requirement for all endpoints</li>
     *         </ul>
     *
     * @see OpenAPI
     * @see Info
     * @see Server
     * @see SecurityScheme
     * @see SecurityRequirement
     */
    @Bean
    OpenAPI customOpenAPI() {
        // Security scheme name constant for JWT Bearer authentication
        String bearerAuth = "BearerAuth";

        // Configure API Gateway server
        Server apiGatewayService = new Server()
                .url("http://localhost:8080")
                .description("API-Gateway");

        // Configure direct Auth Service server
        Server authService = new Server()
                .url("http://localhost:8081")
                .description("Auth-Service");

        // Build and return the complete OpenAPI configuration
        return new OpenAPI()
                // API metadata
                .info(new Info()
                        .title("Auth Service API")
                        .version("1.0")
                        .description("API documentation for Auth Service"))

                // Server configurations
                .servers(List.of(apiGatewayService, authService))

                // Global security requirement
                .addSecurityItem(new SecurityRequirement().addList(bearerAuth))

                // Security scheme definition
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(bearerAuth,
                                new SecurityScheme()
                                        .name(bearerAuth)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}