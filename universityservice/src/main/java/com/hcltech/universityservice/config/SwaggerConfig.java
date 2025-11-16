package com.hcltech.universityservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Swagger/OpenAPI documentation.
 * <p>
 * This configuration sets up comprehensive API documentation for the University Service,
 * including security schemes, server configurations, and API metadata.
 * The generated documentation is accessible through Swagger UI.
 * </p>
 *
 * @author HCL Tech
 * @version 1.0
 * @since 1.0
 * @see OpenAPI
 */
@Configuration
public class SwaggerConfig {

	/**
	 * Creates and configures the OpenAPI specification for the University Service API.
	 * <p>
	 * This method configures:
	 * <ul>
	 *   <li>API metadata (title, version, description)</li>
	 *   <li>Multiple server configurations (API Gateway and direct service access)</li>
	 *   <li>JWT Bearer token authentication scheme</li>
	 * </ul>
	 * </p>
	 *
	 * @return configured OpenAPI instance with complete API documentation setup
	 */
    @Bean
    OpenAPI customOpenAPI() {
    	Server apiGatewayService = new Server()
                .url("http://localhost:8080")
                .description("API-Gateway");

        Server universityService = new Server()
                .url("http://localhost:8083")
                .description("University-Service");
        
		return new OpenAPI()
				.info(new Info().title("University Service API").version("1.0")
						.description("API documentation for University Service"))
				.servers(List.of(apiGatewayService, universityService))
				.addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
				.components(new io.swagger.v3.oas.models.Components().addSecuritySchemes("BearerAuth",
						new SecurityScheme().name("BearerAuth").type(SecurityScheme.Type.HTTP).scheme("bearer")
								.bearerFormat("JWT")));
	}
}