package com.hcltech.apigatewayservice.config;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

/**
 * Utility component for JWT (JSON Web Token) operations.
 * <p>
 * This class provides comprehensive functionality for parsing, validating, and
 * extracting information from JWT tokens. It handles token signature verification,
 * expiration checking, and claims extraction using the JJWT library.
 * </p>
 * <p>
 * The utility uses HMAC-SHA algorithm for token signature verification with a
 * Base64-encoded secret key configured via application properties.
 * </p>
 *
 * @author HCL Tech
 * @version 1.0
 * @since 1.0
 */
@Component
public class JwtUtil {

	/**
	 * Base64-encoded secret key used for JWT signature verification.
	 * Injected from application configuration property 'jwt.secret'.
	 */
	@Value("${jwt.secret}")
	private String secret;

	/**
	 * Extracts the username (subject) from the JWT token.
	 * <p>
	 * The username is stored in the standard JWT 'sub' (subject) claim.
	 * </p>
	 *
	 * @param token the JWT token string
	 * @return the username extracted from the token
	 * @throws io.jsonwebtoken.JwtException if the token is malformed or signature is invalid
	 */
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	/**
	 * Extracts the single role from the JWT token.
	 * <p>
	 * This method retrieves a custom 'role' claim from the token payload.
	 * Note: For multiple roles, use {@link #extractRoles(String)} instead.
	 * </p>
	 *
	 * @param token the JWT token string
	 * @return the role extracted from the token, or null if not present
	 * @throws io.jsonwebtoken.JwtException if the token is malformed or signature is invalid
	 */
	public String extractRole(String token) {
		return extractClaim(token, claims -> claims.get("role", String.class));
	}

	/**
	 * Extracts a specific claim from the JWT token using a custom claims resolver function.
	 * <p>
	 * This generic method allows extraction of any claim from the token by providing
	 * a function that operates on the {@link Claims} object.
	 * </p>
	 *
	 * @param <T> the type of the claim value to extract
	 * @param token the JWT token string
	 * @param claimsResolver a function to extract the desired claim from the Claims object
	 * @return the extracted claim value
	 * @throws io.jsonwebtoken.JwtException if the token is malformed or signature is invalid
	 */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * Parses and extracts all claims from the JWT token.
	 * <p>
	 * This private method performs token signature verification using the configured
	 * secret key and returns all claims contained in the token payload.
	 * </p>
	 *
	 * @param token the JWT token string
	 * @return all claims from the token payload
	 * @throws io.jsonwebtoken.JwtException if the token is malformed or signature is invalid
	 */
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}

	/**
	 * Validates the JWT token by verifying the username and expiration.
	 * <p>
	 * A token is considered valid if:
	 * <ul>
	 *     <li>The extracted username matches the provided username</li>
	 *     <li>The token has not expired</li>
	 *     <li>The signature is valid (verified during extraction)</li>
	 * </ul>
	 * </p>
	 *
	 * @param token the JWT token string to validate
	 * @param username the expected username to match against the token's subject
	 * @return true if the token is valid, false otherwise
	 * @throws io.jsonwebtoken.JwtException if the token is malformed or signature is invalid
	 */
	public Boolean validateToken(String token, String username) {
		final String extractedUsername = extractUsername(token);
		return (extractedUsername.equals(username) && !isTokenExpired(token));
	}

	/**
	 * Checks if the JWT token has expired.
	 * <p>
	 * Compares the token's expiration time against the current system time.
	 * </p>
	 *
	 * @param token the JWT token string
	 * @return true if the token has expired, false otherwise
	 * @throws io.jsonwebtoken.JwtException if the token is malformed or signature is invalid
	 */
	private Boolean isTokenExpired(String token) {
		return extractClaim(token, Claims::getExpiration).before(new Date());
	}

	/**
	 * Constructs the cryptographic key used for JWT signature verification.
	 * <p>
	 * The secret key is Base64-decoded and converted to an HMAC-SHA key suitable
	 * for use with the JJWT library.
	 * </p>
	 *
	 * @return the signing key for JWT verification
	 */
	private Key getSigningKey() {
		byte[] keyBytes = Base64.getDecoder().decode(secret);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	/**
	 * Extracts the list of roles from the JWT token.
	 * <p>
	 * This method retrieves a custom 'roles' claim from the token payload,
	 * which should contain a list of role names assigned to the user.
	 * </p>
	 *
	 * @param token the JWT token string
	 * @return the list of roles extracted from the token, or null if not present
	 * @throws io.jsonwebtoken.JwtException if the token is malformed or signature is invalid
	 */
	public List<String> extractRoles(String token) {
		return extractClaim(token, claims -> claims.get("roles", List.class));
	}
}