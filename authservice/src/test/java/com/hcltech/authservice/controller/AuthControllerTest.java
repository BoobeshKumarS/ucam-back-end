package com.hcltech.authservice.controller;

import com.hcltech.authservice.dto.MessageResponseDTO;
import com.hcltech.authservice.dto.UserLoginRequestDTO;
import com.hcltech.authservice.dto.UserLoginResponseDTO;
import com.hcltech.authservice.dto.UserRegisterRequestDTO;
import com.hcltech.authservice.dto.UserRegisterResponseDTO;
import com.hcltech.authservice.entity.UserRole;
import com.hcltech.authservice.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthController class.
 *
 * @author HCL Technologies
 * @version 1.0
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthController authController;

    private UserLoginRequestDTO loginRequest;
    private UserRegisterRequestDTO registerRequest;
    private UserLoginResponseDTO loginResponse;
    private UserRegisterResponseDTO userResponse;

    /**
     * Setup method to initialize test data before each test.
     */
    @BeforeEach
    void setUp() {
        loginRequest = new UserLoginRequestDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        registerRequest = new UserRegisterRequestDTO();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");

        loginResponse = new UserLoginResponseDTO();
        loginResponse.setId(UUID.randomUUID());
        loginResponse.setUsername("testuser");
        loginResponse.setEmail("test@example.com");
        loginResponse.setToken("jwt-token");
        loginResponse.setExpiry(24L);

        userResponse = new UserRegisterResponseDTO();
        userResponse.setId(UUID.randomUUID());
        userResponse.setUsername("testuser");
        userResponse.setEmail("test@example.com");
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.STUDENT);
        userResponse.setRoles(roles);
    }

    /**
     * Test successful user authentication.
     */
    @Test
    void testAuthenticateUser_Success() {
        // Arrange
        when(authService.login(loginRequest)).thenReturn(loginResponse);

        // Act
        ResponseEntity<UserLoginResponseDTO> result = authController.authenticateUser(loginRequest);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(loginResponse, result.getBody());
        verify(authService).login(loginRequest);
    }

    /**
     * Test successful user registration.
     */
    @Test
    void testRegisterUser_Success() {
        // Arrange
        MessageResponseDTO messageResponse = new MessageResponseDTO("User registered successfully!");
        when(authService.register(registerRequest))
                .thenReturn(ResponseEntity.ok(messageResponse));

        // Act
        ResponseEntity<MessageResponseDTO> result = authController.registerUser(registerRequest);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("User registered successfully!", result.getBody().getMessage());
        verify(authService).register(registerRequest);
    }

    /**
     * Test getting current username with valid authentication.
     */
    @Test
    void testCurrentUserName_WithAuthentication() {
        // Arrange
        when(authentication.getName()).thenReturn("testuser");

        // Act
        String result = authController.currentUserName(authentication);

        // Assert
        assertEquals("testuser", result);
    }

    /**
     * Test getting current username with null authentication.
     */
    @Test
    void testCurrentUserName_NullAuthentication() {
        // Act
        String result = authController.currentUserName(null);

        // Assert
        assertEquals("", result);
    }

    /**
     * Test getting user details with valid authentication.
     */
    @Test
    void testGetUserDetails_Success() {
        // Arrange
        when(authService.getCurrentUserDetails(authentication)).thenReturn(userResponse);

        // Act
        ResponseEntity<UserRegisterResponseDTO> result = authController.getUserDetails(authentication);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(userResponse, result.getBody());
        verify(authService).getCurrentUserDetails(authentication);
    }

    /**
     * Test successful user logout.
     */
    @Test
    void testSignoutUser_Success() {
        // Arrange
        var responseCookie = mock(org.springframework.http.ResponseCookie.class);
        when(responseCookie.toString()).thenReturn("jwt=; Path=/; HttpOnly; Max-Age=0");
        when(authService.logoutUser()).thenReturn(responseCookie);

        // Act
        ResponseEntity<MessageResponseDTO> result = authController.signoutUser();

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("You've been signed out!", result.getBody().getMessage());
        verify(authService).logoutUser();
    }
}