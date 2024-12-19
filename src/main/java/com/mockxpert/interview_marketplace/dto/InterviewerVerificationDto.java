package com.mockxpert.interview_marketplace.dto;



import java.time.LocalDate;

import jakarta.validation.constraints.*;

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

	public Long getVerificationId() {
		return verificationId;
	}

	public void setVerificationId(Long verificationId) {
		this.verificationId = verificationId;
	}

	public Long getInterviewerId() {
		return interviewerId;
	}

	public void setInterviewerId(Long interviewerId) {
		this.interviewerId = interviewerId;
	}

	public String getDocumentUrl() {
		return documentUrl;
	}

	public void setDocumentUrl(String documentUrl) {
		this.documentUrl = documentUrl;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public LocalDate getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(LocalDate uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVerifiedBy() {
		return verifiedBy;
	}

	public void setVerifiedBy(String verifiedBy) {
		this.verifiedBy = verifiedBy;
	}

	public LocalDate getVerificationDate() {
		return verificationDate;
	}

	public void setVerificationDate(LocalDate verificationDate) {
		this.verificationDate = verificationDate;
	}

	public String getVerificationComments() {
		return verificationComments;
	}

	public void setVerificationComments(String verificationComments) {
		this.verificationComments = verificationComments;
	}
    
    
}
