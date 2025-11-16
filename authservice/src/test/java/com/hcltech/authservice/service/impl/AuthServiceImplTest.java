package com.hcltech.authservice.service.impl;

import com.hcltech.authservice.dto.MessageResponseDTO;
import com.hcltech.authservice.dto.UserLoginRequestDTO;
import com.hcltech.authservice.dto.UserLoginResponseDTO;
import com.hcltech.authservice.dto.UserRegisterRequestDTO;
import com.hcltech.authservice.dto.UserRegisterResponseDTO;
import com.hcltech.authservice.entity.User;
import com.hcltech.authservice.entity.UserRole;
import com.hcltech.authservice.exception.UserNotFoundException;
import com.hcltech.authservice.repository.UserRepository;
import com.hcltech.authservice.security.MyUserDetailsService;
import com.hcltech.authservice.util.JwtUtil;
import com.hcltech.authservice.util.UserConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthServiceImpl class.
 *
 * @author HCL Technologies
 * @version 1.0
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserConverter userConverter;

    @Mock
    private MyUserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private UserLoginRequestDTO loginRequest;
    private UserRegisterRequestDTO registerRequest;
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
        testUser.setPassword("encodedPassword");
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.STUDENT);
        testUser.setRoles(roles);

        loginRequest = new UserLoginRequestDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        registerRequest = new UserRegisterRequestDTO();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password");
    }

    /**
     * Test successful user login.
     */
    @Test
    void testLogin_Success() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        UserLoginResponseDTO expectedResponse = new UserLoginResponseDTO();
        expectedResponse.setId(userId);
        expectedResponse.setUsername("testuser");
        expectedResponse.setEmail("test@example.com");
        expectedResponse.setToken("jwt-token");
        expectedResponse.setExpiry(24L);

        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(testUser));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userDetailsService.loadUserByUsername(loginRequest.getEmail()))
                .thenReturn(userDetails);
        when(jwtUtil.generateToken(anyString(), anyString(), anySet()))
                .thenReturn("jwt-token");
        when(jwtUtil.getTokenValidityInHours(anyString()))
                .thenReturn(24L);
        when(userConverter.loginEntityToResponse(testUser))
                .thenReturn(expectedResponse);

        // Act
        UserLoginResponseDTO result = authService.login(loginRequest);

        // Assert
        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
        assertEquals(24L, result.getExpiry());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(loginRequest.getEmail());
    }

    /**
     * Test login when user not found.
     */
    @Test
    void testLogin_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> authService.login(loginRequest));
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verifyNoInteractions(authenticationManager);
    }

    /**
     * Test login with invalid credentials.
     */
    @Test
    void testLogin_InvalidCredentials() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(testUser));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    /**
     * Test successful user registration.
     */
    @Test
    void testRegister_Success() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.getUsername()))
                .thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail()))
                .thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword()))
                .thenReturn("encodedPassword");
        when(userRepository.save(any(User.class)))
                .thenReturn(testUser);

        // Act
        ResponseEntity<MessageResponseDTO> result = authService.register(registerRequest);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("User registered successfully!", result.getBody().getMessage());
        verify(userRepository).existsByUsername(registerRequest.getUsername());
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    /**
     * Test registration with existing username.
     */
    @Test
    void testRegister_UsernameAlreadyExists() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.getUsername()))
                .thenReturn(true);

        // Act
        ResponseEntity<MessageResponseDTO> result = authService.register(registerRequest);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Error: Username already taken!", result.getBody().getMessage());
        verify(userRepository).existsByUsername(registerRequest.getUsername());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Test registration with existing email.
     */
    @Test
    void testRegister_EmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.getUsername()))
                .thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail()))
                .thenReturn(true);

        // Act
        ResponseEntity<MessageResponseDTO> result = authService.register(registerRequest);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Error: Email already in use!", result.getBody().getMessage());
        verify(userRepository).existsByUsername(registerRequest.getUsername());
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Test registration with custom roles.
     */
    @Test
    void testRegister_WithCustomRoles() {
        // Arrange
        Set<UserRole> customRoles = new HashSet<>();
        customRoles.add(UserRole.ADMIN);
        registerRequest.setRoles(customRoles);

        when(userRepository.existsByUsername(registerRequest.getUsername()))
                .thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail()))
                .thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword()))
                .thenReturn("encodedPassword");
        when(userRepository.save(any(User.class)))
                .thenReturn(testUser);

        // Act
        ResponseEntity<MessageResponseDTO> result = authService.register(registerRequest);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(userRepository).save(any(User.class));
    }

    /**
     * Test successful user logout.
     */
    @Test
    void testLogoutUser() {
        // Act
        var result = authService.logoutUser();

        // Assert
        assertNotNull(result);
        assertEquals("jwt", result.getName());
        assertNull(result.getValue());
        assertEquals(0, result.getMaxAge().getSeconds());
    }

    /**
     * Test getting current user details with valid authentication.
     */
    @Test
    void testGetCurrentUserDetails_Success() {
        // Arrange
        UserRegisterResponseDTO expectedResponse = new UserRegisterResponseDTO();
        expectedResponse.setId(userId);
        expectedResponse.setUsername("testuser");
        expectedResponse.setEmail("test@example.com");

        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));
        when(userConverter.registerEntityToResponse(testUser))
                .thenReturn(expectedResponse);

        // Act
        UserRegisterResponseDTO result = authService.getCurrentUserDetails(authentication);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).findByEmail("test@example.com");
        verify(userConverter).registerEntityToResponse(testUser);
    }

    /**
     * Test getting current user details with null authentication.
     */
    @Test
    void testGetCurrentUserDetails_NullAuthentication() {
        // Act
        UserRegisterResponseDTO result = authService.getCurrentUserDetails(null);

        // Assert
        assertNull(result);
    }

    /**
     * Test getting current user details when user not found.
     */
    @Test
    void testGetCurrentUserDetails_UserNotFound() {
        // Arrange
        when(authentication.getName()).thenReturn("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());
        when(userRepository.findByUsername("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> authService.getCurrentUserDetails(authentication));
    }
}