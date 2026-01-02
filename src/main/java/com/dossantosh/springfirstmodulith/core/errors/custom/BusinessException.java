package com.dossantosh.springfirstmodulith.core.errors.custom;

/**
 * Custom exception class to represent business logic errors.
 * 
 * This exception is thrown when a business rule or validation fails
 * within the application.
 * 
 * It extends RuntimeException, so it is an unchecked exception.
 */
public class BusinessException extends RuntimeException {

    /**
     * Constructs a new BusinessException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public BusinessException(String message) {
        super(message);
    }
}
