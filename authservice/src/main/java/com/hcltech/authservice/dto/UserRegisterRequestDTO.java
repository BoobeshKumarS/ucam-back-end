package com.hcltech.authservice.dto;

import java.util.HashSet;
import java.util.Set;

import com.hcltech.authservice.entity.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object for user registration requests.
 * Contains all information required to create a new user account.
 *
 * <p>This DTO includes comprehensive validation constraints and supports
 * role assignment during user registration.
 *
 * @author HCL Technologies
 * @version 1.0
 * @since 1.0
 * @see UserRole
 * @see jakarta.validation.constraints
 */
@Data
public class UserRegisterRequestDTO {
    /**
     * User's chosen username.
     * Must be unique and not blank.
     */
    @NotBlank(message = "Username is required")
    @Size(max = 50)
    private String username;

    /**
     * User's email address.
     * Must be unique, valid format, and not blank.
     */
    @NotBlank(message = "Email is required")
    @Size(max = 50)
    @Email
    private String email;

    /**
     * User's password.
     * Must be at least 8 characters long and contain:
     * uppercase, lowercase, special character, and a digit.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password(which should contain: uppercase, lowercase, character and a digit) must be at least 8 characters")
    private String password;

    /**
     * Set of roles to be assigned to the user.
     * Defaults to an empty set if not provided.
     *
     * @see UserRole
     */
    private Set<UserRole> roles = new HashSet<>();
}