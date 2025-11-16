//package com.hcltech.authservice.service.impl;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//import java.util.HashSet;
//import java.util.Optional;
//import java.util.Set;
//import java.util.UUID;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseCookie;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import com.hcltech.authservice.dto.MessageResponseDTO;
//import com.hcltech.authservice.dto.UserLoginRequestDTO;
//import com.hcltech.authservice.dto.UserLoginResponseDTO;
//import com.hcltech.authservice.dto.UserRegisterRequestDTO;
//import com.hcltech.authservice.dto.UserRegisterResponseDTO;
//import com.hcltech.authservice.entity.User;
//import com.hcltech.authservice.entity.UserRole;
//import com.hcltech.authservice.exception.UserNotFoundException;
//import com.hcltech.authservice.repository.UserRepository;
//import com.hcltech.authservice.security.MyUserDetailsService;
//import com.hcltech.authservice.util.JwtUtil;
//import com.hcltech.authservice.util.UserConverter;
//
//@ExtendWith(MockitoExtension.class)
//class AuthServiceImplTest {
//
//    @Mock
//    private AuthenticationManager authenticationManager;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @Mock
//    private UserConverter userConverter;
//
//    @Mock
//    private MyUserDetailsService userDetailsService;
//
//    @Mock
//    private JwtUtil jwtUtil;
//
//    @Mock
//    private Authentication authentication;
//
//    @InjectMocks
//    private AuthServiceImpl authService;
//
//    private User testUser;
//    private UserLoginRequestDTO loginRequest;
//    private UserRegisterRequestDTO registerRequest;
//
//    @BeforeEach
//    void setUp() {
//        Set<UserRole> roles = new HashSet<>();
//        roles.add(UserRole.STUDENT);
//
//        testUser = new User();
//        testUser.setId(UUID.randomUUID());
//        testUser.setUsername("testuser");
//        testUser.setEmail("test@example.com");
//        testUser.setPassword("encodedPassword");
//        testUser.setRoles(roles);
//
//        loginRequest = new UserLoginRequestDTO();
//        loginRequest.setEmail("test@example.com");
//        loginRequest.setPassword("password");
//
//        registerRequest = new UserRegisterRequestDTO();
//        registerRequest.setUsername("newuser");
//        registerRequest.setEmail("newuser@example.com");
//        registerRequest.setPassword("password");
//    }
//
//    @Test
//    void login_Success() {
//        // Arrange
//        UserDetails userDetails = mock(UserDetails.class);
//        UserLoginResponseDTO expectedResponse = new UserLoginResponseDTO();
//        expectedResponse.setId(UUID.randomUUID());
//        expectedResponse.setUsername("testuser");
//        expectedResponse.setEmail("test@example.com");
//        expectedResponse.setToken("jwt-token");
//        expectedResponse.setExpiry(24L);
//
//        when(userRepository.findByEmail(loginRequest.getEmail()))
//                .thenReturn(Optional.of(testUser));
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenReturn(authentication);
//        when(userDetailsService.loadUserByUsername(testUser.getEmail()))
//                .thenReturn(userDetails);
//        when(jwtUtil.generateToken(null, "test@example.com", Set.of(UserRole.STUDENT)))
//                .thenReturn("jwt-token");
//        when(jwtUtil.getTokenValidityInHours("jwt-token"))
//                .thenReturn(24L);
//        when(userConverter.loginEntityToResponse(testUser))
//                .thenReturn(expectedResponse);
//
//        // Act
//        UserLoginResponseDTO result = authService.login(loginRequest);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("jwt-token", result.getToken());
//        assertEquals(24L, result.getExpiry());
//        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
//        verify(userRepository).findByEmail(loginRequest.getEmail());
//    }
//
//    @Test
//    void login_UserNotFound() {
//        // Arrange
//        when(userRepository.findByEmail(loginRequest.getEmail()))
//                .thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(UserNotFoundException.class, () -> authService.login(loginRequest));
//        verify(userRepository).findByEmail(loginRequest.getEmail());
//        verify(authenticationManager, never()).authenticate(any());
//    }
//
//    @Test
//    void login_AuthenticationFailed() {
//        // Arrange
//        when(userRepository.findByEmail(loginRequest.getEmail()))
//                .thenReturn(Optional.of(testUser));
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenThrow(new BadCredentialsException("Invalid credentials"));
//
//        // Act & Assert
//        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
//        verify(userRepository).findByEmail(loginRequest.getEmail());
//        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
//    }
//
//    @Test
//    void register_Success() {
//        // Arrange
//        when(userRepository.existsByUsername(registerRequest.getUsername()))
//                .thenReturn(false);
//        when(userRepository.existsByEmail(registerRequest.getEmail()))
//                .thenReturn(false);
//        when(passwordEncoder.encode(registerRequest.getPassword()))
//                .thenReturn("encodedPassword");
//        when(userRepository.save(any(User.class)))
//                .thenReturn(testUser);
//
//        // Act
//        ResponseEntity<MessageResponseDTO> result = authService.register(registerRequest);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//        assertEquals("User registered successfully!", result.getBody().getMessage());
//        verify(userRepository).existsByUsername(registerRequest.getUsername());
//        verify(userRepository).existsByEmail(registerRequest.getEmail());
//        verify(passwordEncoder).encode(registerRequest.getPassword());
//        verify(userRepository).save(any(User.class));
//    }
//
//    @Test
//    void register_UsernameAlreadyExists() {
//        // Arrange
//        when(userRepository.existsByUsername(registerRequest.getUsername()))
//                .thenReturn(true);
//
//        // Act
//        ResponseEntity<MessageResponseDTO> result = authService.register(registerRequest);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
//        assertEquals("Error: Username already taken!", result.getBody().getMessage());
//        verify(userRepository).existsByUsername(registerRequest.getUsername());
//        verify(userRepository, never()).existsByEmail(anyString());
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    void register_EmailAlreadyExists() {
//        // Arrange
//        when(userRepository.existsByUsername(registerRequest.getUsername()))
//                .thenReturn(false);
//        when(userRepository.existsByEmail(registerRequest.getEmail()))
//                .thenReturn(true);
//
//        // Act
//        ResponseEntity<MessageResponseDTO> result = authService.register(registerRequest);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
//        assertEquals("Error: Email already in use!", result.getBody().getMessage());
//        verify(userRepository).existsByUsername(registerRequest.getUsername());
//        verify(userRepository).existsByEmail(registerRequest.getEmail());
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    void register_WithRoles() {
//        // Arrange
//        Set<UserRole> roles = new HashSet<>();
//        roles.add(UserRole.ADMIN);
//        registerRequest.setRoles(roles);
//
//        when(userRepository.existsByUsername(registerRequest.getUsername()))
//                .thenReturn(false);
//        when(userRepository.existsByEmail(registerRequest.getEmail()))
//                .thenReturn(false);
//        when(passwordEncoder.encode(registerRequest.getPassword()))
//                .thenReturn("encodedPassword");
//        when(userRepository.save(any(User.class)))
//                .thenReturn(testUser);
//
//        // Act
//        ResponseEntity<MessageResponseDTO> result = authService.register(registerRequest);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//        verify(userRepository).save(any(User.class));
//    }
//
//    @Test
//    void logoutUser_Success() {
//        // Act
//        ResponseCookie result = authService.logoutUser();
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("jwt", result.getName());
//        assertEquals("", result.getValue());
//        assertEquals(0, result.getMaxAge().getSeconds());
//        assertTrue(result.isHttpOnly());
//    }
//
//    @Test
//    void getCurrentUserDetails_SuccessWithEmail() {
//        // Arrange
//        UserRegisterResponseDTO expectedResponse = new UserRegisterResponseDTO();
//        expectedResponse.setId(UUID.randomUUID());
//        expectedResponse.setUsername("testuser");
//        expectedResponse.setEmail("test@example.com");
//
//        when(authentication.getName()).thenReturn("test@example.com");
//        when(userRepository.findByEmail("test@example.com"))
//                .thenReturn(Optional.of(testUser));
//        when(userConverter.registerEntityToResponse(testUser))
//                .thenReturn(expectedResponse);
//
//        // Act
//        UserRegisterResponseDTO result = authService.getCurrentUserDetails(authentication);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("testuser", result.getUsername());
//        assertEquals("test@example.com", result.getEmail());
//        verify(userRepository).findByEmail("test@example.com");
//        verify(userRepository, never()).findByUsername(anyString());
//    }
//
//    @Test
//    void getCurrentUserDetails_SuccessWithUsername() {
//        // Arrange
//        UserRegisterResponseDTO expectedResponse = new UserRegisterResponseDTO();
//        expectedResponse.setId(UUID.randomUUID());
//        expectedResponse.setUsername("testuser");
//        expectedResponse.setEmail("test@example.com");
//
//        when(authentication.getName()).thenReturn("testuser");
//        when(userRepository.findByEmail("testuser"))
//                .thenReturn(Optional.empty());
//        when(userRepository.findByUsername("testuser"))
//                .thenReturn(Optional.of(testUser));
//        when(userConverter.registerEntityToResponse(testUser))
//                .thenReturn(expectedResponse);
//
//        // Act
//        UserRegisterResponseDTO result = authService.getCurrentUserDetails(authentication);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("testuser", result.getUsername());
//        verify(userRepository).findByEmail("testuser");
//        verify(userRepository).findByUsername("testuser");
//    }
//
//    @Test
//    void getCurrentUserDetails_UserNotFound() {
//        // Arrange
//        when(authentication.getName()).thenReturn("nonexistent");
//        when(userRepository.findByEmail("nonexistent"))
//                .thenReturn(Optional.empty());
//        when(userRepository.findByUsername("nonexistent"))
//                .thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(RuntimeException.class, () -> authService.getCurrentUserDetails(authentication));
//    }
//
//    @Test
//    void getCurrentUserDetails_NullAuthentication() {
//        // Act
//        UserRegisterResponseDTO result = authService.getCurrentUserDetails(null);
//
//        // Assert
//        assertNull(result);
//    }
//}