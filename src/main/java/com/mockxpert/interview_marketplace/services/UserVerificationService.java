package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.VerificationResponseDto;
import com.mockxpert.interview_marketplace.entities.User;
import com.mockxpert.interview_marketplace.entities.UserVerification;
import com.mockxpert.interview_marketplace.exceptions.BadRequestException;
import com.mockxpert.interview_marketplace.exceptions.ResourceNotFoundException;
import com.mockxpert.interview_marketplace.repositories.UserRepository;
import com.mockxpert.interview_marketplace.repositories.UserVerificationRepository;
import com.mockxpert.interview_marketplace.utils.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserVerificationService {

    @Autowired
    private UserVerificationRepository verificationRepository;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Initiates the verification process by generating an OTP and sending an email
     * to the user's primary email.
     *
     * @param userId the ID of the user to verify.
     */
    @Transactional
    public void sendVerificationOtp(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new BadRequestException("Invalid email provided.");
        }

        String otp = TokenGenerator.generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(15);

        Optional<UserVerification> existingVerification = verificationRepository.findByUser_UserId(userId);
        UserVerification verification = existingVerification.orElse(new UserVerification());
        verification.setUser(user);
        verification.setVerificationToken(otp);
        verification.setTokenExpiry(expiryTime);
        verification.setStatus(UserVerification.VerificationStatus.EMAIL_SENT);
        verification.setLastUpdated(LocalDateTime.now());

        verificationRepository.save(verification);

        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    /**
     * Verifies the OTP submitted by the user.
     *
     * @param userId the user's ID.
     * @param otp    the OTP provided by the user.
     */
    @Transactional
    public void verifyOtp(Long userId, String otp) {
        UserVerification verification = verificationRepository.findByUser_UserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Verification not found for user ID: " + userId));

        if (!verification.getVerificationToken().equals(otp)) {
            throw new BadRequestException("Invalid OTP.");
        }

        if (verification.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP has expired. Please request a new one.");
        }

        verification.setStatus(UserVerification.VerificationStatus.VERIFIED);
        verification.setLastUpdated(LocalDateTime.now());
        verificationRepository.save(verification);

        User user = verification.getUser();
        user.setStatus(User.Status.ACTIVE);
        user.setEmailVerified(true);
        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail());
    }

    /**
     * Resends a new OTP to the user's primary email.
     *
     * @param userId the ID of the user.
     */
    @Transactional
    public void resendOtp(Long userId) {
        sendVerificationOtp(userId);
    }
}

