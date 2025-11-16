package com.hcltech.studentservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration class for API documentation.
 *
 * This configuration class sets up the OpenAPI 3.0 documentation for the Student Service,
 * including API information, server configurations, and security schemes. It enables
 * interactive API documentation through Swagger UI with JWT Bearer token authentication.
 *
 * Features:
 * - API metadata and version information
 * - Multiple server configurations (API Gateway and direct service access)
 * - JWT Bearer authentication scheme for secure endpoints
 * - Interactive API testing through Swagger UI
 *
 * @author HCL Technologies
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configures the OpenAPI specification for the Student Service.
     *
     * Defines the API documentation with:
     * - Service metadata (title, version, description)
     * - Available servers (API Gateway on port 8080, Student Service on port 8082)
     * - JWT Bearer authentication security scheme
     *
     * @return OpenAPI the configured OpenAPI object
     */
    @Bean
    OpenAPI customOpenAPI() {
    	Server apiGatewayService = new Server()
                .url("http://localhost:8080")
                .description("API-Gateway");

        Server studentService = new Server()
                .url("http://localhost:8082")
                .description("Student-Service");

		return new OpenAPI()
				.info(new Info().title("Student Service API").version("1.0")
						.description("API documentation for Student Service"))
				.servers(List.of(apiGatewayService, studentService))
				.addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
				.components(new io.swagger.v3.oas.models.Components().addSecuritySchemes("BearerAuth",
						new SecurityScheme().name("BearerAuth").type(SecurityScheme.Type.HTTP).scheme("bearer")
								.bearerFormat("JWT")));
	}
} 