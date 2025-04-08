package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.VerificationResponseDto;
import com.mockxpert.interview_marketplace.entities.Interviewer;
import com.mockxpert.interview_marketplace.entities.InterviewerVerification;
import com.mockxpert.interview_marketplace.entities.User;
import com.mockxpert.interview_marketplace.exceptions.BadRequestException;
import com.mockxpert.interview_marketplace.exceptions.ResourceNotFoundException;
import com.mockxpert.interview_marketplace.repositories.InterviewerRepository;
import com.mockxpert.interview_marketplace.repositories.InterviewerVerificationRepository;
import com.mockxpert.interview_marketplace.utils.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service class for managing InterviewerVerification entities.
 * <p>
 * This service now accepts a userId, retrieves the corresponding Interviewer record,
 * and then performs the OTP generation, verification, and resend operations for work email.
 * </p>
 */
@Service
public class InterviewerVerificationService {

    @Autowired
    private InterviewerVerificationRepository verificationRepository;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private InterviewerRepository interviewerRepository;

    /**
     * Initiates the verification process by generating an OTP and sending an email.
     * Uses the provided userId to fetch the associated Interviewer record.
     *
     * @param userId the ID of the user whose associated interviewer record should be verified.
     */
    @Transactional
    public void sendVerificationOtp(Long userId) {
        // Retrieve the Interviewer record based on the user's ID.
        Interviewer interviewer = interviewerRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Interviewer not found for user ID: " + userId));

        User user = interviewer.getUser();
        if (user == null || user.getWorkEmail() == null || !user.getWorkEmail().contains("@")) {
            throw new BadRequestException("Invalid work email provided.");
        }

        String otp = TokenGenerator.generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(15);

        Optional<InterviewerVerification> existingVerification =
                verificationRepository.findByInterviewer_InterviewerId(interviewer.getInterviewerId());
        InterviewerVerification verification = existingVerification.orElse(new InterviewerVerification());

        verification.setInterviewer(interviewer);
        verification.setVerificationToken(otp);
        verification.setTokenExpiry(expiryTime);
        verification.setStatus(InterviewerVerification.VerificationStatus.EMAIL_SENT);
        verification.setLastUpdated(LocalDateTime.now());

        verificationRepository.save(verification);

        emailService.sendOtpEmail(user.getWorkEmail(), otp);
    }

    /**
     * Verifies the OTP submitted by the interviewer.
     * Uses the provided userId to retrieve the Interviewer record and its verification details.
     *
     * @param userId the user's ID whose associated interviewer record is being verified.
     * @param otp    the OTP provided by the interviewer.
     */
    @Transactional
    public void verifyOtp(Long userId, String otp) {
        Interviewer interviewer = interviewerRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Interviewer not found for user ID: " + userId));

        InterviewerVerification verification =
                verificationRepository.findByInterviewer_InterviewerId(interviewer.getInterviewerId())
                .orElseThrow(() -> new ResourceNotFoundException("Verification not found for interviewer with user ID: " + userId));

        if (!verification.getVerificationToken().equals(otp)) {
            throw new BadRequestException("Invalid OTP.");
        }

        if (verification.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP has expired. Please request a new one.");
        }

        verification.setStatus(InterviewerVerification.VerificationStatus.VERIFIED);
        verification.setLastUpdated(LocalDateTime.now());
        verificationRepository.save(verification);

        interviewer.setStatus(Interviewer.Status.ACTIVE);
        interviewer.setIsVerified(true);
        interviewerRepository.save(interviewer);

        User user = interviewer.getUser();
        if (user != null) {
            user.setStatus(User.Status.ACTIVE);
            user.setWorkEmailVerified(true);
        }

        emailService.sendVerificationEmail(user.getWorkEmail());
    }

    /**
     * Resends a new OTP to the interviewer's work email.
     *
     * @param userId the ID of the user whose associated interviewer record should have its OTP resent.
     */
    @Transactional
    public void resendOtp(Long userId) {
        sendVerificationOtp(userId);
    }
}
