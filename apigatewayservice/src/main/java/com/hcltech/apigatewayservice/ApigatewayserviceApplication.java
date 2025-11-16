package com.hcltech.apigatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the API Gateway Service.
 * <p>
 * This Spring Boot application serves as the entry point for all client requests
 * in the microservices architecture. It acts as a reverse proxy, routing requests
 * to appropriate backend services while providing centralized authentication,
 * authorization, and cross-cutting concerns.
 * </p>
 * <p>
 * The gateway handles JWT-based authentication and enforces role-based access
 * control before forwarding requests to downstream services.
 * </p>
 *
 * @author HCL Tech
 * @version 1.0
 * @since 1.0
 */
@SpringBootApplication
public class ApigatewayserviceApplication {

	/**
	 * Main entry point for the API Gateway Service application.
	 * <p>
	 * Bootstraps the Spring Boot application context and starts the embedded
	 * web server to begin processing incoming requests.
	 * </p>
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(ApigatewayserviceApplication.class, args);
	}

}
