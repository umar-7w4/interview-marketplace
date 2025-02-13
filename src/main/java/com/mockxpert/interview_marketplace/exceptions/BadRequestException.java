package com.mockxpert.interview_marketplace.exceptions;

/**
 * Exception class for managing all bad request exceptions.
 * 
 * @author Umar Mohammad
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}