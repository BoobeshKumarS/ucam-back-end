package com.hcltech.apigatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Main security configuration class for the API Gateway Service.
 * <p>
 * This configuration establishes comprehensive security controls for the reactive
 * Spring WebFlux application, including:
 * <ul>
 *     <li>JWT-based stateless authentication</li>
 *     <li>Role-based authorization for various endpoints</li>
 *     <li>CSRF protection (disabled for stateless APIs)</li>
 *     <li>Custom exception handling for authentication and authorization failures</li>
 *     <li>Public access configuration for authentication, registration, and documentation endpoints</li>
 * </ul>
 * </p>
 * <p>
 * The security filter chain processes requests through JWT authentication filters
 * and enforces role-based access control (RBAC) with ADMIN and STUDENT roles.
 * </p>
 *
 * @author HCL Tech
 * @version 1.0
 * @since 1.0
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Configures the Spring Security filter chain for the API Gateway.
     * <p>
     * This method establishes the complete security configuration including:
     * </p>
     * <h3>Stateless Configuration</h3>
     * <ul>
     *     <li>No server-side sessions using {@link NoOpServerSecurityContextRepository}</li>
     *     <li>CSRF protection disabled (suitable for stateless JWT APIs)</li>
     *     <li>HTTP Basic and form login disabled</li>
     * </ul>
     *
     * <h3>Exception Handling</h3>
     * <ul>
     *     <li>Custom 401 Unauthorized responses for authentication failures</li>
     *     <li>Custom 403 Forbidden responses for authorization failures</li>
     *     <li>JSON-formatted error responses</li>
     *     <li>Removal of WWW-Authenticate header to prevent browser authentication popups</li>
     * </ul>
     *
     * <h3>Authorization Rules</h3>
     * <ul>
     *     <li><b>Public Access:</b> CORS preflight, Swagger/OpenAPI docs, actuator endpoints</li>
     *     <li><b>Authentication Endpoints:</b> /api/auth/test, /api/auth/register, /api/auth/login</li>
     *     <li><b>Registration Endpoints:</b> /api/students/register, /api/admins/register</li>
     *     <li><b>Public Read-Only:</b> GET requests to universities and courses</li>
     *     <li><b>Role-Based:</b> ADMIN and STUDENT roles for protected resources</li>
     *     <li><b>Default:</b> All other endpoints require authentication</li>
     * </ul>
     *
     * @param http the {@link ServerHttpSecurity} configuration object
     * @param authenticationWebFilter the JWT authentication web filter for token validation
     * @return the configured security web filter chain
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            AuthenticationWebFilter authenticationWebFilter
    ) {
        http
            // Stateless + no CSRF for APIs
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

            // Return clean 401/403 without Basic popup
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((exchange, e) -> {
                    var response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    response.getHeaders().remove(HttpHeaders.WWW_AUTHENTICATE);
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    var body = """
                        {"error":"unauthorized","message":"Authentication required"}
                        """.getBytes(StandardCharsets.UTF_8);
                    return response.writeWith(Mono.just(response.bufferFactory().wrap(body)));
                })
                .accessDeniedHandler((exchange, e) -> {
                    var response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.FORBIDDEN);
                    response.getHeaders().remove(HttpHeaders.WWW_AUTHENTICATE);
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    var body = """
                        {"error":"forbidden","message":"Insufficient permissions"}
                        """.getBytes(StandardCharsets.UTF_8);
                    return response.writeWith(Mono.just(response.bufferFactory().wrap(body)));
                })
            )

            .authorizeExchange(exchange -> exchange
                // CORS preflight
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Swagger & Actuator
                .pathMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/webjars/**",
                    "/swagger-resources/**",
                    "/actuator/**",

                    // service-specific OpenAPI if you route them through gateway
                    "/authservice/v3/api-docs/**",
                    "/studentservice/v3/api-docs/**",
                    "/universityservice/v3/api-docs/**",
                    "/applicationservice/v3/api-docs/**"
                ).permitAll()

                // Auth endpoints (open)
                .pathMatchers(
                    "/api/auth/test",
                    "/api/auth/register",
                    "/api/auth/login"
                ).permitAll()

                // Registration
                .pathMatchers(
                    "/api/students/register",
                    "/api/admins/register"
                ).permitAll()

                // Public READ endpoints only
                .pathMatchers(HttpMethod.GET,
                    "/api/universities",
                    "/api/universities/*",
                    "/api/universities/*/courses",
                    "/api/courses",
                    "/api/courses/university/*",
                    "/api/courses/*"
                ).permitAll()

                // Role-protected
                .pathMatchers("/api/auth/**").hasAnyRole("ADMIN", "STUDENT")
                .pathMatchers("/api/students/**").hasAnyRole("STUDENT", "ADMIN")
                .pathMatchers(
                    "/api/admins/**",
                    "/api/universities/**",
                    "/api/courses/**"
                ).hasRole("ADMIN")

                // Everything else requires authentication
                .anyExchange().authenticated()
            )

            // Register your JWT AuthenticationWebFilter in the chain
            .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }
}