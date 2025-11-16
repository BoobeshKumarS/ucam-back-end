//package com.hcltech.authservice.controller;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import java.util.HashSet;
//import java.util.Set;
//import java.util.UUID;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseCookie;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//
//import com.hcltech.authservice.dto.MessageResponseDTO;
//import com.hcltech.authservice.dto.UserLoginRequestDTO;
//import com.hcltech.authservice.dto.UserLoginResponseDTO;
//import com.hcltech.authservice.dto.UserRegisterRequestDTO;
//import com.hcltech.authservice.dto.UserRegisterResponseDTO;
//import com.hcltech.authservice.entity.UserRole;
//import com.hcltech.authservice.service.AuthService;
//
//@ExtendWith(MockitoExtension.class)
//class AuthServiceControllerTest {
//
//    @Mock
//    private AuthService authService;
//
//    @Mock
//    private Authentication authentication;
//
//    @InjectMocks
//    private AuthController authController;
//
//    private UserLoginRequestDTO loginRequest;
//    private UserRegisterRequestDTO registerRequest;
//    private UserLoginResponseDTO loginResponse;
//    private UserRegisterResponseDTO userResponse;
//
//    @BeforeEach
//    void setUp() {
//        loginRequest = new UserLoginRequestDTO();
//        loginRequest.setEmail("test@example.com");
//        loginRequest.setPassword("password");
//
//        registerRequest = new UserRegisterRequestDTO();
//        registerRequest.setUsername("testuser");
//        registerRequest.setEmail("test@example.com");
//        registerRequest.setPassword("password");
//
//        loginResponse = new UserLoginResponseDTO();
//        loginResponse.setId(UUID.randomUUID());
//        loginResponse.setUsername("testuser");
//        loginResponse.setEmail("test@example.com");
//        loginResponse.setToken("jwt-token");
//        loginResponse.setExpiry(24L);
//
//        userResponse = new UserRegisterResponseDTO();
//        userResponse.setId(UUID.randomUUID());
//        userResponse.setUsername("testuser");
//        userResponse.setEmail("test@example.com");
//        
//        Set<UserRole> roles = new HashSet<>();
//        roles.add(UserRole.STUDENT);
//        userResponse.setRoles(roles);
//    }
//
//    @Test
//    void test_ShouldReturnUserDetails() {
//        // Arrange
//        when(authService.getCurrentUserDetails(authentication)).thenReturn(userResponse);
//
//        // Act
//        ResponseEntity<?> result = authController.getUserDetails(authentication);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//        assertEquals(userResponse, result.getBody());
//        verify(authService).getCurrentUserDetails(authentication);
//    }
//
//    @Test
//    void authenticateUser_Success() {
//        // Arrange
//        when(authService.login(loginRequest)).thenReturn(loginResponse);
//
//        // Act
//        ResponseEntity<?> result = authController.authenticateUser(loginRequest);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//        assertEquals(loginResponse, result.getBody());
//        verify(authService).login(loginRequest);
//    }
//
//    @Test
//    void registerUser_Success() {
//        // Arrange
//        MessageResponseDTO messageResponse = new MessageResponseDTO("User registered successfully!");
//        
//        when(authService.register(registerRequest))
//                .thenAnswer(invocation -> ResponseEntity.ok(messageResponse));
//
//        // Act
//        ResponseEntity<?> result = authController.registerUser(registerRequest);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//        assertEquals(messageResponse, result.getBody());
//        verify(authService).register(registerRequest);
//    }
//
//    @Test
//    void currentUserName_Success() {
//        // Arrange
//        when(authentication.getName()).thenReturn("testuser");
//
//        // Act
//        String result = authController.currentUserName(authentication);
//
//        // Assert
//        assertEquals("testuser", result);
//    }
//
//    @Test
//    void currentUserName_NullAuthentication() {
//        // Act
//        String result = authController.currentUserName(null);
//
//        // Assert
//        assertEquals("", result);
//    }
//
//    @Test
//    void getUserDetails_Success() {
//        // Arrange
//        when(authService.getCurrentUserDetails(authentication)).thenReturn(userResponse);
//
//        // Act
//        ResponseEntity<?> result = authController.getUserDetails(authentication);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//        assertEquals(userResponse, result.getBody());
//        verify(authService).getCurrentUserDetails(authentication);
//    }
//
//    @Test
//    void signoutUser_Success() {
//        // Arrange
//        ResponseCookie cookie = ResponseCookie.from("jwt", null)
//                .path("/")
//                .httpOnly(true)
//                .maxAge(0)
//                .build();
//        when(authService.logoutUser()).thenReturn(cookie);
//
//        // Act
//        ResponseEntity<?> result = authController.signoutUser();
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//        assertTrue(result.getHeaders().containsKey(HttpHeaders.SET_COOKIE));
//        assertEquals(cookie.toString(), result.getHeaders().getFirst(HttpHeaders.SET_COOKIE));
//        
//        MessageResponseDTO body = (MessageResponseDTO) result.getBody();
//        assertEquals("You've been signed out!", body.getMessage());
//        
//        verify(authService).logoutUser();
//    }
//}