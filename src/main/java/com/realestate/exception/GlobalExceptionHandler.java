package com.realestate.exception;

import com.realestate.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global Exception Handler for the Real Estate Application
 * Provides centralized exception handling with proper HTTP status codes and
 * error messages
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle property validation exceptions
     * 
     * @param ex the PropertyValidationException
     * @return error response with BAD_REQUEST status
     */
    @ExceptionHandler(PropertyValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handlePropertyValidationException(PropertyValidationException ex) {
        logger.warn("Property validation error: {}", ex.getMessage());

        ApiResponse<Object> errorResponse = new ApiResponse<>(false, ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle validation errors from @Valid annotations
     * 
     * @param ex the MethodArgumentNotValidException
     * @return error response with BAD_REQUEST status and field-specific errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        logger.warn("Validation error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> errorResponse = new ApiResponse<>(
                false,
                "Validation failed for one or more fields",
                errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle constraint violation exceptions from @Validated
     * 
     * @param ex the ConstraintViolationException
     * @return error response with BAD_REQUEST status
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolationException(
            ConstraintViolationException ex) {

        logger.warn("Constraint violation error: {}", ex.getMessage());

        Map<String, String> errors = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage));

        ApiResponse<Map<String, String>> errorResponse = new ApiResponse<>(
                false,
                "Validation constraints violated",
                errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle method argument type mismatch (e.g., invalid enum values)
     * 
     * @param ex the MethodArgumentTypeMismatchException
     * @return error response with BAD_REQUEST status
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {

        logger.warn("Type mismatch error: {}", ex.getMessage());

        String message = String.format(
                "Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(),
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        ApiResponse<Object> errorResponse = new ApiResponse<>(false, message, null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle illegal argument exceptions (e.g., invalid enum values)
     * 
     * @param ex the IllegalArgumentException
     * @return error response with BAD_REQUEST status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        logger.warn("Illegal argument error: {}", ex.getMessage());

        ApiResponse<Object> errorResponse = new ApiResponse<>(false, ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle resource not found exceptions
     * 
     * @param ex the RuntimeException with "not found" message
     * @return error response with NOT_FOUND status
     */
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ApiResponse<?>> handleTransactionException(TransactionSystemException ex) {
        logger.error("Transaction error: {}", ex.getMessage(), ex);

        // Extract the underlying cause for better error message
        Throwable cause = ex.getCause();
        String message = "Could not complete the database operation";

        if (cause instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) cause;
            Map<String, String> errors = cve.getConstraintViolations()
                    .stream()
                    .collect(Collectors.toMap(
                            violation -> violation.getPropertyPath().toString(),
                            ConstraintViolation::getMessage));
            ApiResponse<Map<String, String>> errorResponse = new ApiResponse<>(
                    false,
                    "Validation constraints violated during database operation",
                    errors);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Check for database integrity violations
        if (cause != null && cause.getMessage() != null) {
            message = cause.getMessage();
        }

        ApiResponse<Object> errorResponse = new ApiResponse<>(false, message, null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle data integrity violation exceptions (constraint violations at DB
     * level)
     * 
     * @param ex the DataIntegrityViolationException
     * @return error response with BAD_REQUEST status
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {
        logger.warn("Data integrity violation: {}", ex.getMessage());

        String message = "Invalid data provided. Please check your input and try again.";

        // Provide more specific messages for common constraint violations
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("unique")) {
                message = "This record already exists. Please use a unique value.";
            } else if (ex.getMessage().contains("foreign key")) {
                message = "Cannot complete operation due to related records. Please check your selections.";
            } else if (ex.getMessage().contains("not null")) {
                message = "Required field is missing. Please fill in all required fields.";
            }
        }

        ApiResponse<Object> errorResponse = new ApiResponse<>(false, message, null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle resource not found exceptions
     * 
     * @param ex the RuntimeException with "not found" message
     * @return error response with NOT_FOUND status
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex) {
        logger.error("Runtime error: {}", ex.getMessage(), ex);

        // Check if it's a "not found" type error
        if (ex.getMessage() != null &&
                (ex.getMessage().toLowerCase().contains("not found") ||
                        ex.getMessage().toLowerCase().contains("not available"))) {

            ApiResponse<Object> errorResponse = new ApiResponse<>(false, ex.getMessage(), null);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        // Generic runtime exception
        ApiResponse<Object> errorResponse = new ApiResponse<>(
                false,
                "An error occurred while processing your request: " + ex.getMessage(),
                null);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle all other exceptions
     * 
     * @param ex the Exception
     * @return error response with INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        ApiResponse<Object> errorResponse = new ApiResponse<>(
                false,
                "An unexpected error occurred. Please try again later.",
                null);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle number format exceptions
     * 
     * @param ex the NumberFormatException
     * @return error response with BAD_REQUEST status
     */
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiResponse<Object>> handleNumberFormatException(NumberFormatException ex) {
        logger.warn("Number format error: {}", ex.getMessage());

        ApiResponse<Object> errorResponse = new ApiResponse<>(
                false,
                "Invalid number format. Please provide a valid numeric value.",
                null);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}