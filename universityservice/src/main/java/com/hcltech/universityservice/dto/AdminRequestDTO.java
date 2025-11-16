package com.hcltech.universityservice.dto;

import java.time.LocalDate;

import com.hcltech.universityservice.entity.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object for admin registration and update requests.
 * <p>
 * This DTO encapsulates all required and optional information for creating
 * or updating an admin account. It includes comprehensive validation constraints
 * to ensure data integrity.
 * </p>
 *
 * @author HCL Tech
 * @version 1.0
 * @since 1.0
 */
@Data
public class AdminRequestDTO {

	/**
	 * Admin's first name (required, max 50 characters).
	 */
	@NotBlank(message = "First name is required")
    @Size(max = 50)
    private String firstName;

	/**
	 * Admin's last name (required, max 50 characters).
	 */
    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    private String lastName;

	/**
	 * Admin's email address (required, must be valid email format).
	 * This will be used as the username for authentication.
	 */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

	/**
	 * Admin's password (required, minimum 8 characters).
	 * Must contain uppercase, lowercase, special character, and digit.
	 */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password(which should contain: uppercase, lowercase, character and a digit) must be at least 8 characters")
    private String password;

	/**
	 * Admin's date of birth (required, must be in the past).
	 */
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

	/**
	 * Admin's phone number (optional, must match E.164 format).
	 */
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number")
	private String phoneNumber;

	/**
	 * Admin's gender (required).
	 */
    @NotNull(message = "Gender is required")
    private Gender gender;

	/**
	 * Admin's nationality (optional, max 50 characters).
	 */
    @Size(max = 50)
	private String nationality;
}
