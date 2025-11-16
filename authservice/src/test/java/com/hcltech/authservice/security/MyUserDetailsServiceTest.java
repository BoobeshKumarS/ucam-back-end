package com.hcltech.authservice.security;

import com.hcltech.authservice.entity.User;
import com.hcltech.authservice.entity.UserRole;
import com.hcltech.authservice.exception.UserNotFoundException;
import com.hcltech.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MyUserDetailsService class.
 *
 * @author HCL Technologies
 * @version 1.0
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MyUserDetailsService userDetailsService;

    private User testUser;

    /**
     * Setup method to initialize test data before each test.
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");

        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.STUDENT);
        testUser.setRoles(roles);
    }

    /**
     * Test successful user loading by username (email).
     */
    @Test
    void testLoadUserByUsername_Success() {
        // Arrange
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        verify(userRepository).findByEmail("test@example.com");
    }

    /**
     * Test user loading when user not found.
     */
    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonexistent@example.com"));
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    /**
     * Test user loading with null email.
     */
    @Test
    void testLoadUserByUsername_NullEmail() {
        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(null));
    }

    /**
     * Test user loading with empty email.
     */
    @Test
    void testLoadUserByUsername_EmptyEmail() {
        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(""));
    }
}