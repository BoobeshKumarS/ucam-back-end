package com.hcltech.authservice.service;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.hcltech.authservice.dto.MessageResponseDTO;
import com.hcltech.authservice.dto.UserLoginRequestDTO;
import com.hcltech.authservice.dto.UserLoginResponseDTO;
import com.hcltech.authservice.dto.UserRegisterRequestDTO;
import com.hcltech.authservice.dto.UserRegisterResponseDTO;

/**
 * Service interface for authentication and user management operations.
 *
 * <p>Defines the contract for all authentication-related business logic including
 * user registration, login, logout, and user information retrieval.
 *
 * <p>Implementations of this interface handle the core business logic for
 * user authentication and authorization processes.
 *
 * @author Boobesh Kumar S
 * @version 1.0
 * @since 2025-11-17
 * @see com.hcltech.authservice.service.impl.AuthServiceImpl
 * @see org.springframework.stereotype.Service
 */
public interface AuthService {

    /**
     * Authenticates a user and generates login response with JWT token.
     *
     * @param loginRequest the user credentials for authentication
     * @return UserLoginResponseDTO containing user details and JWT token
     * @throws com.hcltech.authservice.exception.UserNotFoundException if user not found
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are invalid
     * @see UserLoginRequestDTO
     * @see UserLoginResponseDTO
     */
    UserLoginResponseDTO login(UserLoginRequestDTO loginRequest);

    /**
     * Registers a new user in the system.
     *
     * <p>Validates the registration data, checks for existing users with same
     * username or email, encodes the password, and saves the new user.
     *
     * @param registerRequest the user registration data
     * @return ResponseEntity with success message or error if registration fails
     * @see UserRegisterRequestDTO
     * @see MessageResponseDTO
     */
    ResponseEntity<MessageResponseDTO> register(UserRegisterRequestDTO registerRequest);

    /**
     * Creates a response cookie to clear the JWT token during logout.
     *
     * @return ResponseCookie configured to clear the authentication cookie
     * @see ResponseCookie
     */
    ResponseCookie logoutUser();

    /**
     * Retrieves detailed information about the currently authenticated user.
     *
     * @param authentication the Spring Security authentication object
     * @return UserRegisterResponseDTO with user details, or null if not authenticated
     * @see Authentication
     * @see UserRegisterResponseDTO
     */
    UserRegisterResponseDTO getCurrentUserDetails(Authentication authentication);
}