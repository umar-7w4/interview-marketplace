package com.mockxpert.interview_marketplace.exceptions;

/**
 * Exception class for managing all validation related exceptions.
 * 
 * @author Umar Mohammad
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}