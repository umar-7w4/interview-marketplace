package com.mockxpert.interview_marketplace.exceptions;

/**
 * Exception class for managing all internal server exceptions.
 * 
 * @author Umar Mohammad
 */
public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(String message) {
        super(message);
    }
}