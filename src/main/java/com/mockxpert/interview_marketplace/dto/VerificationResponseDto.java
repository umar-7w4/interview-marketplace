package com.mockxpert.interview_marketplace.dto;

import jakarta.validation.constraints.NotNull;


/**
 * Data Transfer Object for the interviewer verification JSON object.
 * 
 * @author Umar Mohammad
 */

public class VerificationResponseDto {
	
    @NotNull(message = "Interviewer ID is required")
    private Long interviewerId;
    
    @NotNull(message = "Status is required")
    private String status;
    
    @NotNull(message = "Message is required")
    private String message;

    public Long getInterviewerId() {
        return interviewerId;
    }

    public void setInterviewerId(Long interviewerId) {
        this.interviewerId = interviewerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

