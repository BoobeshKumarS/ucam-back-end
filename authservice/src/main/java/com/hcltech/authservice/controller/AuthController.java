package com.hcltech.authservice.controller;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.hcltech.authservice.dto.MessageResponseDTO;
import com.hcltech.authservice.dto.UserLoginRequestDTO;
import com.hcltech.authservice.dto.UserLoginResponseDTO;
import com.hcltech.authservice.dto.UserRegisterRequestDTO;
import com.hcltech.authservice.dto.UserRegisterResponseDTO;
import com.hcltech.authservice.service.AuthService;

/**
 * REST Controller for handling authentication-related operations.
 * Provides endpoints for user registration, login, logout, and user information retrieval.
 *
 * <p>All endpoints are prefixed with "/api/auth" and include proper security authorization
 * where required. The controller uses JWT tokens for authentication stored in HttpOnly cookies.
 *
 * <p>Key features:
 * <ul>
 *   <li>User registration with role-based access</li>
 *   <li>User authentication with JWT token generation</li>
 *   <li>Secure logout with cookie clearance</li>
 *   <li>User information retrieval for authenticated users</li>
 *   <li>Role-based authorization using Spring Security annotations</li>
 * </ul>
 *
 * @author Boobesh Kumar S
 * @version 1.0
 * @since 2025-11-17
 * @see AuthService
 * @see PreAuthorize
 * @see Authentication
 */
//@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    /**
     * Authenticates a user and generates JWT token upon successful login.
     * The token is stored in an HttpOnly cookie for secure client-side handling.
     *
     * <p>This endpoint validates the user credentials and returns user information
     * along with authentication token details.
     *
     * @param loginRequest the user login credentials containing email and password
     * @return ResponseEntity containing UserLoginResponseDTO with:
     *         <ul>
     *           <li>User details (ID, username, email, roles)</li>
     *           <li>JWT token information</li>
     *           <li>Token expiration time</li>
     *         </ul>
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are invalid
     * @see UserLoginRequestDTO
     * @see UserLoginResponseDTO
     */
    // ✅ LOGIN → Generates JWT, stores in HttpOnly cookie
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> authenticateUser(@Valid @RequestBody UserLoginRequestDTO loginRequest) {
        logger.debug("Login Request Received ---> {}", loginRequest);
        UserLoginResponseDTO result = authService.login(loginRequest);

        // result contains: ResponseCookie + UserInfoResponse
        return ResponseEntity.ok().body(result);
    }

    /**
     * Registers a new user in the system.
     *
     * <p>Validates the registration request and creates a new user account
     * with the provided information. Supports role assignment during registration.
     *
     * @param registerRequest the user registration data containing username, email, password, and roles
     * @return ResponseEntity with MessageResponseDTO indicating registration success or failure
     * @throws org.springframework.dao.DataIntegrityViolationException if username or email already exists
     * @see UserRegisterRequestDTO
     * @see MessageResponseDTO
     */
    // ✅ SIGNUP → Registers new user
    @PostMapping("/register")
    public ResponseEntity<MessageResponseDTO> registerUser(@Valid @RequestBody UserRegisterRequestDTO registerRequest) {
        logger.debug("Signup Request ---> {}", registerRequest);
        return authService.register(registerRequest);
    }

    /**
     * Retrieves the username of the currently authenticated user.
     *
     * <p>This endpoint requires the user to have either 'STUDENT' or 'ADMIN' role.
     * Returns an empty string if no user is authenticated.
     *
     * @param authentication the Spring Security authentication object containing user details
     * @return the username of the authenticated user, or empty string if not authenticated
     * @see Authentication
     */
    // ✅ CURRENT USERNAME
    @GetMapping("/username")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public String currentUserName(Authentication authentication) {
        if (authentication != null) {
            logger.debug("Authenticated username ---> {}", authentication.getName());
            return authentication.getName();
        } else {
            return "";
        }
    }

    /**
     * Retrieves detailed information about the currently authenticated user.
     *
     * <p>This endpoint requires the user to have either 'STUDENT' or 'ADMIN' role.
     * Returns comprehensive user details including roles, email, and user ID.
     *
     * @param authentication the Spring Security authentication object containing user details
     * @return ResponseEntity containing UserRegisterResponseDTO with user details
     * @see Authentication
     * @see UserRegisterResponseDTO
     */
    // ✅ CURRENT USER DETAILS (with roles, email, etc.)
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<UserRegisterResponseDTO> getUserDetails(Authentication authentication) {
        logger.debug("Authenticated user details ---> {}", authentication);
        return ResponseEntity.ok(authService.getCurrentUserDetails(authentication));
    }

    /**
     * Logs out the currently authenticated user by clearing the JWT cookie.
     *
     * <p>This endpoint requires the user to be authenticated and invalidates
     * the session by clearing the authentication cookie.
     *
     * @return ResponseEntity with MessageResponseDTO indicating successful logout
     *         and SET-COOKIE header to clear the authentication cookie
     * @see ResponseCookie
     * @see MessageResponseDTO
     */
    // ✅ LOGOUT → Clears the JWT cookie
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponseDTO> signoutUser() {
        ResponseCookie cookie = authService.logoutUser();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponseDTO("You've been signed out!"));
    }
}