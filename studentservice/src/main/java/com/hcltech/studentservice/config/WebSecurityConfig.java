package com.hcltech.studentservice.config;

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
 * Web security configuration class for the Student Service.
 *
 * This configuration class sets up Spring Security with JWT-based authentication,
 * stateless session management, CORS policies, and role-based access control.
 * It defines security rules for different endpoints and integrates custom JWT
 * authentication filter.
 *
 * Key security features:
 * - JWT token-based authentication
 * - Stateless session management
 * - CORS configuration for cross-origin requests
 * - Method-level security with pre-post annotations
 * - Role-based authorization (ADMIN, STUDENT)
 *
 * @author HCL Technologies
 * @version 1.0
 * @since 1.0
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
	 * This method sets up the security rules including:
	 * - Disabling CSRF protection (suitable for stateless JWT authentication)
	 * - Configuring stateless session management
	 * - Defining authorization rules for different endpoints
	 * - Integrating JWT authentication filter
	 *
	 * Public endpoints (no authentication required):
	 * - Swagger/OpenAPI documentation paths
	 * - Actuator endpoints
	 * - Student registration endpoint
	 *
	 * Protected endpoints require STUDENT or ADMIN role.
	 *
	 * @param http the HttpSecurity object to configure
	 * @return SecurityFilterChain the configured security filter chain
	 * @throws Exception if an error occurs during configuration
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
								"/studentservice/v3/api-docs/**",
								"/swagger-ui/**",
								"/swagger-ui.html",
								"/actuator/**",
								"/error",

								"/api/students/register"
								)
						.permitAll()
						.requestMatchers("/api/students/**").hasAnyRole("STUDENT", "ADMIN")
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
	 * Configures CORS (Cross-Origin Resource Sharing) settings.
	 *
	 * Allows the React frontend application to make cross-origin requests
	 * to this service. Configured to accept requests from localhost:3000
	 * with credentials and all standard HTTP methods.
	 *
	 * @return CorsConfigurationSource the configured CORS configuration source
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(Arrays.asList("*"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	/**
	 * Provides the authentication manager bean.
	 *
	 * Exposes the authentication manager from Spring Security's authentication
	 * configuration for use in custom authentication flows if needed.
	 *
	 * @param authConfig the authentication configuration
	 * @return AuthenticationManager the configured authentication manager
	 * @throws Exception if an error occurs during configuration
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}
}
