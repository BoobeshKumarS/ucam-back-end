package com.hcltech.authservice.util;

import com.hcltech.authservice.dto.UserLoginResponseDTO;
import com.hcltech.authservice.dto.UserRegisterResponseDTO;
import com.hcltech.authservice.entity.User;
import com.hcltech.authservice.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserConverter class.
 *
 * @author HCL Technologies
 * @version 1.0
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class UserConverterTest {

    @InjectMocks
    private UserConverter userConverter;

    private User testUser;
    private UUID userId;

    /**
     * Setup method to initialize test data before each test.
     */
    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");

        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.STUDENT);
        roles.add(UserRole.ADMIN);
        testUser.setRoles(roles);
    }

    /**
     * Test conversion from User entity to UserRegisterResponseDTO.
     */
    @Test
    void testRegisterEntityToResponse() {
        // Act
        UserRegisterResponseDTO result = userConverter.registerEntityToResponse(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(2, result.getRoles().size());
        assertTrue(result.getRoles().contains(UserRole.STUDENT));
        assertTrue(result.getRoles().contains(UserRole.ADMIN));
    }

    /**
     * Test conversion from User entity to UserLoginResponseDTO.
     */
    @Test
    void testLoginEntityToResponse() {
        // Act
        UserLoginResponseDTO result = userConverter.loginEntityToResponse(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(2, result.getRoles().size());
        assertTrue(result.getRoles().contains(UserRole.STUDENT));
        assertTrue(result.getRoles().contains(UserRole.ADMIN));
        assertNull(result.getToken()); // Token should be set separately
        assertNull(result.getExpiry()); // Expiry should be set separately
    }

    /**
     * Test conversion with null user entity.
     */
    @Test
    void testRegisterEntityToResponse_NullUser() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> userConverter.registerEntityToResponse(null));
    }

    /**
     * Test conversion with minimal user data.
     */
    @Test
    void testLoginEntityToResponse_MinimalUser() {
        // Arrange
        User minimalUser = new User();
        minimalUser.setId(userId);
        minimalUser.setUsername("minimal");
        minimalUser.setEmail("minimal@example.com");
        minimalUser.setRoles(new HashSet<>());

        // Act
        UserLoginResponseDTO result = userConverter.loginEntityToResponse(minimalUser);

        // Assert
        assertNotNull(result);
        assertEquals("minimal", result.getUsername());
        assertEquals("minimal@example.com", result.getEmail());
        assertTrue(result.getRoles().isEmpty());
    }
}