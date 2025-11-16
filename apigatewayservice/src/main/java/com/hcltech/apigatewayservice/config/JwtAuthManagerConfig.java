package com.hcltech.apigatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

/**
 * Configuration class for JWT-based reactive authentication management.
 * <p>
 * This configuration provides a custom {@link ReactiveAuthenticationManager} bean
 * that validates JWT tokens and creates authenticated security contexts for
 * reactive web applications using Spring WebFlux.
 * </p>
 * <p>
 * The authentication manager extracts and validates JWT tokens, retrieves user
 * information and roles, and constructs Spring Security authentication objects
 * for downstream security filters.
 * </p>
 *
 * @author HCL Tech
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class JwtAuthManagerConfig {

    /**
     * Utility component for JWT token operations including parsing, validation,
     * and claims extraction.
     */
    private final JwtUtil jwtUtil;

    /**
     * Constructs a new JwtAuthManagerConfig with the required JWT utility.
     *
     * @param jwtUtil the JWT utility component for token operations
     */
    public JwtAuthManagerConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Creates and configures a reactive authentication manager for JWT validation.
     * <p>
     * This manager performs the following operations:
     * <ul>
     *     <li>Extracts the JWT token from authentication credentials</li>
     *     <li>Validates the token signature and expiration</li>
     *     <li>Extracts username and roles from token claims</li>
     *     <li>Ensures role names are prefixed with "ROLE_" for Spring Security</li>
     *     <li>Constructs an authenticated {@link Authentication} object</li>
     * </ul>
     * </p>
     * <p>
     * Returns an empty {@link Mono} if the token is missing, invalid, or expired,
     * which will result in an authentication failure.
     * </p>
     *
     * @return a configured reactive authentication manager bean
     */
    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        return authentication -> {
            String token = (String) authentication.getCredentials();
            if (token == null) {
                return Mono.empty();
            }

            String username = jwtUtil.extractUsername(token);
            if (username == null || !jwtUtil.validateToken(token, username)) {
                return Mono.empty();
            }

            var roles = jwtUtil.extractRoles(token);
            var authorities = roles.stream()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

            Authentication auth = new UsernamePasswordAuthenticationToken(username, token, authorities);
            return Mono.just(auth);
        };
    }
}