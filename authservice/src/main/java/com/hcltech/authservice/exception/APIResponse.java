package com.hcltech.authservice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard API response wrapper for consistent error and success responses.
 *
 * <p>This class provides a uniform structure for all API responses,
 * including both successful operations and error conditions.
 *
 * <p>Used by global exception handlers and controller methods to ensure
 * consistent response format across the entire application.
 *
 * @author Boobesh Kumar S
 * @version 1.0
 * @since 2025-11-17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class APIResponse {
    /**
     * Human-readable message describing the operation result or error.
     */
    private String message;

    /**
     * Indicates the success status of the operation.
     * true for successful operations, false for errors.
     */
    private boolean status;
}