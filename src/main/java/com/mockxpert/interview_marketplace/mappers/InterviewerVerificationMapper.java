package com.mockxpert.interview_marketplace.mappers;

import com.mockxpert.interview_marketplace.dto.AdminOverrideDto;
import com.mockxpert.interview_marketplace.dto.InterviewerVerificationDto;
import com.mockxpert.interview_marketplace.dto.VerificationResponseDto;
import com.mockxpert.interview_marketplace.entities.InterviewerVerification;
import com.mockxpert.interview_marketplace.entities.InterviewerVerification.VerificationStatus;

/**
 * Mapper class for converting between InterviewerVerification entities and DTOs.
 * 
 * @author Umar Mohammad
 */
public class InterviewerVerificationMapper {

    /**
     * Converts an InterviewerVerification entity to a VerificationResponseDto.
     *
     * @param verification the InterviewerVerification entity.
     * @return the VerificationResponseDto.
     */
    public static VerificationResponseDto toVerificationResponseDto(InterviewerVerification verification) {
        if (verification == null) {
            return null;
        }

        VerificationResponseDto dto = new VerificationResponseDto();
        dto.setInterviewerId(verification.getInterviewer().getInterviewerId());
        dto.setStatus(verification.getStatus().name());
        dto.setMessage("Verification status updated to " + verification.getStatus().name());

        return dto;
    }

    /**
     * Converts an InterviewerVerification entity to a detailed InterviewerVerificationDto.
     *
     * @param verification the InterviewerVerification entity.
     * @return the InterviewerVerificationDto.
     */
    public static InterviewerVerificationDto toInterviewerVerificationDto(InterviewerVerification verification) {
        if (verification == null) {
            return null;
        }

        InterviewerVerificationDto dto = new InterviewerVerificationDto();
        dto.setVerificationId(verification.getInterviewerVerificationId());
        dto.setVerificationToken(verification.getVerificationToken());
        dto.setStatus(verification.getStatus().name());
        dto.setVerificationNotes(verification.getVerificationNotes());
        dto.setLastUpdated(verification.getLastUpdated());
        dto.setInterviewerId(verification.getInterviewer().getInterviewerId());
        dto.setTokenExpiry(verification.getTokenExpiry());

        return dto;
    }

    /**
     * Updates the InterviewerVerification entity based on AdminOverrideDto.
     *
     * @param verification      the InterviewerVerification entity to update.
     * @param adminOverrideDto the AdminOverrideDto containing override information.
     * @throws IllegalArgumentException if the provided status is invalid.
     */
    public static void updateVerificationFromAdminOverride(InterviewerVerification verification, AdminOverrideDto adminOverrideDto) {
        if (verification == null || adminOverrideDto == null) {
            throw new IllegalArgumentException("Verification entity and AdminOverrideDto must not be null.");
        }

        // Convert status string to enum
        VerificationStatus newStatus;
        try {
            newStatus = VerificationStatus.valueOf(adminOverrideDto.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid verification status: " + adminOverrideDto.getStatus());
        }

        // Update status and notes
        verification.setStatus(newStatus);
        verification.setVerificationNotes(adminOverrideDto.getNotes());
    }

    /**
     * Converts an InterviewerVerification entity to a VerificationResponseDto after admin override.
     *
     * @param verification the updated InterviewerVerification entity.
     * @return the VerificationResponseDto.
     */
    public static VerificationResponseDto toVerificationResponseDtoAfterOverride(InterviewerVerification verification) {
        if (verification == null) {
            return null;
        }

        VerificationResponseDto dto = new VerificationResponseDto();
        dto.setInterviewerId(verification.getInterviewer().getInterviewerId());
        dto.setStatus(verification.getStatus().name());
        dto.setMessage("Admin override: Verification status set to " + verification.getStatus().name());

        return dto;
    }
}

