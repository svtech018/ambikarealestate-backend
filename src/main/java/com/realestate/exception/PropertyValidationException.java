package com.realestate.exception;

/**
 * Custom exception for property validation errors
 * Used when property search criteria or property data validation fails
 */
public class PropertyValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new PropertyValidationException with the specified detail message.
     *
     * @param message the detail message
     */
    public PropertyValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new PropertyValidationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public PropertyValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new PropertyValidationException with the specified cause.
     *
     * @param cause the cause
     */
    public PropertyValidationException(Throwable cause) {
        super(cause);
    }
}