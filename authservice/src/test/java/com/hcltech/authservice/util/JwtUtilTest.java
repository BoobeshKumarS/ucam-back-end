package com.hcltech.authservice.util;

import com.hcltech.authservice.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JwtUtil class.
 *
 * @author HCL Technologies
 * @version 1.0
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private String testSecret = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
    private long testExpiration = 3600000; // 1 hour

    private String testToken;
    private String username = "testuser";
    private String email = "test@example.com";

    /**
     * Setup method to initialize JwtUtil configuration.
     */
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "secret", testSecret);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationInMs", testExpiration);

        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.STUDENT);
        testToken = jwtUtil.generateToken(username, email, roles);
    }

    /**
     * Test token generation.
     */
    @Test
    void testGenerateToken() {
        // Arrange
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.ADMIN);

        // Act
        String token = jwtUtil.generateToken(username, email, roles);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    /**
     * Test username extraction from token.
     */
    @Test
    void testExtractUsername() {
        // Act
        String extractedUsername = jwtUtil.extractUsername(testToken);

        // Assert
        assertEquals(email, extractedUsername);
    }

    /**
     * Test token validation with valid token.
     */
    @Test
    void testValidateToken_ValidToken() {
        // Act & Assert
        assertTrue(jwtUtil.validateToken(testToken, email));
    }

    /**
     * Test token validation with invalid username.
     */
    @Test
    void testValidateToken_InvalidUsername() {
        // Act & Assert
        assertFalse(jwtUtil.validateToken(testToken, "wrong@example.com"));
    }

    /**
     * Test role extraction from token.
     */
    @Test
    void testExtractRoles() {
        // Act
        List<String> roles = jwtUtil.extractRoles(testToken);

        // Assert
        assertNotNull(roles);
        assertFalse(roles.isEmpty());
        assertTrue(roles.contains("ROLE_STUDENT"));
    }

    /**
     * Test token validity period calculation.
     */
    @Test
    void testGetTokenValidityInHours() {
        // Act
        long validityHours = jwtUtil.getTokenValidityInHours(testToken);

        // Assert
        assertTrue(validityHours > 0);
        assertTrue(validityHours <= 1);
    }

    /**
     * Test individual role extraction.
     */
    @Test
    void testExtractRole() {
        // Act
        String role = jwtUtil.extractRole(testToken);

        // Assert
        assertNull(role); // This returns null as we're using roles list now
    }
}