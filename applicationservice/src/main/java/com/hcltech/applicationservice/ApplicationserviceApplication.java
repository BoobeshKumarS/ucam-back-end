package com.hcltech.applicationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main application class for the Application Service microservice.
 *
 * This service handles student application submissions to universities,
 * managing the complete lifecycle of applications from draft creation
 * through submission and review. It integrates with the University Service
 * via Feign clients to retrieve university and course information.
 *
 * Key features include:
 * - Application form management (create, update, submit, delete)
 * - Application status tracking and updates
 * - Personal information management
 * - Integration with university and course services
 * - JWT-based authentication and authorization
 *
 * @author HCLTech
 * @version 1.0
 * @since 2025-01-01
 */
@SpringBootApplication
@EnableFeignClients
public class ApplicationserviceApplication {

	/**
	 * Main entry point for the Application Service application.
	 *
	 * Initializes the Spring Boot application context and starts
	 * the embedded web server on the configured port.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(ApplicationserviceApplication.class, args);
	}

}
