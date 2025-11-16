package com.hcltech.studentservice.config;

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

import com.hcltech.studentservice.util.JwtUtil;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT authentication filter that intercepts HTTP requests to validate JWT tokens.
 *
 * This filter extends OncePerRequestFilter to ensure it is executed once per request.
 * It extracts JWT tokens from the Authorization header, validates them, and sets up
 * the Spring Security context with authenticated user information and authorities.
 *
 * The filter performs the following operations:
 * - Extracts Bearer token from Authorization header
 * - Validates token authenticity and expiration
 * - Extracts user details and roles from token claims
 * - Populates Security Context with authentication information
 *
 * @author HCL Technologies
 * @version 1.0
 * @since 1.0
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	/**
	 * Logger instance for this class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	/**
	 * Utility class for JWT token operations.
	 */
	@Autowired
	private JwtUtil jwtUtil;

	/**
	 * Determines whether this filter should be skipped for the given request.
	 *
	 * @param request the HTTP request
	 * @return true if the filter should be skipped, false otherwise
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		return path.startsWith("/authenticate/");
	}

	/**
	 * Core filter logic that executes for each HTTP request.
	 *
	 * This method extracts the JWT token from the Authorization header,
	 * validates it, and sets up the authentication in the Security Context.
	 * If the token is valid, the user's details and authorities are loaded
	 * into the security context for authorization checks.
	 *
	 * @param request the HTTP servlet request
	 * @param response the HTTP servlet response
	 * @param filterChain the filter chain to continue processing
	 * @throws ServletException if a servlet error occurs
	 * @throws IOException if an I/O error occurs
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