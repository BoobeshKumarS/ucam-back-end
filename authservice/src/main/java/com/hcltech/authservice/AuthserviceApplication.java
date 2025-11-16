package com.hcltech.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Authentication Service.
 *
 * <p>This class serves as the entry point for the Spring Boot application that provides
 * authentication and authorization services. It handles user registration, login,
 * JWT token generation, and user management operations.</p>
 *
 * <p>The application includes JWT-based authentication, role-based access control,
 * and integration with Spring Security for secure API endpoints.</p>
 *
 * @author HCL Tech
 * @version 1.0
 * @since 2025-01-01
 */
@SpringBootApplication
public class AuthserviceApplication {

	/**
	 * Main method to start the Spring Boot application.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(AuthserviceApplication.class, args);
	}

}
