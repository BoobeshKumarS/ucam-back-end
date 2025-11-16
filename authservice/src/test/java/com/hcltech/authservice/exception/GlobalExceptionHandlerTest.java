package com.hcltech.authservice.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MyGlobalExceptionHandler class.
 *
 * @author HCL Technologies
 * @version 1.0
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class MyGlobalExceptionHandlerTest {

    @InjectMocks
    private MyGlobalExceptionHandler exceptionHandler;

    /**
     * Test handling UserNotFoundException.
     */
    @Test
    void testHandleUserNotFoundException() {
        // Arrange
        UserNotFoundException exception = new UserNotFoundException("User", "email", "test@example.com");

        // Act
        ResponseEntity<APIResponse> response = exceptionHandler.myUserNotFoundException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not found with email : test@example.com", response.getBody().getMessage());
        assertFalse(response.getBody().isStatus());
    }

    /**
     * Test handling ResourceAlreadyExistsException.
     */
    @Test
    void testHandleResourceAlreadyExistsException() {
        // Arrange
        ResourceAlreadyExistsException exception =
                new ResourceAlreadyExistsException("User", "email", "test@example.com");

        // Act
        ResponseEntity<APIResponse> response = exceptionHandler.myResourceAlreadyExistsException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User already exists with email : test@example.com", response.getBody().getMessage());
        assertFalse(response.getBody().isStatus());
    }
}