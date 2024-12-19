package com.mockxpert.interview_marketplace.mappers;

import com.mockxpert.interview_marketplace.dto.InterviewerVerificationDto;
import com.mockxpert.interview_marketplace.entities.Interviewer;
import com.mockxpert.interview_marketplace.entities.InterviewerVerification;

public class InterviewerVerificationMapper {

    // Convert InterviewerVerification Entity to InterviewerVerificationDto
    public static InterviewerVerificationDto toDto(InterviewerVerification verification) {
        if (verification == null) {
            return null;
        }

        InterviewerVerificationDto dto = new InterviewerVerificationDto();
        dto.setVerificationId(verification.getVerificationId());
        dto.setInterviewerId(verification.getInterviewer() != null ? verification.getInterviewer().getInterviewerId() : null);
        dto.setDocumentUrl(verification.getDocumentUrl());
        dto.setDocumentType(verification.getDocumentType());
        dto.setUploadDate(verification.getUploadDate());
        dto.setStatus(verification.getStatus() != null ? verification.getStatus().name() : null);
        dto.setVerifiedBy(verification.getVerifiedBy());
        dto.setVerificationDate(verification.getVerificationDate());
        dto.setVerificationComments(verification.getVerificationComments());

        return dto;
    }

    // Convert InterviewerVerificationDto to InterviewerVerification Entity
    public static InterviewerVerification toEntity(InterviewerVerificationDto dto, Interviewer interviewer) {
        if (dto == null) {
            return null;
        }

        InterviewerVerification verification = new InterviewerVerification();
        verification.setVerificationId(dto.getVerificationId());
        verification.setInterviewer(interviewer);
        verification.setDocumentUrl(dto.getDocumentUrl());
        verification.setDocumentType(dto.getDocumentType());
        verification.setUploadDate(dto.getUploadDate());
        verification.setStatus(dto.getStatus() != null ? InterviewerVerification.VerificationStatus.valueOf(dto.getStatus()) : null);
        verification.setVerifiedBy(dto.getVerifiedBy());
        verification.setVerificationDate(dto.getVerificationDate());
        verification.setVerificationComments(dto.getVerificationComments());

        return verification;
    }
}
