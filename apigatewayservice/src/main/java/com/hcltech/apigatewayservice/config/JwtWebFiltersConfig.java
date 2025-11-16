package com.hcltech.apigatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Configuration class for JWT authentication web filters in a reactive Spring WebFlux application.
 * <p>
 * This configuration creates and configures the {@link AuthenticationWebFilter} that intercepts
 * incoming HTTP requests, extracts JWT tokens from the Authorization header, and delegates
 * authentication to the {@link ReactiveAuthenticationManager}.
 * </p>
 * <p>
 * The filter operates in a stateless manner without maintaining server-side sessions,
 * converting Bearer tokens to Spring Security authentication objects for processing.
 * </p>
 *
 * @author HCL Tech
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class JwtWebFiltersConfig {

    /**
     * Creates and configures the JWT authentication web filter.
     * <p>
     * This filter performs the following operations:
     * <ul>
     *     <li>Intercepts all incoming HTTP requests</li>
     *     <li>Extracts JWT tokens from the Authorization header</li>
     *     <li>Converts Bearer tokens to authentication objects</li>
     *     <li>Delegates authentication to the provided authentication manager</li>
     *     <li>Operates statelessly without server-side sessions</li>
     * </ul>
     * </p>
     * <p>
     * The filter uses {@link NoOpServerSecurityContextRepository} to ensure no
     * session state is maintained, making it suitable for stateless JWT authentication.
     * </p>
     *
     * @param authManager the reactive authentication manager for validating JWT tokens
     * @return a configured authentication web filter bean
     */
    @Bean
    public AuthenticationWebFilter authenticationWebFilter(ReactiveAuthenticationManager authManager) {
        AuthenticationWebFilter authWebFilter = new AuthenticationWebFilter(authManager);

        // Stateless (no sessions)
        authWebFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());

        // Convert Authorization: Bearer <token> to Authentication
        authWebFilter.setServerAuthenticationConverter(this::convertFromBearer);

        // Run on any exchange — Spring will decide if it's needed based on matchers/permitAll
        authWebFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.anyExchange());

        return authWebFilter;
    }

    /**
     * Converts an HTTP request's Authorization header containing a Bearer token
     * into a Spring Security authentication object.
     * <p>
     * This method extracts the JWT token from the "Authorization: Bearer {token}"
     * header format and creates an {@link org.springframework.security.authentication.UsernamePasswordAuthenticationToken}
     * with the token as the credential. The authentication manager will later validate
     * this token and populate the principal and authorities.
     * </p>
     * <p>
     * Returns an empty {@link Mono} if:
     * <ul>
     *     <li>No Authorization header is present</li>
     *     <li>The Authorization header doesn't start with "Bearer "</li>
     * </ul>
     * </p>
     *
     * @param exchange the current server web exchange containing the HTTP request
     * @return a Mono containing the authentication object with the JWT token,
     *         or an empty Mono if no valid Bearer token is found
     */
    private Mono<org.springframework.security.core.Authentication> convertFromBearer(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.empty();
        }
        String token = authHeader.substring(7);
        // Put the token into credentials; ReactiveAuthenticationManager will validate it
        return Mono.just(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(null, token));
    }
}
