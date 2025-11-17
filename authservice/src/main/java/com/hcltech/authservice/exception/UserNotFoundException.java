package com.hcltech.authservice.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * Exception thrown when a requested user is not found in the system.
 *
 * <p>This exception provides detailed information about the resource
 * that was not found, including the resource name, field name, and field value.
 *
 * <p>Typically thrown during user lookup operations when no user matches
 * the provided criteria (email, username, etc.).
 *
 * @author Boobesh Kumar S
 * @version 1.0
 * @since 2025-11-17
 * @see RuntimeException
 */
@Getter
@Setter
public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final String resourceName;
    private final String field;
    private final String fieldName;

    /**
     * Constructs a new UserNotFoundException with detailed information.
     *
     * @param resourceName the name of the resource that was not found (e.g., "User")
     * @param field the field that was used for lookup (e.g., "email")
     * @param fieldName the value of the field that was used for lookup
     */
    public UserNotFoundException(String resourceName, String field, String fieldName) {
        super(String.format("%s not found with %s : %s", resourceName, field, fieldName));

        this.resourceName = resourceName;
        this.field = field;
        this.fieldName = fieldName;
    }
}