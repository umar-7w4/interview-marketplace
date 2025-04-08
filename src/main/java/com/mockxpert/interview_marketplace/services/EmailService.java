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
     * 
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
            String subject = "MockXpert Interviewer Verification Code";

            String message = String.format("""
                <!DOCTYPE html>
                <html>
                  <head>
                    <meta charset="UTF-8">
                    <title>MockXpert OTP Verification</title>
                  </head>
                  <body style="margin: 0; padding: 0; background-color: #f4f4f4; font-family: Arial, sans-serif;">
                    <table border="0" cellpadding="0" cellspacing="0" width="100%%">
                      <tr>
                        <td align="center" style="padding: 20px 10px;">
                          <table border="0" cellpadding="0" cellspacing="0" width="600"
                            style="background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                            <tr>
                              <td align="center" bgcolor="#6366f1"
                                style="padding: 30px 0; color: #ffffff; font-size: 28px; font-weight: bold;">
                                MockXpert
                              </td>
                            </tr>
                            <tr>
                              <td style="padding: 40px 30px; color: #333333;">
                                <p style="margin: 0; font-size: 16px; line-height: 1.5;">
                                  Dear Interviewer,
                                </p>
                                <p style="margin: 20px 0 0 0; font-size: 16px; line-height: 1.5;">
                                  Please use the following One-Time Password (OTP) to verify your email address:
                                </p>
                                <p style="margin: 20px 0 0 0; font-size: 24px; font-weight: bold; color: #6366f1;">
                                  %s
                                </p>
                                <p style="margin: 20px 0 0 0; font-size: 16px; line-height: 1.5;">
                                  This OTP is valid for <strong>15 minutes</strong>.
                                </p>
                                <p style="margin: 30px 0 0 0; font-size: 16px; line-height: 1.5;">
                                  Best regards,<br />
                                  The MockXpert Team
                                </p>
                              </td>
                            </tr>
                            <tr>
                              <td align="center" bgcolor="#f4f4f4"
                                style="padding: 20px; font-size: 12px; color: #777777;">
                                © 2025 MockXpert. All rights reserved.
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </body>
                </html>
                """, otp);

            sendHtmlEmail(recipientEmail, subject, message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email to " + recipientEmail, e);
        }
    }


    /**
     * Sends a verification email to an interviewer's work email.
     *
     * @param workEmail the interviewer's work email.
     * @param token     the verification token.
     */
    public void sendVerificationEmail(String workEmail) {
        String subject = "Congratulations, your profile got verified!";
        String message = """
            <!DOCTYPE html>
            <html>
              <head>
                <meta charset="UTF-8">
                <title>Congratulations, Your Profile Got Verified!</title>
              </head>
              <body style="margin: 0; padding: 0; background-color: #f4f4f4; font-family: Arial, sans-serif;">
                <!-- Wrapper Table -->
                <table border="0" cellpadding="0" cellspacing="0" width="100%">
                  <tr>
                    <td align="center" style="padding: 20px 10px;">
                      <!-- Main Container -->
                      <table border="0" cellpadding="0" cellspacing="0" width="600" style="background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                        <!-- Header -->
                        <tr>
                          <td align="center" bgcolor="#6366f1" style="padding: 30px 0; color: #ffffff; font-size: 28px; font-weight: bold;">
                            MockXpert
                          </td>
                        </tr>
                        <!-- Body -->
                        <tr>
                          <td style="padding: 40px 30px; color: #333333;">
                            <p style="margin: 0; font-size: 16px; line-height: 1.5;">Dear Interviewer,</p>
                            <p style="margin: 20px 0 0 0; font-size: 16px; line-height: 1.5;">
                              Thank you for registering with <strong>MockXpert</strong>. We are thrilled to announce that your profile as an Interviewer has been verified.
                            </p>
                            <p style="margin: 20px 0 0 0; font-size: 16px; line-height: 1.5;">
                              We wish you all the best and look forward to serving you.
                            </p>
                            <p style="margin: 30px 0 0 0; font-size: 16px; line-height: 1.5;">
                              Best regards,<br>
                              The MockXpert Team
                            </p>
                          </td>
                        </tr>
                        <!-- Footer -->
                        <tr>
                          <td align="center" bgcolor="#f4f4f4" style="padding: 20px; font-size: 12px; color: #777777;">
                            © 2025 MockXpert. All rights reserved.
                          </td>
                        </tr>
                      </table>
                      <!-- End Main Container -->
                    </td>
                  </tr>
                </table>
                <!-- End Wrapper Table -->
              </body>
            </html>
            """;

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
        return "<html><body style='font-family: Arial, sans-serif;'>"  +
                "<p>" + message + "</p>" +
                "<br><p style='font-size: 12px; color: gray;'>MockXpert Team</p>" +
                "</body></html>";
    }
}
