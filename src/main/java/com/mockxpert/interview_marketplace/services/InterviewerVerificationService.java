package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.InterviewerVerificationDto;
import com.mockxpert.interview_marketplace.entities.Interviewer;
import com.mockxpert.interview_marketplace.entities.InterviewerVerification;
import com.mockxpert.interview_marketplace.exceptions.*;
import com.mockxpert.interview_marketplace.mappers.InterviewerVerificationMapper;
import com.mockxpert.interview_marketplace.repositories.InterviewerRepository;
import com.mockxpert.interview_marketplace.repositories.InterviewerVerificationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class InterviewerVerificationService {

    @Autowired
    private InterviewerVerificationRepository verificationRepository;

    @Autowired
    private InterviewerRepository interviewerRepository;

    /**
     * Register a new verification for an interviewer.
     * @param dto the interviewer verification data transfer object containing registration information.
     * @return the saved InterviewerVerification entity.
     */
    @Transactional
    public InterviewerVerificationDto registerInterviewerVerification(InterviewerVerificationDto dto) {
        Interviewer interviewer = interviewerRepository.findById(dto.getInterviewerId())
                .orElseThrow(() -> new ResourceNotFoundException("Interviewer not found with ID: " + dto.getInterviewerId()));

        if (verificationRepository.findByInterviewer_InterviewerId(dto.getInterviewerId()).isPresent()) {
            throw new ConflictException("Verification already exists for interviewer with ID: " + dto.getInterviewerId());
        }

        InterviewerVerification verification = InterviewerVerificationMapper.toEntity(dto, interviewer);

        try {
            InterviewerVerification savedVerification = verificationRepository.save(verification);
            return InterviewerVerificationMapper.toDto(savedVerification);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to register interviewer verification due to server error.");
        }
    }

    /**
     * Update interviewer verification information.
     * @param verificationId the ID of the verification to update.
     * @param dto the interviewer verification data transfer object containing updated information.
     * @return the updated InterviewerVerification entity.
     */
    @Transactional
    public InterviewerVerificationDto updateInterviewerVerification(Long verificationId, InterviewerVerificationDto dto) {
        InterviewerVerification verification = verificationRepository.findById(verificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Verification not found with ID: " + verificationId));

        if (verification.getStatus() == InterviewerVerification.VerificationStatus.APPROVED ||
                verification.getStatus() == InterviewerVerification.VerificationStatus.REJECTED) {
            throw new ForbiddenException("Cannot update verification that has already been approved or rejected.");
        }

        verification.setDocumentUrl(dto.getDocumentUrl());
        verification.setDocumentType(dto.getDocumentType());
        verification.setUploadDate(dto.getUploadDate());
        verification.setVerificationComments(dto.getVerificationComments());

        if (dto.getStatus() != null) {
            try {
                verification.setStatus(InterviewerVerification.VerificationStatus.valueOf(dto.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid verification status: " + dto.getStatus());
            }
        }

        verification.setVerifiedBy(dto.getVerifiedBy());
        verification.setVerificationDate(dto.getVerificationDate());

        try {
            InterviewerVerification updatedVerification = verificationRepository.save(verification);
            return InterviewerVerificationMapper.toDto(updatedVerification);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to update interviewer verification due to server error.");
        }
    }

    /**
     * Find interviewer verification by interviewer ID.
     * @param interviewerId the interviewer ID linked to the verification.
     * @return an Optional containing the InterviewerVerificationDto if found, or empty otherwise.
     */
    public Optional<InterviewerVerificationDto> findByInterviewerId(Long interviewerId) {
        return verificationRepository.findByInterviewer_InterviewerId(interviewerId)
                .map(InterviewerVerificationMapper::toDto)
                .or(() -> {
                    throw new ResourceNotFoundException("Verification not found for interviewer ID: " + interviewerId);
                });
    }

    /**
     * Approve the verification of an interviewer.
     * @param verificationId the ID of the verification to approve.
     * @param verifiedBy the name of the admin verifying the interviewer.
     * @return the updated InterviewerVerification entity.
     */
    @Transactional
    public InterviewerVerificationDto approveVerification(Long verificationId, String verifiedBy) {
        InterviewerVerification verification = verificationRepository.findById(verificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Verification not found with ID: " + verificationId));

        if (verification.getStatus() != InterviewerVerification.VerificationStatus.PENDING) {
            throw new ConflictException("Verification must be in pending status to approve.");
        }

        verification.setStatus(InterviewerVerification.VerificationStatus.APPROVED);
        verification.setVerifiedBy(verifiedBy);
        verification.setVerificationDate(LocalDate.now());

        try {
            InterviewerVerification updatedVerification = verificationRepository.save(verification);
            return InterviewerVerificationMapper.toDto(updatedVerification);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to approve interviewer verification due to server error.");
        }
    }

    /**
     * Reject the verification of an interviewer.
     * @param verificationId the ID of the verification to reject.
     * @param verifiedBy the name of the admin rejecting the interviewer.
     * @param comments comments on why the verification was rejected.
     * @return the updated InterviewerVerification entity.
     */
    @Transactional
    public InterviewerVerificationDto rejectVerification(Long verificationId, String verifiedBy, String comments) {
        InterviewerVerification verification = verificationRepository.findById(verificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Verification not found with ID: " + verificationId));

        if (verification.getStatus() != InterviewerVerification.VerificationStatus.PENDING) {
            throw new ConflictException("Verification must be in pending status to reject.");
        }

        verification.setStatus(InterviewerVerification.VerificationStatus.REJECTED);
        verification.setVerifiedBy(verifiedBy);
        verification.setVerificationDate(LocalDate.now());
        verification.setVerificationComments(comments);

        try {
            InterviewerVerification updatedVerification = verificationRepository.save(verification);
            return InterviewerVerificationMapper.toDto(updatedVerification);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to reject interviewer verification due to server error.");
        }
    }
}
