package com.hcltech.eurekaservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka Service Discovery Server Application.
 * <p>
 * This class serves as the entry point for the Netflix Eureka Service Registry server.
 * It provides service registration and discovery capabilities for microservices architecture,
 * allowing services to register themselves and discover other services dynamically.
 * </p>
 * <p>
 * The application is configured as a Spring Boot application with Eureka Server capabilities
 * enabled through the {@link EnableEurekaServer} annotation. All microservices in the ecosystem
 * can register with this server and query it to locate other services.
 * </p>
 * <p>
 * Key Features:
 * <ul>
 *   <li>Service registration - Microservices register their instances with the Eureka server</li>
 *   <li>Service discovery - Clients can discover and communicate with registered services</li>
 *   <li>Health monitoring - Tracks the health status of registered service instances</li>
 *   <li>Load balancing support - Enables client-side load balancing capabilities</li>
 * </ul>
 * </p>
 *
 * @author HCL Technologies
 * @version 1.0
 * @since 1.0
 * @see org.springframework.cloud.netflix.eureka.server.EnableEurekaServer
 * @see org.springframework.boot.SpringApplication
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaserviceApplication {

	/**
	 * Main entry point for the Eureka Service Discovery Server application.
	 * <p>
	 * This method bootstraps the Spring Boot application context and starts the
	 * embedded web server with Eureka Server capabilities. Once started, the server
	 * will be ready to accept service registrations and handle discovery requests
	 * from client applications.
	 * </p>
	 * <p>
	 * The application will bind to the port specified in the configuration
	 * (typically 8761 for Eureka servers) and expose the Eureka dashboard UI
	 * and REST endpoints for service registration and discovery.
	 * </p>
	 *
	 * @param args command-line arguments passed to the application.
	 *             These can be used to override default Spring Boot configuration properties.
	 *             Example: --server.port=8761 --spring.application.name=eureka-server
	 * @throws IllegalArgumentException if the application context cannot be created
	 *                                  due to invalid configuration or missing dependencies
	 * @see SpringApplication#run(Class, String...)
	 */
	public static void main(String[] args) {
		SpringApplication.run(EurekaserviceApplication.class, args);
	}

}
