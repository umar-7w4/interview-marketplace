package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.NotificationDto;
import com.mockxpert.interview_marketplace.dto.PaymentDto;
import com.mockxpert.interview_marketplace.entities.*;
import com.mockxpert.interview_marketplace.entities.Availability.AvailabilityStatus;
import com.mockxpert.interview_marketplace.entities.Booking.PaymentStatus;
import com.mockxpert.interview_marketplace.exceptions.ResourceNotFoundException;
import com.mockxpert.interview_marketplace.mappers.PaymentMapper;
import com.mockxpert.interview_marketplace.repositories.BookingRepository;
import com.mockxpert.interview_marketplace.repositories.InterviewRepository;
import com.mockxpert.interview_marketplace.repositories.IntervieweeRepository;
import com.mockxpert.interview_marketplace.repositories.InterviewerRepository;
import com.mockxpert.interview_marketplace.repositories.PaymentRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.util.List;

/**
 * Service class for managing all payment operations.
 * 
 * @author Umar Mohammad
 */
@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private InterviewRepository interviewRepository;
    
    @Autowired
    private InterviewerRepository interviewerRepository;
    
    @Autowired
    private IntervieweeRepository intervieweeRepository;

    @Autowired
    private GoogleCalendarService googleCalendarService;
    
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private GoogleOAuthService googleOAuthService;

    // The dedicated meeting account's refresh token from application.properties.
    @Value("${meeting.google.refresh.token}")
    private String dedicatedGoogleRefreshToken;

    /**
     * Creates a new payment record in PENDING state when the user starts the Stripe checkout process.
     *
     * @param bookingId The ID of the booking.
     * @param sessionId The Stripe session ID.
     * @return The created PaymentDto.
     */
    @Transactional
    public PaymentDto createPayment(Long bookingId, String sessionId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        Payment existingPayment = paymentRepository.findByTransactionId(sessionId);
        if (existingPayment != null) {
            throw new IllegalArgumentException("Payment already exists for session ID: " + sessionId);
        }

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setTransactionId(sessionId);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setAmount(booking.getTotalPrice());
        payment.setCurrency("USD");
        payment.setPaymentMethod("Stripe");
        payment.setPaymentStatus(Payment.PaymentStatus.PENDING);

        Payment savedPayment = paymentRepository.save(payment);
        logger.info("Payment record created with status PENDING for Booking ID: {}", bookingId);

        return PaymentMapper.toDto(savedPayment);
    }

    /**
     * Processes a successful payment event from Stripe webhook.
     * Updates payment status, creates an interview, and schedules a Google Meet event.
     *
     * @param sessionId The Stripe session ID confirming the successful transaction.
     * @return The updated PaymentDto after processing.
     */
    @Transactional    
    public PaymentDto processSuccessfulPayment(String sessionId) {
        Payment payment = paymentRepository.findByTransactionId(sessionId);
        if (payment == null) {
            throw new ResourceNotFoundException("Payment not found for session ID: " + sessionId);
        }

        if (payment.getPaymentStatus() == Payment.PaymentStatus.PAID) {
            logger.warn("Payment for session {} is already marked as PAID.", sessionId);
            return PaymentMapper.toDto(payment);
        }

        payment.setPaymentStatus(Payment.PaymentStatus.PAID);
        paymentRepository.save(payment);

        Booking booking = payment.getBooking();
        booking.setPaymentStatus(PaymentStatus.PAID);
        booking.getAvailability().setStatus(AvailabilityStatus.BOOKED);

        String bookingDate = booking.getBookingDate().toString(); 
        String intervieweeName = booking.getInterviewee().getUser().getFullName();
        String interviewerName = booking.getAvailability().getInterviewer().getUser().getFullName();

        String subjectInterviewee = String.format("Payment Successful: Your Booking on %s is Confirmed", bookingDate);
        String messageInterviewee = String.format(
                "Dear %s,<br/><br/>Your payment for your booking on %s has been successfully processed. " +
                "Your interview with %s is now confirmed. We look forward to a great session!",
                intervieweeName, bookingDate, interviewerName);
        sendPaymentNotification(booking.getInterviewee().getUser().getUserId(), subjectInterviewee, messageInterviewee);

        String subjectInterviewer = String.format("Booking Confirmed: Interview Scheduled on %s", bookingDate);
        String messageInterviewer = String.format(
                "Dear %s,<br/><br/>A new booking has been confirmed for your interview session on %s with %s. " +
                "Please prepare for your upcoming interview.",
                interviewerName, bookingDate, intervieweeName);
        
        sendPaymentNotification(booking.getAvailability().getInterviewer().getUser().getUserId(), subjectInterviewer, messageInterviewer);

        if (interviewRepository.existsByBooking_BookingId(booking.getBookingId())) {
            logger.warn("Interview already scheduled for Booking ID: {}", booking.getBookingId());
            return PaymentMapper.toDto(payment);
        }

        if (dedicatedGoogleRefreshToken == null || dedicatedGoogleRefreshToken.isEmpty()) {
            throw new RuntimeException("Dedicated Google refresh token is not configured.");
        }
        String accessToken = googleOAuthService.getAccessTokenFromRefreshToken(dedicatedGoogleRefreshToken);

        String interviewerFirstName = booking.getAvailability().getInterviewer().getUser().getFirstName();
        String intervieweeFirstName = booking.getInterviewee().getUser().getFirstName();

        Interview interview = new Interview();
        interview.setBooking(booking);
        interview.setInterviewee(booking.getInterviewee());
        interview.setInterviewer(booking.getAvailability().getInterviewer());
        interview.setDate(booking.getBookingDate());
        interview.setStartTime(booking.getAvailability().getStartTime());
        interview.setDuration(Duration.ofMinutes(60));
        interview.setStatus(Interview.InterviewStatus.BOOKED);
        interview.setTimezone(booking.getAvailability().getTimezone());
        interview.setTitle("Mock Interview between " + interviewerFirstName + " and " + intervieweeFirstName);

        try {
            String intervieweeEmail = booking.getInterviewee().getUser().getEmail();
            String interviewerEmail = booking.getAvailability().getInterviewer().getUser().getEmail();

            LocalDateTime startTime = booking.getBookingDate().atTime(booking.getAvailability().getStartTime());
            LocalDateTime endTime = booking.getBookingDate().atTime(booking.getAvailability().getEndTime());

            String meetLink = googleCalendarService.createGoogleMeetEvent(
                    accessToken,
                    "Mock Interview",
                    "Scheduled interview",
                    interviewerEmail,
                    intervieweeEmail,
                    startTime,
                    endTime
            );

            interview.setInterviewLink(meetLink);
        } catch (IOException | GeneralSecurityException e) {
            logger.error("Failed to schedule Google Meet event for Booking ID: {}", booking.getBookingId(), e);
            throw new RuntimeException("Google Meet scheduling failed", e);
        }

        Interview savedInterview = interviewRepository.save(interview);
        payment.setInterview(savedInterview);
        paymentRepository.save(payment);

        logger.info("Interview successfully scheduled for Booking ID: {} with Meet Link: {}",
                booking.getBookingId(), savedInterview.getInterviewLink());

        return PaymentMapper.toDto(payment);
    }

    /**
     * Retrieve a payment by ID.
     *
     * @param paymentId the ID of the payment.
     * @return the PaymentDto.
     */
    public PaymentDto getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));
        return PaymentMapper.toDto(payment);
    }

    /**
     * Update an existing payment.
     *
     * @param paymentId  the ID of the payment to update.
     * @param paymentDto the updated payment data transfer object.
     * @return the updated PaymentDto.
     */
    @Transactional
    public PaymentDto updatePayment(Long paymentId, PaymentDto paymentDto) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));

        if (paymentDto.getTransactionId() != null) {
            payment.setTransactionId(paymentDto.getTransactionId());
        }
        if (paymentDto.getPaymentDate() != null) {
            payment.setPaymentDate(paymentDto.getPaymentDate());
        }
        if (paymentDto.getAmount() != null) {
            payment.setAmount(paymentDto.getAmount());
        }
        if (paymentDto.getCurrency() != null) {
            payment.setCurrency(paymentDto.getCurrency());
        }
        if (paymentDto.getPaymentMethod() != null) {
            payment.setPaymentMethod(paymentDto.getPaymentMethod());
        }
        if (paymentDto.getPaymentStatus() != null) {
            payment.setPaymentStatus(Payment.PaymentStatus.valueOf(paymentDto.getPaymentStatus()));
        }

        Payment updatedPayment = paymentRepository.save(payment);
        return PaymentMapper.toDto(updatedPayment);
    }

    /**
     * Delete a payment by ID.
     *
     * @param paymentId the ID of the payment to delete.
     */
    @Transactional
    public void deletePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));
        paymentRepository.delete(payment);
    }

    /**
     * Get all payments.
     *
     * @return a list of PaymentDto.
     */
    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(PaymentMapper::toDto)
                .collect(Collectors.toList());
    }
    

    /**
     * Handles payment refunds and notifies the interviewee.
     *
     * @param paymentId Payment ID.
     */
    @Transactional
    public void processRefund(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));

        payment.setPaymentStatus(Payment.PaymentStatus.REFUNDED);
        paymentRepository.save(payment);

        String bookingDate = payment.getBooking().getBookingDate().toString();
        String intervieweeName = payment.getBooking().getInterviewee().getUser().getFullName();
        String subject = String.format("Refund Processed: Booking on %s", bookingDate);
        String message = String.format(
                "Dear %s,<br/><br/>Your payment for your booking on %s has been refunded. " +
                "If you have any questions, please contact our support team.<br/><br/>Best regards,<br/>MockXpert Team",
                intervieweeName, bookingDate);
        sendPaymentNotification(payment.getBooking().getInterviewee().getUser().getUserId(), subject, message);
    }
    
    /**
     * Helper method to send payment-related notifications using a beautiful HTML email template.
     *
     * @param userId       The recipient's user ID.
     * @param subject      The subject for the notification email.
     * @param plainMessage The plain text message content, which will be wrapped in a styled HTML template.
     */
    private void sendPaymentNotification(Long userId, String subject, String plainMessage) {
        // Build the full HTML email message using the helper method.
        String htmlMessage = buildHtmlEmail(subject, plainMessage);

        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setUserId(userId);
        notificationDto.setSubject(subject);
        notificationDto.setMessage(htmlMessage);
        notificationDto.setType("EMAIL");
        notificationDto.setStatus("SENT");

        notificationService.createNotification(notificationDto);
    }
    
    
    /**
     * Helper method to construct a full HTML email template (using the MockXpert theme) that wraps the content.
     *
     * @param headerTitle The header title to show in the email (typically the subject).
     * @param content     The HTML content (with dynamic data) for the email body.
     * @return A complete HTML string representing the email.
     */
    private String buildHtmlEmail(String headerTitle, String content) {
        return String.format(
            "<!DOCTYPE html>" +
            "<html>" +
              "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<title>%s</title>" +
              "</head>" +
              "<body style=\"margin: 0; padding: 0; background-color: #f4f4f4; font-family: Arial, sans-serif;\">" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%%\">" +
                  "<tr>" +
                    "<td align=\"center\" style=\"padding: 20px 10px;\">" +
                      "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" " +
                        "style=\"background-color: #ffffff; border-radius: 8px; overflow: hidden; " +
                        "box-shadow: 0 2px 8px rgba(0,0,0,0.1);\">" +
                        "<tr>" +
                          "<td align=\"center\" bgcolor=\"#6366f1\" " +
                            "style=\"padding: 30px 0; color: #ffffff; font-size: 28px; font-weight: bold;\">" +
                            "MockXpert" +
                          "</td>" +
                        "</tr>" +
                        "<tr>" +
                          "<td style=\"padding: 40px 30px; color: #333333;\">" +
                            "<p style=\"margin: 20px 0 0 0; font-size: 16px; line-height: 1.5;\">%s</p>" +
                          "</td>" +
                        "</tr>" +
                        "<tr>" +
                          "<td align=\"center\" bgcolor=\"#f4f4f4\" " +
                            "style=\"padding: 20px; font-size: 12px; color: #777777;\">" +
                            "© 2025 MockXpert. All rights reserved." +
                          "</td>" +
                        "</tr>" +
                      "</table>" +
                    "</td>" +
                  "</tr>" +
                "</table>" +
              "</body>" +
            "</html>", headerTitle, content);
    }
    
    
    /**
     * Get the total earnings for an interviewer.
     *
     * @param interviewerId the ID of the interviewer.
     * @return total earnings amount as BigDecimal.
     */
    public BigDecimal getTotalEarningsForInterviewer(Long userId) {
    	long interviewerId = interviewerRepository.findByUser_UserId(userId).get().getInterviewerId();
        return paymentRepository.findAll().stream()
            .filter(payment -> payment.getPaymentStatus() == Payment.PaymentStatus.PAID)
            .filter(payment -> payment.getBooking().getAvailability().getInterviewer().getInterviewerId().equals(interviewerId))
            .map(Payment::getAmount) 
            .reduce(BigDecimal.ZERO, BigDecimal::add); 
    }
    
    /**
     * Retrieve all payments for a given user.
     * This fetches payments where the user is either the interviewee (payer) or the interviewer (receiver).
     *
     * @param userId the ID of the user.
     * @return List of PaymentDto objects.
     */
    public List<PaymentDto> getPaymentsForUser(Long userId) {
        List<Payment> payments = paymentRepository.findPaymentsByUserId(userId);
        if (payments == null || payments.isEmpty()) {
            throw new ResourceNotFoundException("No payments found for user with ID: " + userId);
        }
        return payments.stream()
                .map(PaymentMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get total money spent by interviewee
     * 
     * @param userId
     * @return
     */
    public BigDecimal getTotalSpentByInterviewee(Long userId) {
    	long intervieweeId = intervieweeRepository.findIntervieweeIdByUserId(userId);
        return paymentRepository.getTotalSpentByInterviewee(intervieweeId);
    }

}
