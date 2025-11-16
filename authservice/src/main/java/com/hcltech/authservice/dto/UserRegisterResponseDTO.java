package com.hcltech.authservice.dto;

import java.util.Set;
import java.util.UUID;

import com.hcltech.authservice.entity.UserRole;

import lombok.Data;

/**
 * Data Transfer Object for user registration responses.
 * Contains user information returned after successful registration.
 *
 * <p>This DTO provides the created user's details without sensitive
 * information like passwords.
 *
 * @author HCL Technologies
 * @version 1.0
 * @since 1.0
 * @see UserRole
 */
@Data
public class UserRegisterResponseDTO {
    /**
     * Unique identifier for the newly registered user.
     */
    private UUID id;

    /**
     * User's chosen username.
     */
    private String username;

    /**
     * User's registered email address.
     */
    private String email;

    /**
     * Set of roles assigned to the user.
     *
     * @see UserRole
     */
    private Set<UserRole> roles;
}