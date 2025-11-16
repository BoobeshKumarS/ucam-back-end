package com.hcltech.studentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for student response data.
 *
 * This DTO is used to transfer student information from the service layer
 * to the client. It contains all student details including metadata fields
 * like creation and update timestamps. The password field is intentionally
 * excluded for security purposes.
 *
 * This class uses Lombok annotations for automatic generation of getters,
 * setters, constructors, and builder pattern implementation.
 *
 * @author HCL Technologies
 * @version 1.0
 * @since 1.0
 */
@Builder
@Data
@AllArgsConstructor
public class StudentResponseDTO {

    /**
     * Unique identifier for the student.
     * Generated as UUID when the student is created.
     */
    private UUID id;

    /**
     * The student's first name.
     */
    private String firstName;

    /**
     * The student's last name.
     */
    private String lastName;

    /**
     * The student's email address.
     * Used as unique identifier for authentication.
     */
    private String email;

    /**
     * The student's date of birth.
     */
    private LocalDate dateOfBirth;

    /**
     * The student's gender as display name string.
     * Converted from Gender enum to human-readable format.
     */
    private String gender;

    /**
     * The student's phone number in international format.
     */
    private String phoneNumber;

    /**
     * The student's nationality.
     */
    private String nationality;

    /**
     * The student's residential address.
     */
    private String address;

    /**
     * Timestamp when the student record was created.
     * Automatically set by the database on record creation.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the student record was last updated.
     * Automatically updated by the database on record modification.
     */
    private LocalDateTime updatedAt;
}