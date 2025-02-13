package com.mockxpert.interview_marketplace.dto;

import java.time.*;
import jakarta.validation.constraints.*;


/**
 * Data Transfer Object for the interview verification JSON object.
 * 
 * @author Umar Mohammad
 */

public class InterviewerVerificationDto {
	
    private Long verificationId;
    
    private String verificationToken;
    
    private String status;
    
    private String verificationNotes;
    
    private LocalDateTime lastUpdated;
    
    private Long interviewerId;
    
    private LocalDateTime tokenExpiry;

    
    public Long getVerificationIdId() {
        return verificationId;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public String getStatus() {
        return status;
    }

    public String getVerificationNotes() {
        return verificationNotes;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public Long getInterviewerId() {
        return interviewerId;
    }

    public LocalDateTime getTokenExpiry() {
        return tokenExpiry;
    }

    public void setVerificationId(Long verificationId) {
        this.verificationId = verificationId;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setVerificationNotes(String verificationNotes) {
        this.verificationNotes = verificationNotes;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setInterviewerId(Long interviewerId) {
        this.interviewerId = interviewerId;
    }

    public void setTokenExpiry(LocalDateTime tokenExpiry) {
        this.tokenExpiry = tokenExpiry;
    }
}

