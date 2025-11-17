package com.hcltech.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object for sending simple message responses from the API.
 *
 * <p>This DTO is commonly used for operations that don't require complex
 * response data but need to communicate success or status messages to the client.
 *
 * <p>Example usage:
 * <pre>
 * {@code
 * // Success response
 * new MessageResponseDTO("User registered successfully");
 *
 * // Error response
 * new MessageResponseDTO("Invalid credentials provided");
 * }
 * </pre>
 *
 * @author Boobesh Kumar S
 * @version 1.0
 * @since 2025-11-17
 */
@Data
@AllArgsConstructor
public class MessageResponseDTO {
    /**
     * The message content describing the operation result or status.
     * This can be a success message, error description, or informational text.
     */
    private String message;
}