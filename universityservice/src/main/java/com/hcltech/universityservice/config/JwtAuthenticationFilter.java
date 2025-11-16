package com.hcltech.universityservice.config;

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

import com.hcltech.universityservice.util.JwtUtil;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT authentication filter that intercepts HTTP requests to validate JWT tokens.
 * <p>
 * This filter extends {@link OncePerRequestFilter} to ensure it is executed once per request.
 * It extracts and validates JWT tokens from the Authorization header, and if valid,
 * sets up the Spring Security authentication context with the user's credentials and authorities.
 * </p>
 * <p>
 * The filter:
 * <ul>
 *   <li>Extracts the JWT token from the "Bearer" Authorization header</li>
 *   <li>Validates the token and extracts username and roles</li>
 *   <li>Creates an authentication token and sets it in the SecurityContext</li>
 *   <li>Skips filtering for paths starting with "/authenticate/"</li>
 * </ul>
 * </p>
 *
 * @author HCL Tech
 * @version 1.0
 * @since 1.0
 * @see OncePerRequestFilter
 * @see JwtUtil
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	/**
	 * Utility class for JWT token operations.
	 */
	@Autowired
	private JwtUtil jwtUtil;

	/**
	 * Determines if the filter should be skipped for the given request.
	 * <p>
	 * This filter is not applied to paths starting with "/authenticate/",
	 * allowing authentication endpoints to be accessed without a JWT token.
	 * </p>
	 *
	 * @param request the HTTP request
	 * @return true if the filter should not be applied to this request, false otherwise
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		return path.startsWith("/authenticate/");
	}

	/**
	 * Performs the JWT authentication filtering logic.
	 * <p>
	 * This method extracts the JWT token from the Authorization header,
	 * validates it, and if valid, sets up the Spring Security authentication
	 * context with the user's credentials and granted authorities.
	 * </p>
	 *
	 * @param request the HTTP request
	 * @param response the HTTP response
	 * @param filterChain the filter chain for continuing request processing
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs during request processing
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