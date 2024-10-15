package com.mockxpert.interview_marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewerVerificationDto {

    private Long verificationId;

    @NotNull(message = "Interviewer ID is required")
    private Long interviewerId;

    @NotBlank(message = "Document URL is required")
    private String documentUrl;

    @NotBlank(message = "Document type is required")
    private String documentType;

    @NotNull(message = "Upload date is required")
    private LocalDate uploadDate;

    private String status;  // Use String to represent enum value in DTO

    private String verifiedBy;

    private LocalDate verificationDate;

    @Size(max = 500, message = "Verification comments must be less than 500 characters")
    private String verificationComments;
}
