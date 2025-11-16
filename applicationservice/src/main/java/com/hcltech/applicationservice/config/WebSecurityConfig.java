package com.hcltech.applicationservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * Spring Security configuration for the Application Service.
 *
 * This configuration class establishes the security framework for the application,
 * implementing JWT-based authentication and role-based access control. It configures
 * the security filter chain, CORS policies, and method-level security.
 *
 * Key security features:
 * - Stateless authentication using JWT tokens
 * - CSRF protection disabled (appropriate for stateless APIs)
 * - Role-based access control (STUDENT and ADMIN roles)
 * - CORS configuration for cross-origin requests from the React frontend
 * - Public access to Swagger documentation and health endpoints
 * - JWT authentication filter integrated into the security chain
 *
 * The configuration follows security best practices for microservices:
 * - Stateless session management
 * - Token-based authentication
 * - Method-level authorization with @PreAuthorize
 *
 * @author HCLTech
 * @version 1.0
 * @since 2025-01-01
 * @see JwtAuthenticationFilter
 * @see EnableWebSecurity
 * @see EnableMethodSecurity
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

	/**
	 * JWT authentication filter for validating and processing JWT tokens.
	 */
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	/**
	 * Configures the security filter chain for HTTP requests.
	 *
	 * This method establishes the security rules for the application:
	 * - Disables CSRF protection (suitable for stateless JWT-based APIs)
	 * - Sets stateless session management (no server-side sessions)
	 * - Defines public endpoints (Swagger, actuator, error pages)
	 * - Requires STUDENT or ADMIN role for /api/applications/** endpoints
	 * - Adds JWT authentication filter before the standard username/password filter
	 *
	 * Public endpoints accessible without authentication:
	 * - /applicationservice/v3/api-docs/** (API documentation)
	 * - /swagger-ui/** (Swagger UI interface)
	 * - /actuator/** (health and metrics endpoints)
	 * - /error (error handling)
	 *
	 * @param http the HttpSecurity object to configure
	 * @return configured SecurityFilterChain
	 * @throws Exception if an error occurs during configuration
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
								"/applicationservice/v3/api-docs/**",
								"/swagger-ui/**",
								"/swagger-ui.html",
								"/actuator/**",
								"/error"
								)
						.permitAll()
						.requestMatchers("/api/applications/**").hasAnyRole("STUDENT", "ADMIN")
						.anyRequest().authenticated())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
	}
	
//	@Bean
//    public CorsFilter corsFilter() {
//        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        final CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8080")); // this allows all origin
//        config.addAllowedHeader("*"); // this allows all headers
//        config.addAllowedMethod("OPTIONS");
//        config.addAllowedMethod("HEAD");
//        config.addAllowedMethod("GET");
//        config.addAllowedMethod("PUT");
//        config.addAllowedMethod("POST");
//        config.addAllowedMethod("DELETE");
//        config.addAllowedMethod("PATCH");
//        config.addExposedHeader("Authorization");
//
//        config.setMaxAge((long) 3600000);
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
//    }
	
	/**
	 * Configures CORS (Cross-Origin Resource Sharing) settings for the application.
	 *
	 * This configuration allows the React frontend application running on localhost:3000
	 * to make cross-origin requests to this service. It defines:
	 * - Allowed origins (React app URL)
	 * - Allowed HTTP methods (GET, POST, PUT, DELETE, OPTIONS)
	 * - Allowed headers (all headers accepted)
	 * - Credentials support enabled
	 *
	 * The configuration is applied to all endpoints (/**) in the application.
	 *
	 * @return configured CorsConfigurationSource with CORS policies
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // ✅ React app only
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(Arrays.asList("*"));
		config.setAllowCredentials(true);
 
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
	
	/**
	 * Provides the AuthenticationManager bean for authentication processing.
	 *
	 * This method exposes Spring Security's AuthenticationManager as a Spring bean,
	 * making it available for dependency injection. The AuthenticationManager is
	 * responsible for authenticating users based on provided credentials.
	 *
	 * @param authConfig the AuthenticationConfiguration provided by Spring Security
	 * @return the configured AuthenticationManager
	 * @throws Exception if an error occurs while retrieving the AuthenticationManager
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}
}
