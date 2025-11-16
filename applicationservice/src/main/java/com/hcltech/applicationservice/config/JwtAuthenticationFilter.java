package com.hcltech.applicationservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hcltech.applicationservice.util.JwtUtil;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT authentication filter that intercepts incoming HTTP requests and validates JWT tokens.
 *
 * This filter extends Spring's {@link OncePerRequestFilter} to ensure it is executed once per request.
 * It extracts the JWT token from the Authorization header, validates it, and sets up the Spring Security
 * authentication context with the user's credentials and authorities.
 *
 * The filter performs the following operations:
 * - Extracts JWT token from the "Bearer" Authorization header
 * - Validates the token and extracts username and roles
 * - Creates a Spring Security authentication object with the user's authorities
 * - Sets the authentication in the SecurityContext for downstream processing
 *
 * Paths starting with "/authenticate/" are excluded from filtering to allow authentication endpoints
 * to be accessed without a token.
 *
 * @author HCLTech
 * @version 1.0
 * @since 2025-01-01
 * @see OncePerRequestFilter
 * @see JwtUtil
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	/**
	 * Logger instance for logging authentication filter operations.
	 */
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	/**
	 * JWT utility for token validation and claims extraction.
	 */
	@Autowired
	private JwtUtil jwtUtil;

	/**
	 * Determines whether this filter should skip processing for a given request.
	 *
	 * This method is called before the filter processes each request. It allows
	 * authentication endpoints to be accessed without JWT token validation.
	 *
	 * @param request the HTTP request to evaluate
	 * @return true if the request path starts with "/authenticate/", false otherwise
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		return path.startsWith("/authenticate/");
	}

	/**
	 * Performs JWT token validation and sets up Spring Security authentication context.
	 *
	 * This method is the core of the JWT authentication process. It:
	 * 1. Extracts the JWT token from the Authorization header
	 * 2. Validates the token format (must start with "Bearer ")
	 * 3. Extracts username and roles from the token
	 * 4. Validates the token's signature and expiration
	 * 5. Creates and sets the authentication object in the SecurityContext
	 *
	 * If the Authorization header is missing or invalid, the request continues
	 * without authentication. If the token is valid, the user is authenticated
	 * with their assigned roles and authorities.
	 *
	 * @param request the HTTP servlet request containing the JWT token
	 * @param response the HTTP servlet response
	 * @param filterChain the filter chain to continue processing the request
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs during filter processing
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final String authorizationHeader = request.getHeader("Authorization");
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		String jwt = authorizationHeader.substring(7);
		String username = jwtUtil.extractUsername(jwt);
		String role = jwtUtil.extractRole(jwt);

		List<String> roles = jwtUtil.extractRoles(jwt);

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			if (role != null && !role.startsWith("ROLE_")) {
				role = "ROLE_" + role; // Ensure role format is correct
			}
			List<GrantedAuthority> authorities = roles.stream().filter(r -> r != null && !r.isBlank())
					.map(SimpleGrantedAuthority::new).collect(Collectors.toList());
			if (jwtUtil.validateToken(jwt, username)) {
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, null,
						authorities);
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		logger.info("Authentication filter done...");
		filterChain.doFilter(request, response);
	}
}