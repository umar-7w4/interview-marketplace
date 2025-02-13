package com.mockxpert.interview_marketplace.exceptions;

/**
 * Exception class for managing all conflict exceptions.
 * 
 * @author Umar Mohammad
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}