package com.mockxpert.interview_marketplace.exceptions;

/**
 * Exception class for managing all resource not found exceptions.
 * 
 * @author Umar Mohammad
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}