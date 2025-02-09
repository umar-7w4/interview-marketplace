package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.AdminOverrideDto;
import com.mockxpert.interview_marketplace.dto.VerificationResponseDto;
import com.mockxpert.interview_marketplace.entities.Interviewer;
import com.mockxpert.interview_marketplace.entities.InterviewerVerification;
import com.mockxpert.interview_marketplace.entities.InterviewerVerification.VerificationStatus;
import com.mockxpert.interview_marketplace.exceptions.BadRequestException;
import com.mockxpert.interview_marketplace.exceptions.ResourceNotFoundException;
import com.mockxpert.interview_marketplace.mappers.InterviewerVerificationMapper;
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
    private InterviewerService interviewerService;
    
    @Autowired
    private InterviewerRepository interviewerRepository;

    /**
     * Initiates the verification process by generating a token and sending a verification email.
     *
     * @param interviewer the interviewer to verify.
     */
    @Transactional
    public void initiateVerification(long interviewerId) {
    	
        Interviewer interviewer = interviewerRepository.findById(interviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Interviewer not found with ID: " + interviewerId));
        
        // Generate a unique verification token
        String token = TokenGenerator.generateToken();
        
        System.out.println(token);

        // Set token expiry (e.g., 24 hours from now)
        LocalDateTime expiryTime = LocalDateTime.now().plusHours(24);

        // Create InterviewerVerification record
        InterviewerVerification verification = new InterviewerVerification();
        verification.setVerificationToken(token);
        verification.setStatus(VerificationStatus.EMAIL_SENT);
        verification.setInterviewer(interviewer);
        verification.setTokenExpiry(expiryTime);
        verification.setLastUpdated(LocalDateTime.now());

        // Save verification record
        verificationRepository.save(verification);

        // Send verification email
        emailService.sendVerificationEmail(interviewer.getUser().getEmail(), token);
    }

    /**
     * Verifies the interviewer's email based on the provided token.
     *
     * @param token the verification token.
     * @return VerificationResponseDto indicating the result.
     */
    @Transactional
    public VerificationResponseDto verifyEmail(String token) {
        // Retrieve verification record by token
        InterviewerVerification verification = verificationRepository.findByVerificationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid verification token."));

        // Check if already verified
        if (verification.getStatus() == VerificationStatus.VERIFIED) {
            throw new BadRequestException("Email is already verified.");
        }

        // Check if token is expired
        if (verification.getTokenExpiry() != null && verification.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Verification token has expired.");
        }

        // Update verification status to VERIFIED
        verification.setStatus(VerificationStatus.VERIFIED);
        verification.setLastUpdated(LocalDateTime.now());

        // Save updated verification record
        verificationRepository.save(verification);

        // Update interviewer's isVerified flag via InterviewerService
        Interviewer interviewer = verification.getInterviewer();
        interviewerService.updateInterviewerVerificationStatus(interviewer.getInterviewerId(), true);

        // Map to VerificationResponseDto
        return InterviewerVerificationMapper.toVerificationResponseDto(verification);
    }

    /**
     * Admin overrides the verification status of an interviewer.
     *
     * @param adminOverrideDto the DTO containing override information.
     * @return VerificationResponseDto indicating the override result.
     */
    @Transactional
    public VerificationResponseDto adminOverride(AdminOverrideDto adminOverrideDto) {
        // Retrieve verification record by interviewer ID
        InterviewerVerification verification = verificationRepository.findByInterviewer_InterviewerId(adminOverrideDto.getInterviewerId())
                .orElseThrow(() -> new ResourceNotFoundException("Verification not found for interviewer ID: " + adminOverrideDto.getInterviewerId()));

        // Update verification entity based on AdminOverrideDto
        InterviewerVerificationMapper.updateVerificationFromAdminOverride(verification, adminOverrideDto);

        // Save updated verification record
        verificationRepository.save(verification);

        // Update interviewer's isVerified flag based on new status
        boolean isVerified = (verification.getStatus() == VerificationStatus.VERIFIED);
        interviewerService.updateInterviewerVerificationStatus(adminOverrideDto.getInterviewerId(), isVerified);

        // Map to VerificationResponseDto
        return InterviewerVerificationMapper.toVerificationResponseDtoAfterOverride(verification);
    }

    /**
     * Resends the verification email to the interviewer.
     *
     * @param interviewerId the ID of the interviewer.
     */
    @Transactional
    public void resendVerificationEmail(Long interviewerId) {
        // Retrieve verification record by interviewer ID
        InterviewerVerification verification = verificationRepository.findByInterviewer_InterviewerId(interviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Verification not found for interviewer ID: " + interviewerId));

        // Check if already verified
        if (verification.getStatus() == VerificationStatus.VERIFIED) {
            throw new BadRequestException("Interviewer is already verified.");
        }

        // Generate a new verification token
        String newToken = TokenGenerator.generateToken();
        verification.setVerificationToken(newToken);
        verification.setStatus(VerificationStatus.EMAIL_SENT);
        verification.setTokenExpiry(LocalDateTime.now().plusHours(24));
        verification.setLastUpdated(LocalDateTime.now());

        // Save updated verification record
        verificationRepository.save(verification);

        // Resend verification email
        emailService.sendVerificationEmail(verification.getInterviewer().getUser().getEmail(), newToken);
    }
    
    /**
     * Initiates the verification process by generating an OTP and sending an email.
     *
     * @param interviewerId the ID of the interviewer to verify.
     */
    @Transactional
    public void sendVerificationOtp(Long interviewerId) {
        Interviewer interviewer = interviewerRepository.findById(interviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Interviewer not found with ID: " + interviewerId));

        String workEmail = interviewer.getUser().getEmail();
        if (workEmail == null || !workEmail.contains("@")) {
            throw new BadRequestException("Invalid work email provided.");
        }

        // Generate OTP (6-digit random number)
        String otp = TokenGenerator.generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(15);

        // Check if an existing verification record exists
        Optional<InterviewerVerification> existingVerification = verificationRepository.findByInterviewer_InterviewerId(interviewerId);
        InterviewerVerification verification = existingVerification.orElse(new InterviewerVerification());

        // Update verification details
        verification.setInterviewer(interviewer);
        verification.setVerificationToken(otp);
        verification.setTokenExpiry(expiryTime);
        verification.setStatus(VerificationStatus.EMAIL_SENT);
        verification.setLastUpdated(LocalDateTime.now());

        // Save verification record
        verificationRepository.save(verification);

        // Send OTP email
        emailService.sendOtpEmail(workEmail, otp);
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

        // Validate OTP
        if (!verification.getVerificationToken().equals(otp)) {
            throw new BadRequestException("Invalid OTP.");
        }

        // Check if OTP is expired
        if (verification.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP has expired. Please request a new one.");
        }

        // Mark verification as completed
        verification.setStatus(VerificationStatus.VERIFIED);
        verification.setLastUpdated(LocalDateTime.now());
        verificationRepository.save(verification);

        // Activate the interviewer
        Interviewer interviewer = verification.getInterviewer();
        interviewer.setStatus(Interviewer.Status.ACTIVE);
        interviewer.setIsVerified(true);
        interviewerRepository.save(interviewer);
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
