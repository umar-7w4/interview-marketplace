package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.PaymentDto;
import com.mockxpert.interview_marketplace.entities.*;
import com.mockxpert.interview_marketplace.exceptions.ResourceNotFoundException;
import com.mockxpert.interview_marketplace.mappers.PaymentMapper;
import com.mockxpert.interview_marketplace.repositories.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for handling payment transactions and scheduling interviews after payment success.
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
    private GoogleCalendarService googleCalendarService;

    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private GoogleOAuthService googleOAuthService;

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

        // Check if a payment already exists for this session ID
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

        // Mark payment as PAID
        payment.setPaymentStatus(Payment.PaymentStatus.PAID);
        paymentRepository.save(payment);

        // Retrieve booking details
        Booking booking = payment.getBooking();
        
        // Check if an interview already exists for this booking
        if (interviewRepository.existsByBooking_BookingId(booking.getBookingId())) {
            logger.warn("Interview already scheduled for Booking ID: {}", booking.getBookingId());
            return PaymentMapper.toDto(payment);
        }

        // Retrieve interviewee's refresh token from the database
        String refreshToken = booking.getInterviewee().getUser().getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new RuntimeException("No refresh token found for interviewee.");
        }

        // Exchange refresh token for access token
        String accessToken = googleOAuthService.getAccessTokenFromRefreshToken(refreshToken);

        // Create interview record
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
            // Retrieve email addresses
            String intervieweeEmail = booking.getInterviewee().getUser().getEmail();
            String interviewerEmail = booking.getAvailability().getInterviewer().getUser().getEmail();

            // Calculate start and end times
            LocalDateTime startTime = booking.getBookingDate().atTime(booking.getAvailability().getStartTime());
            LocalDateTime endTime = booking.getBookingDate().atTime(booking.getAvailability().getEndTime());

            // Schedule Google Meet event (Interviewee is the owner)
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

        // Save interview record
        Interview savedInterview = interviewRepository.save(interview);
        payment.setInterview(savedInterview);
        paymentRepository.save(payment);

        logger.info("Interview successfully scheduled for Booking ID: {} with Meet Link: {}",
                    booking.getBookingId(), savedInterview.getInterviewLink());

        return PaymentMapper.toDto(payment);
    }




    /**
     * Retrieve a payment by ID.
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
     * @param paymentId the ID of the payment to update.
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
     * @return a list of PaymentDto.
     */
    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(PaymentMapper::toDto)
                .collect(Collectors.toList());
    }
}