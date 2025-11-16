package com.hcltech.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object for user login requests.
 * Contains the credentials required for user authentication.
 *
 * <p>This DTO includes validation constraints to ensure data integrity:
 * <ul>
 *   <li>Email must be valid and not blank</li>
 *   <li>Password must meet security requirements</li>
 * </ul>
 *
 * @author HCL Technologies
 * @version 1.0
 * @since 1.0
 * @see jakarta.validation.constraints
 */
@Data
public class UserLoginRequestDTO {
    /**
     * User's email address used for authentication.
     * Must be a valid email format and cannot be empty.
     *
     * @see Email
     * @see NotBlank
     */
    @NotBlank(message = "Email is required")
    @Size(max = 50)
    @Email
    private String email;

    /**
     * User's password for authentication.
     * Must be at least 8 characters long and contain:
     * uppercase, lowercase, special character, and a digit.
     *
     * @see NotBlank
     * @see Size
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password(which should contain: uppercase, lowercase, character and a digit) must be at least 8 characters")
    private String password;
}