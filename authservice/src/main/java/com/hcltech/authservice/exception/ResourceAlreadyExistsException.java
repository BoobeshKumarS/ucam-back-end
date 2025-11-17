package com.hcltech.authservice.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * Exception thrown when attempting to create a resource that already exists.
 *
 * <p>This exception is typically thrown during user registration when
 * a username or email address is already in use by another user.
 *
 * <p>Provides detailed information about the conflicting resource
 * for better error reporting and client handling.
 *
 * @author Boobesh Kumar S
 * @version 1.0
 * @since 2025-11-17
 * @see RuntimeException
 */
@Getter
@Setter
public class ResourceAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String resourceName;
    private final String field;
    private final String fieldName;
    private final Long fieldId;

    /**
     * Constructs exception with string field value.
     *
     * @param resourceName the name of the resource (e.g., "User")
     * @param field the field that caused the conflict (e.g., "email")
     * @param fieldName the value of the field that caused the conflict
     */
    public ResourceAlreadyExistsException(String resourceName, String field, String fieldName) {
        super(String.format("%s already exists with %s : %s", resourceName, field, fieldName));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldName = fieldName;
        this.fieldId = null;
    }
}