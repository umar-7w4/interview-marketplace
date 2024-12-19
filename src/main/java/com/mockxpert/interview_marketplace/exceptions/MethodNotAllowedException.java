package com.mockxpert.interview_marketplace.exceptions;

public class MethodNotAllowedException extends RuntimeException {
    public MethodNotAllowedException(String message) {
        super(message);
    }
}