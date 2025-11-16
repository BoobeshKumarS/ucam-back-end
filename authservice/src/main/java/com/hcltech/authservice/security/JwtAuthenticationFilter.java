package com.hcltech.authservice.security;

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

import com.hcltech.authservice.util.JwtUtil;

import java.io.IOException;
import java.util.List;

/**
 * JWT Authentication Filter that processes JWT tokens in incoming HTTP requests.
 *
 * <p>This filter extends OncePerRequestFilter to ensure it's executed once per request
 * and handles JWT token validation and authentication context setup for Spring Security.
 *
 * <p>Filter process flow:
 * <ol>
 *   <li>Checks if request should be filtered using shouldNotFilter()</li>
 *   <li>Extracts JWT token from Authorization header</li>
 *   <li>Validates token and extracts user information</li>
 *   <li>Sets up Spring Security authentication context</li>
 *   <li>Continues filter chain for authorized requests</li>
 * </ol>
 *
 * @author HCL Technologies
 * @version 1.0
 * @since 1.0
 * @see OncePerRequestFilter
 * @see JwtUtil
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger jwtLogger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Determines which requests should not be processed by this filter.
     *
     * <p>Requests starting with "/authenticate/" path are excluded from JWT filtering,
     * typically used for public endpoints that don't require authentication.
     *
     * @param request the HTTP servlet request
     * @return true if the request should not be filtered, false otherwise
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/authenticate/");
    }

    /**
     * Processes JWT token from Authorization header and sets up authentication context.
     *
     * <p>This method:
     * <ul>
     *   <li>Extracts Bearer token from Authorization header</li>
     *   <li>Validates JWT token signature and expiration</li>
     *   <li>Extracts username and roles from token claims</li>
     *   <li>Creates Spring Security authentication token</li>
     *   <li>Sets authentication in SecurityContextHolder</li>
     * </ul>
     *
     * @param request the HTTP servlet request
     * @param response the HTTP servlet response
     * @param filterChain the filter chain to continue processing
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
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

        List<String> roles = jwtUtil.extractRoles(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            List<GrantedAuthority> authorities = roles.stream()
                    .filter(r -> r != null && !r.isBlank())
                    .map(SimpleGrantedAuthority::new)
                    .<GrantedAuthority>map(a -> a)
                    .toList();
            if (jwtUtil.validateToken(jwt, username)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, null,
                        authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        jwtLogger.info("Authentication filter done...");
        filterChain.doFilter(request, response);
    }
}