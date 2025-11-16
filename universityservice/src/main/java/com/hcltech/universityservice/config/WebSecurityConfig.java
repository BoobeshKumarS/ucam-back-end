package com.hcltech.universityservice.config;

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

import java.util.Arrays;

/**
 * Web security configuration for the University Service.
 * <p>
 * This configuration class defines security policies including:
 * <ul>
 *   <li>JWT-based authentication using custom filter</li>
 *   <li>Stateless session management</li>
 *   <li>Role-based access control (RBAC) for API endpoints</li>
 *   <li>CORS configuration for cross-origin requests</li>
 *   <li>Public endpoint access rules</li>
 * </ul>
 * </p>
 * <p>
 * Public endpoints (no authentication required):
 * - Swagger documentation endpoints
 * - Admin registration
 * - University and course read operations (GET)
 * </p>
 * <p>
 * Protected endpoints (ADMIN role required):
 * - All POST, PUT, DELETE operations for universities and courses
 * - Admin management operations
 * </p>
 *
 * @author HCL Tech
 * @version 1.0
 * @since 1.0
 * @see JwtAuthenticationFilter
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

	/**
	 * JWT authentication filter for processing and validating JWT tokens.
	 */
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	/**
	 * Configures the security filter chain with authentication and authorization rules.
	 * <p>
	 * This method sets up:
	 * <ul>
	 *   <li>CSRF protection disabled (suitable for stateless REST APIs)</li>
	 *   <li>Stateless session management</li>
	 *   <li>Authorization rules for different endpoints</li>
	 *   <li>JWT authentication filter before username/password authentication</li>
	 * </ul>
	 * </p>
	 *
	 * @param http the HttpSecurity to configure
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
								"/universityservice/v3/api-docs/**",
								"/swagger-ui/**",
								"/swagger-ui.html",
								"/actuator/**",
								"/error",
								
								"/api/admins/register",
								
								"/api/universities",
                                "/api/universities/*",
                                "/api/universities/*/courses",
								
								"/api/courses",
								"/api/courses/university/*",
								"/api/courses/*"
								)
						.permitAll()
//						.requestMatchers(HttpMethod.GET, "/api/courses").permitAll()
						.requestMatchers(
								"/api/admins/**",
								"/api/universities/**",
								"/api/courses/**"
								).hasAnyRole("ADMIN")
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
	 * Configures Cross-Origin Resource Sharing (CORS) settings.
	 * <p>
	 * This configuration allows the React frontend application to access
	 * the University Service APIs from a different origin (localhost:3000).
	 * </p>
	 *
	 * @return configured CorsConfigurationSource with allowed origins, methods, and headers
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
	 * Provides the AuthenticationManager bean for authentication operations.
	 *
	 * @param authConfig the authentication configuration
	 * @return the configured AuthenticationManager
	 * @throws Exception if an error occurs retrieving the authentication manager
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}
}
