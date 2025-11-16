package com.hcltech.studentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main application class for the Student Service microservice.
 *
 * This Spring Boot application provides student management functionality including
 * registration, profile management, and CRUD operations. It is designed to work
 * as part of a microservices architecture with service discovery and Feign client
 * communication capabilities.
 *
 * Key features:
 * - Spring Boot auto-configuration for rapid development
 * - Service discovery integration via Eureka
 * - Feign client support for inter-service communication
 * - JWT-based authentication and authorization
 * - RESTful API endpoints for student operations
 *
 * @author HCL Technologies
 * @version 1.0
 * @since 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class StudentserviceApplication {

	/**
	 * Main entry point for the Student Service application.
	 *
	 * Bootstraps the Spring Boot application context and starts the embedded
	 * web server to handle incoming HTTP requests.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(StudentserviceApplication.class, args);
	}

}
