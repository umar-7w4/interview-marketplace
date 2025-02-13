package com.mockxpert.interview_marketplace.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Service class for sending email notifications.
 * 
 * @author Umar Mohammad
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Base URL for verification links, configured in application.properties.
     * Example: https://yourdomain.com/api/verification/verify-email?token=
     */
    @Value("${app.verification.url}")
    private String verificationUrl;

    /**
     * Default "From" email address, configured in application.properties.
     */
    @Value("${meeting.google.account.email}")
    private String defaultFromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    /**
     * Sends an OTP verification email.
     *
     * @param recipientEmail the recipient's email address.
     * @param otp            the OTP to be sent.
     */
    public void sendOtpEmail(String recipientEmail, String otp) {
    	try {
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setFrom(defaultFromEmail); // Replace with a valid email address
	        message.setTo(recipientEmail);
	        message.setSubject("MockXpert Interviewer Verification Code");
	        message.setText("Your verification code is: " + otp + "\n\n This code will expire in 15 minutes.");
	
	        mailSender.send(message);
	    } catch (Exception e) {
	        throw new RuntimeException("Failed to send email to "+ e);
	    }
    }

    /**
     * Sends a verification email to an interviewer's work email.
     *
     * @param workEmail the interviewer's work email.
     * @param token     the verification token.
     */
    public void sendVerificationEmail(String workEmail) {
        String subject = "Congragulations your profile got verified";
        String message = "<p>Dear Interviewer,</p>" +
                "<p>Thank you for registering with <strong>MockXpert</strong>. We are happy to announce that your profile as Interviewer got verified.</p>" +
                "<p>We wish you all the best and hope to serve you well.</p>" +
                "<p>Best regards,<br>MockXpert Team</p>";

        sendHtmlEmail(workEmail, subject, message);
    }

    /**
     * Sends a general email notification.
     *
     * @param to      The recipient's email.
     * @param subject The subject of the email.
     * @param message The content of the email (supports HTML).
     */
    public void sendNotificationEmail(String to, String subject, String message) {
        sendHtmlEmail(to, subject, message);
    }

    /**
     * Sends a plain text email.
     *
     * @param to      The recipient's email.
     * @param subject The subject of the email.
     * @param message The content of the email (plain text).
     */
    public void sendPlainTextEmail(String to, String subject, String message) {
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(to);
            email.setFrom(defaultFromEmail);
            email.setSubject(subject);
            email.setText(message);
            
            mailSender.send(email);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email to " + to, e);
        }
    }

    /**
     * Sends an HTML email.
     *
     * @param to      The recipient's email.
     * @param subject The subject of the email.
     * @param message The HTML content of the email.
     */
    private void sendHtmlEmail(String to, String subject, String message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(defaultFromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(formatEmailHtml(subject, message), true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email to " + to, e);
        }
    }

    /**
     * Formats the email content with an HTML template.
     *
     * @param subject Email subject.
     * @param message Email body.
     * @return Formatted HTML email.
     */
    private String formatEmailHtml(String subject, String message) {
        return "<html><body style='font-family: Arial, sans-serif;'>" +
                "<h3 style='color: #007bff;'>" + subject + "</h3>" +
                "<p>" + message + "</p>" +
                "<br><p style='font-size: 12px; color: gray;'>MockXpert Team</p>" +
                "</body></html>";
    }
}
