package com.hcltech.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the authentication service.
 *
 * <p>This class provides centralized exception handling across all controllers
 * and ensures consistent error responses for different exception types.
 *
 * <p>Handled exceptions:
 * <ul>
 *   <li>UserNotFoundException - Returns 404 NOT_FOUND</li>
 *   <li>ResourceAlreadyExistsException - Returns 409 CONFLICT</li>
 * </ul>
 *
 * @author HCL Technologies
 * @version 1.0
 * @since 1.0
 * @see RestControllerAdvice
 * @see ExceptionHandler
 */
@RestControllerAdvice
public class MyGlobalExceptionHandler {

    /**
     * Handles UserNotFoundException and returns appropriate HTTP response.
     *
     * @param ex the UserNotFoundException instance
     * @return ResponseEntity with APIResponse and HTTP 404 status
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<APIResponse> myUserNotFoundException(UserNotFoundException ex){
        String message = ex.getMessage();
        APIResponse apiResponse = new APIResponse(message,false);
        return new ResponseEntity<>(apiResponse,HttpStatus.NOT_FOUND);
    }

    /**
     * Handles ResourceAlreadyExistsException and returns appropriate HTTP response.
     *
     * @param ex the ResourceAlreadyExistsException instance
     * @return ResponseEntity with APIResponse and HTTP 409 status
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<APIResponse> myResourceAlreadyExistsException(ResourceAlreadyExistsException ex){
        String message = ex.getMessage();
        APIResponse apiResponse = new APIResponse(message,false);
        return new ResponseEntity<>(apiResponse,HttpStatus.CONFLICT);

    }
}