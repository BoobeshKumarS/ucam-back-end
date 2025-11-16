package com.hcltech.applicationservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration for the Application Service API documentation.
 *
 * This configuration class sets up Swagger UI and OpenAPI 3.0 documentation
 * for the Application Service. It configures API information, available servers,
 * and JWT Bearer token authentication scheme for testing protected endpoints.
 *
 * The Swagger UI is accessible at /swagger-ui.html and provides an interactive
 * interface for exploring and testing the API endpoints.
 *
 * Key configurations:
 * - API metadata (title, version, description)
 * - Multiple server configurations (API Gateway and direct service access)
 * - JWT Bearer authentication security scheme
 *
 * @author HCLTech
 * @version 1.0
 * @since 2025-01-01
 * @see OpenAPI
 */
@Configuration
public class SwaggerConfig {
	/**
	 * Creates and configures the OpenAPI specification for the Application Service.
	 *
	 * This bean defines the complete OpenAPI documentation including:
	 * - API information (title, version, description)
	 * - Available servers for API access (API Gateway on port 8080 and direct service on port 8084)
	 * - JWT Bearer token authentication security scheme
	 * - Security requirements for protected endpoints
	 *
	 * The security scheme allows users to test authenticated endpoints directly from
	 * Swagger UI by providing a valid JWT token.
	 *
	 * @return configured OpenAPI instance with all API documentation and security settings
	 */
    @Bean
    OpenAPI customOpenAPI() {
    	Server apiGatewayService = new Server()
                .url("http://localhost:8080")
                .description("API-Gateway");

        Server applicationService = new Server()
                .url("http://localhost:8084")
                .description("Application-Service");
        
		return new OpenAPI()
				.info(new Info().title("Application Service API").version("1.0")
						.description("API documentation for Application Service"))
				.servers(List.of(apiGatewayService, applicationService))
				.addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
				.components(new io.swagger.v3.oas.models.Components().addSecuritySchemes("BearerAuth",
						new SecurityScheme().name("BearerAuth").type(SecurityScheme.Type.HTTP).scheme("bearer")
								.bearerFormat("JWT")));
	}
} 