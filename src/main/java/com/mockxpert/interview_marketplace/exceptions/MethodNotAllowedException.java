package com.mockxpert.interview_marketplace.exceptions;

/**
 * Exception class for managing all not allowed methods exceptions.
 * 
 * @author Umar Mohammad
 */
public class MethodNotAllowedException extends RuntimeException {
    public MethodNotAllowedException(String message) {
        super(message);
    }
}