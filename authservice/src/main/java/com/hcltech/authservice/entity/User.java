package com.hcltech.authservice.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Entity class representing a User in the system.
 *
 * <p>This JPA entity maps to the "users" table in the database and contains
 * all user-related information including authentication credentials and roles.
 *
 * <p>The entity enforces uniqueness constraints on both username and email fields
 * to prevent duplicate user accounts.
 *
 * <p>Key features:
 * <ul>
 *   <li>UUID-based primary key for distributed systems compatibility</li>
 *   <li>Unique username and email constraints</li>
 *   <li>Role-based access control using UserRole enum</li>
 *   <li>Password validation with security requirements</li>
 * </ul>
 *
 * @author Boobesh Kumar S
 * @version 1.0
 * @since 2025-11-17
 * @see UserRole
 * @see jakarta.persistence.Entity
 */
@Entity
@Data
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "email") })
public class User {
    /**
     * Unique identifier for the user.
     * Generated automatically using UUID strategy for better distribution compatibility.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * User's unique username for identification and display.
     * Must be unique across the system and cannot be blank.
     */
    @NotBlank(message = "Username is required")
    @Size(max = 50)
    @Column(nullable = false)
    private String username;

    /**
     * User's unique email address for communication and authentication.
     * Must be unique across the system and in valid email format.
     */
    @NotBlank(message = "Email is required")
    @Size(max = 50)
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * User's encrypted password for authentication.
     * Must meet security requirements: minimum 8 characters containing
     * uppercase, lowercase, special character, and digit.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password(which should contain: uppercase, lowercase, character and a digit) must be at least 8 characters")
    private String password;

    /**
     * Set of roles assigned to the user for authorization purposes.
     * Defaults to an empty set and uses string-based enum storage.
     *
     * @see UserRole
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles = new HashSet<>();
}