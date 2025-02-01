package com.mockxpert.interview_marketplace.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service class for sending emails.
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Base URL for verification links, configured in application.properties.
     * Example: https://yourdomain.com/api/verification/verify-email?token=
     */
    private final String verificationUrl = "https://yourdomain.com/api/verification/verify-email?token=";

    /**
     * Default "From" email address.
     */
    private final String defaultFromEmail = "mohammadumar19996w4@gmail.com"; // Replace with your verified email

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a verification email to the specified work email with the provided token.
     *
     * @param workEmail the interviewer's work email.
     * @param token     the verification token.
     */
    public void sendVerificationEmail(String workEmail, String token) {
        String verificationLink = verificationUrl + token;

        String subject = "Email Verification for Your Interviewer Profile";
        String message = String.format(
                "Dear Interviewer,\n\n" +
                "Thank you for registering with us. Please verify your email address by clicking the link below:\n" +
                "%s\n\n" +
                "If you did not initiate this request, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Your Company Name",
                verificationLink
        );

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(workEmail);
        email.setFrom(defaultFromEmail); // Set the "From" header
        email.setSubject(subject);
        email.setText(message);

        mailSender.send(email);
    }
}
