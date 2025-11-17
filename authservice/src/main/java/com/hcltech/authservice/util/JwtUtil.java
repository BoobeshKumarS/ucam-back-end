package com.hcltech.authservice.util;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hcltech.authservice.entity.UserRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

/**
 * Utility class for JWT token operations including generation, validation, and extraction.
 *
 * <p>This component handles all JWT-related operations using the JJWT library
 * and provides methods for:
 * <ul>
 *   <li>Token generation with user claims</li>
 *   <li>Token validation and expiration checking</li>
 *   <li>Claim extraction (username, roles, etc.)</li>
 *   <li>Token signature verification</li>
 * </ul>
 *
 * <p>Uses HMAC-SHA512 algorithm for token signing and Base64-encoded secret key.
 *
 * @author Boobesh Kumar S
 * @version 1.0
 * @since 2025-11-17
 * @see io.jsonwebtoken.Jwts
 * @see SignatureAlgorithm
 */
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    /**
     * Extracts username from JWT token.
     *
     * @param token the JWT token
     * @return username extracted from token subject
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Calculates remaining validity time of the token in hours.
     *
     * @param token the JWT token to check
     * @return number of hours until token expiration
     */
    public long getTokenValidityInHours(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        long durationMillis = expiration.getTime() - System.currentTimeMillis();
        return TimeUnit.MILLISECONDS.toHours(durationMillis);
    }

    /**
     * Extracts role from JWT token claims.
     *
     * @param token the JWT token
     * @return role string from token claims
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Generic method to extract specific claim from JWT token.
     *
     * @param <T> the type of claim to extract
     * @param token the JWT token
     * @param claimsResolver function to extract specific claim from Claims
     * @return the extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from JWT token.
     *
     * @param token the JWT token
     * @return Claims object containing all token claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    /**
     * Validates JWT token against username and expiration.
     *
     * @param token the JWT token to validate
     * @param username the username to validate against
     * @return true if token is valid and not expired, false otherwise
     */
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * Checks if JWT token is expired.
     *
     * @param token the JWT token to check
     * @return true if token is expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /**
     * Generates a new JWT token for authenticated user.
     *
     * @param username the user's username
     * @param email the user's email address
     * @param roles the set of user roles for authorization
     * @return signed JWT token string
     */
    public String generateToken(String username, String email, Set<UserRole> roles) {
        Map<String, Object> claims = new HashMap<>();
//        claims.put("roles", roles); // Ensure role is correctly added
        claims.put("roles", roles.stream().map(r -> "ROLE_" + r.name()).toList());

        return createToken(claims, username, email);
    }

    /**
     * Creates the signing key from Base64 encoded secret.
     *
     * @return Key object for JWT signing
     */
    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Creates JWT token with specified claims and subject.
     *
     * @param claims additional claims to include in token
     * @param subject the token subject (typically username)
     * @param email the user's email address
     * @return signed JWT token string
     */
    private String createToken(Map<String, Object> claims, String subject, String email) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .claim("email", email)
                .claim("username", subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512).compact();
    }

    /**
     * Extracts roles list from JWT token claims.
     *
     * @param token the JWT token
     * @return list of role strings from token claims
     */
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", List.class));
    }
}