package com.hcltech.authservice.dto;

import java.util.Set;
import java.util.UUID;

import com.hcltech.authservice.entity.UserRole;

import lombok.Data;

/**
 * Data Transfer Object for user login responses.
 * Contains user information and authentication token details upon successful login.
 *
 * <p>This DTO provides all necessary information for the client to maintain
 * the user session and display user-specific content.
 *
 * @author HCL Technologies
 * @version 1.0
 * @since 1.0
 * @see UserRole
 */
@Data
public class UserLoginResponseDTO {
    /**
     * Unique identifier for the user.
     */
    private UUID id;

    /**
     * User's display name or username.
     */
    private String username;

    /**
     * User's email address.
     */
    private String email;

    /**
     * Set of roles assigned to the user for authorization purposes.
     *
     * @see UserRole
     */
    private Set<UserRole> roles;

    /**
     * JWT authentication token for subsequent API requests.
     * This token should be included in the Authorization header.
     */
    private String token;

    /**
     * Expiration time of the JWT token in milliseconds since epoch.
     * Indicates when the token will become invalid.
     */
    private Long expiry;
}