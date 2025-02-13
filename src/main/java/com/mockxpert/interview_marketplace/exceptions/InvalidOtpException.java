package com.mockxpert.interview_marketplace.exceptions;


/**
 * Exception class for managing all invalid otp exceptions.
 * 
 * @author Umar Mohammad
 */
public class InvalidOtpException extends RuntimeException {
    public InvalidOtpException(String message) {
        super(message);
    }
}