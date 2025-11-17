package com.hcltech.authservice.entity;

/**
 * Enumeration representing user roles in the system.
 *
 * <p>Defines the available roles that can be assigned to users for
 * authorization and access control purposes.
 *
 * <p>Available roles:
 * <ul>
 *   <li>STUDENT - Standard user role with basic access privileges</li>
 *   <li>ADMIN - Administrative role with elevated access privileges</li>
 * </ul>
 *
 * <p>Roles are stored as strings in the database and used by Spring Security
 * for method-level and URL-based authorization.
 *
 * @author Boobesh Kumar S
 * @version 1.0
 * @since 2025-11-17
 * @see org.springframework.security.access.prepost.PreAuthorize
 */
public enum UserRole {
    /**
     * Standard user role with basic system access privileges.
     * Typically used for regular application users.
     */
    STUDENT,

    /**
     * Administrative role with elevated system access privileges.
     * Typically used for system administrators with management capabilities.
     */
    ADMIN
}