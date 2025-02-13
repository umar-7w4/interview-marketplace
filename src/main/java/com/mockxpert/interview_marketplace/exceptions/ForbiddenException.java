package com.mockxpert.interview_marketplace.exceptions;


/**
 * Exception class for managing all forbidden exceptions.
 * 
 * @author Umar Mohammad
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}