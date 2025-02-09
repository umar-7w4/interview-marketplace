package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.NotificationDto;
import com.mockxpert.interview_marketplace.dto.PaymentDto;
import com.mockxpert.interview_marketplace.entities.*;
import com.mockxpert.interview_marketplace.exceptions.ResourceNotFoundException;
import com.mockxpert.interview_marketplace.mappers.PaymentMapper;
import com.mockxpert.interview_marketplace.repositories.BookingRepository;
import com.mockxpert.interview_marketplace.repositories.InterviewRepository;
import com.mockxpert.interview_marketplace.repositories.PaymentRepository;
import jakarta.persistence.EntityManager;
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

        // Mark payment as PAID.
        payment.setPaymentStatus(Payment.PaymentStatus.PAID);
        paymentRepository.save(payment);

        Booking booking = payment.getBooking();
        
        sendPaymentNotification(booking.getInterviewee().getUser().getUserId(), "Payment Successful",
                "Your payment for booking on " + booking.getBookingDate() + " has been received.");

        sendPaymentNotification(booking.getAvailability().getInterviewer().getUser().getUserId(), "Booking Confirmed",
                "Your interview session on " + booking.getBookingDate() + " has been confirmed after payment.");

        
        if (interviewRepository.existsByBooking_BookingId(booking.getBookingId())) {
            logger.warn("Interview already scheduled for Booking ID: {}", booking.getBookingId());
            return PaymentMapper.toDto(payment);
        }

        // Use the dedicated meeting account's refresh token to obtain an access token.
        if (dedicatedGoogleRefreshToken == null || dedicatedGoogleRefreshToken.isEmpty()) {
            throw new RuntimeException("Dedicated Google refresh token is not configured.");
        }

        String accessToken = googleOAuthService.getAccessTokenFromRefreshToken(dedicatedGoogleRefreshToken);

        // Create the interview record.
        Interview interview = new Interview();
        interview.setBooking(booking);
        interview.setInterviewee(booking.getInterviewee());
        interview.setInterviewer(booking.getAvailability().getInterviewer());
        interview.setDate(booking.getBookingDate());
        interview.setStartTime(booking.getAvailability().getStartTime());
        interview.setDuration(Duration.ofMinutes(60));
        interview.setStatus(Interview.InterviewStatus.BOOKED);
        interview.setTimezone(booking.getAvailability().getTimezone());

        try {
            // Retrieve email addresses.
            String intervieweeEmail = booking.getInterviewee().getUser().getEmail();
            String interviewerEmail = booking.getAvailability().getInterviewer().getUser().getEmail();

            // Calculate start and end times.
            LocalDateTime startTime = booking.getBookingDate().atTime(booking.getAvailability().getStartTime());
            LocalDateTime endTime = booking.getBookingDate().atTime(booking.getAvailability().getEndTime());

            // Schedule the Google Meet event using the dedicated account.
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

        // Notify interviewee about the refund
        sendPaymentNotification(payment.getBooking().getInterviewee().getUser().getUserId(), "Refund Processed",
                "Your payment for booking on " + payment.getBooking().getBookingDate() + " has been refunded.");
    }
    
    private void sendPaymentNotification(Long userId, String subject, String message) {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setUserId(userId);
        notificationDto.setSubject(subject);
        notificationDto.setMessage(message);
        notificationDto.setType("EMAIL");
        notificationDto.setStatus("SENT");

        notificationService.createNotification(notificationDto);
    }
}
