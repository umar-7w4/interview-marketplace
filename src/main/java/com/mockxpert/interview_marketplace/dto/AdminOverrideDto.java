package com.mockxpert.interview_marketplace.dto;

import jakarta.validation.constraints.*;

/**
 * Data Transfer Object for the admin JSON object.
 * 
 * @author Umar Mohammad
 */
public class AdminOverrideDto {

    @NotNull(message = "Interviewer ID is required")
    private Long interviewerId;

    @NotBlank(message = "Status is required")
    private String status; 

    private String notes;

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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
