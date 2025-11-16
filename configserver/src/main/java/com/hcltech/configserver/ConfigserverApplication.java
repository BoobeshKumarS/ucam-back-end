package com.hcltech.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Spring Cloud Config Server Application.
 *
 * <p>This application serves as a centralized configuration server for microservices architecture.
 * It provides externalized configuration management, allowing applications to retrieve their
 * configuration properties from a central location at runtime.</p>
 *
 * <p>The Config Server can serve configuration from various backends including Git repositories,
 * local file systems, or HashiCorp Vault. This enables environment-specific configurations
 * without requiring application redeployment.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Centralized configuration management for distributed systems</li>
 *   <li>Version-controlled configuration through Git integration</li>
 *   <li>Environment-specific property resolution</li>
 *   <li>Real-time configuration updates without service restarts</li>
 *   <li>Security through encryption of sensitive properties</li>
 * </ul>
 *
 * @author HCLTech
 * @version 1.0
 * @since 1.0
 * @see org.springframework.cloud.config.server.EnableConfigServer
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigserverApplication {

	/**
	 * Main entry point for the Spring Cloud Config Server application.
	 *
	 * <p>This method bootstraps the Spring Boot application context and starts the
	 * embedded web server. The Config Server will begin listening for configuration
	 * requests from client applications on the configured port (default: 8888).</p>
	 *
	 * <p>The application will initialize all necessary Spring Cloud Config components,
	 * establish connections to the configured configuration repository, and expose
	 * RESTful endpoints for configuration retrieval.</p>
	 *
	 * @param args command-line arguments passed to the application. These can include
	 *             Spring Boot configuration overrides such as server port, profile
	 *             selection, or logging levels. Example: --server.port=8889
	 * @throws IllegalArgumentException if invalid command-line arguments are provided
	 * @throws IllegalStateException if the application context cannot be initialized
	 *                               or required configuration properties are missing
	 * @see SpringApplication#run(Class, String...)
	 */
	public static void main(String[] args) {
		SpringApplication.run(ConfigserverApplication.class, args);
	}

}
