package com.hcltech.universityservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main application class for the University Service microservice.
 * <p>
 * This service manages universities, courses, and administrative operations
 * within the educational platform. It provides RESTful APIs for creating,
 * updating, and querying university and course information.
 * </p>
 * <p>
 * The service is configured as a Spring Cloud microservice with:
 * <ul>
 *   <li>Service discovery integration for registration with Eureka</li>
 *   <li>Feign client support for inter-service communication</li>
 *   <li>JWT-based authentication and authorization</li>
 * </ul>
 * </p>
 *
 * @author HCL Tech
 * @version 1.0
 * @since 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class UniversityserviceApplication {

	/**
	 * Application entry point that bootstraps the University Service.
	 *
	 * @param args command line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(UniversityserviceApplication.class, args);
	}

}
