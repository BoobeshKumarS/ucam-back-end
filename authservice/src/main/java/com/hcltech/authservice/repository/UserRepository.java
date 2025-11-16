package com.hcltech.authservice.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcltech.authservice.entity.User;

/**
 * Repository interface for User entity CRUD operations.
 *
 * <p>Extends JpaRepository to provide standard data access methods
 * and includes custom query methods for user-specific operations.
 *
 * <p>This repository handles all database interactions for the User entity
 * including finding users by various identifiers and checking for existence.
 *
 * @author HCL Technologies
 * @version 1.0
 * @since 1.0
 * @see User
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds a user by email address.
     *
     * @param identifier the email address to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String identifier);

    /**
     * Finds a user by username.
     *
     * @param identifier the username to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByUsername(String identifier);

    /**
     * Checks if a user exists with the given username.
     *
     * @param username the username to check
     * @return true if a user exists with the given username, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user exists with the given email address.
     *
     * @param email the email address to check
     * @return true if a user exists with the given email, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Finds a user by email address (non-optional version).
     *
     * @param email the email address to search for
     * @return the User entity if found
     * @throws jakarta.persistence.EntityNotFoundException if user not found
     */
    User findUserByEmail(String email);
}