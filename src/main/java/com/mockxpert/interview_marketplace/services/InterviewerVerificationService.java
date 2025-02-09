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
     *
     * @param interviewerId the ID of the interviewer to verify.
     */

    @Transactional
    public void sendVerificationOtp(Long interviewerId) {
        Interviewer interviewer = interviewerRepository.findById(interviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Interviewer not found with ID: " + interviewerId));

        User user = interviewer.getUser();
        if (user == null || user.getWorkEmail() == null || !user.getWorkEmail().contains("@")) {
            throw new BadRequestException("Invalid work email provided.");
        }

        String otp = TokenGenerator.generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(15);

        Optional<InterviewerVerification> existingVerification = verificationRepository.findByInterviewer_InterviewerId(interviewerId);
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
     *
     * @param interviewerId the interviewer's ID.
     * @param otp           the OTP provided by the interviewer.
     */
    @Transactional
    public void verifyOtp(Long interviewerId, String otp) {
        InterviewerVerification verification = verificationRepository.findByInterviewer_InterviewerId(interviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Verification not found for interviewer ID: " + interviewerId));

        if (!verification.getVerificationToken().equals(otp)) {
            throw new BadRequestException("Invalid OTP.");
        }

        if (verification.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP has expired. Please request a new one.");
        }

        verification.setStatus(InterviewerVerification.VerificationStatus.VERIFIED);
        verification.setLastUpdated(LocalDateTime.now());
        verificationRepository.save(verification);

        Interviewer interviewer = verification.getInterviewer();
        interviewer.setStatus(Interviewer.Status.ACTIVE);
        interviewer.setIsVerified(true);
        interviewerRepository.save(interviewer);

        User user = interviewer.getUser();
        if (user != null) {
            user.setStatus(User.Status.ACTIVE);
        }
    }

    /**
     * Resends a new OTP to the interviewer's work email.
     *
     * @param interviewerId the ID of the interviewer.
     */
    @Transactional
    public void resendOtp(Long interviewerId) {
        sendVerificationOtp(interviewerId);
    }
}






























