package com.hcltech.authservice.util;

import org.springframework.stereotype.Component;

import com.hcltech.authservice.dto.UserLoginResponseDTO;
import com.hcltech.authservice.dto.UserRegisterResponseDTO;
import com.hcltech.authservice.entity.User;

/**
 * Utility component for converting between User entities and DTOs.
 *
 * <p>This converter handles the transformation of User entity objects
 * to various response DTOs used in the authentication service API responses.
 *
 * <p>Provides separate conversion methods for different use cases:
 * <ul>
 *   <li>Registration responses (without sensitive data)</li>
 *   <li>Login responses (with authentication tokens)</li>
 * </ul>
 *
 * @author Boobesh Kumar S
 * @version 1.0
 * @since 2025-11-17
 * @see User
 * @see UserRegisterResponseDTO
 * @see UserLoginResponseDTO
 */
@Component
public class UserConverter {

    /**
     * Converts User entity to UserRegisterResponseDTO for registration operations.
     *
     * <p>Includes basic user information without authentication tokens:
     * <ul>
     *   <li>User ID</li>
     *   <li>Username</li>
     *   <li>Email address</li>
     *   <li>Assigned roles</li>
     * </ul>
     *
     * @param user the User entity to convert
     * @return UserRegisterResponseDTO with user registration data
     */
    public UserRegisterResponseDTO registerEntityToResponse(User user) {
        UserRegisterResponseDTO registerResponse = new UserRegisterResponseDTO();
        registerResponse.setId(user.getId());
        registerResponse.setUsername(user.getUsername());
        registerResponse.setEmail(user.getEmail());
        registerResponse.setRoles(user.getRoles());
        return registerResponse;
    }

    /**
     * Converts User entity to UserLoginResponseDTO for authentication operations.
     *
     * <p>Includes user information along with authentication details:
     * <ul>
     *   <li>User ID</li>
     *   <li>Username</li>
     *   <li>Email address</li>
     *   <li>Assigned roles</li>
     *   <li>JWT token and expiration (to be set separately)</li>
     * </ul>
     *
     * @param user the User entity to convert
     * @return UserLoginResponseDTO with user login data
     */
    public UserLoginResponseDTO loginEntityToResponse(User user) {
        UserLoginResponseDTO loginResponse = new UserLoginResponseDTO();
        loginResponse.setId(user.getId());
        loginResponse.setUsername(user.getUsername());
        loginResponse.setEmail(user.getEmail());
        loginResponse.setRoles(user.getRoles());
        return loginResponse;
    }
}