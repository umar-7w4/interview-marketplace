package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.PaymentDto;
import com.mockxpert.interview_marketplace.entities.Availability;
import com.mockxpert.interview_marketplace.entities.Booking;
import com.mockxpert.interview_marketplace.entities.Booking.PaymentStatus;
import com.mockxpert.interview_marketplace.entities.Interview;
import com.mockxpert.interview_marketplace.entities.Interview.InterviewStatus;
import com.mockxpert.interview_marketplace.entities.Interviewer;
import com.mockxpert.interview_marketplace.entities.Payment;
import com.mockxpert.interview_marketplace.entities.User;
import com.mockxpert.interview_marketplace.exceptions.ResourceNotFoundException;
import com.mockxpert.interview_marketplace.mappers.PaymentMapper;
import com.mockxpert.interview_marketplace.repositories.AvailabilityRepository;
import com.mockxpert.interview_marketplace.repositories.BookingRepository;
import com.mockxpert.interview_marketplace.repositories.InterviewRepository;
import com.mockxpert.interview_marketplace.repositories.InterviewerRepository;
import com.mockxpert.interview_marketplace.repositories.PaymentRepository;
import com.mockxpert.interview_marketplace.repositories.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InterviewRepository interviewRepository;
    
    @Autowired
    private InterviewerRepository interviewerRepository;
    
    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private AvailabilityRepository availabilityRepository;


    @Autowired
    private GoogleCalendarService googleCalendarService;

    /**
     * Process payment and schedule an interview with Google Meet link.
     * @param paymentDto the payment details.
     * @return the saved PaymentDto.
     */
    @Transactional
    public PaymentDto createPayment(PaymentDto paymentDto) {
        Booking booking = bookingRepository.findById(paymentDto.getBookingId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + paymentDto.getBookingId()));

        Availability availability = booking.getAvailability();
        Interviewer interviewer = interviewerRepository.findById(availability.getInterviewer().getInterviewerId())
                .orElseThrow(() -> new ResourceNotFoundException("Interviewer not found"));

        Payment payment = PaymentMapper.toEntity(paymentDto, booking, null);
        Payment savedPayment = paymentRepository.saveAndFlush(payment);

        if ("PAID".equalsIgnoreCase(paymentDto.getPaymentStatus())) {
            Interview interview = new Interview();
            interview.setBooking(booking);
            interview.setInterviewee(booking.getInterviewee());
            interview.setInterviewer(interviewer);
            interview.setDate(booking.getBookingDate());
            interview.setStartTime(availability.getStartTime());
            interview.setDuration(Duration.ofMinutes(60));
            interview.setStatus(Interview.InterviewStatus.BOOKED);
            interview.setTimezone(availability.getTimezone());

            try {
            	String intervieweeName = booking.getInterviewee().getUser().getFullName();
            	String interviewerName = interviewer.getUser().getFullName();
            	String intervieweeEmail = booking.getInterviewee().getUser().getEmail();
            	String interviewerEmail =  interviewer.getUser().getEmail();
            	
            	LocalDateTime startTime = booking.getBookingDate().atTime(availability.getStartTime());
            	LocalDateTime endTime = booking.getBookingDate().atTime(availability.getEndTime());

            	String meetLink = googleCalendarService.createGoogleMeetEvent(
            	        "Mock Interview",
            	        "Scheduled interview between " + intervieweeName + " and " + interviewerName,
            	        interviewerEmail,
            	        intervieweeEmail,
            	        startTime,
            	        endTime
            	);

                interview.setInterviewLink(meetLink);
            } catch (IOException | GeneralSecurityException e) {
                throw new RuntimeException("Failed to schedule Google Meet event", e);
            }

            Interview savedInterview = interviewRepository.saveAndFlush(interview);
            booking.setPaymentStatus(Booking.PaymentStatus.PAID);
            savedPayment.setInterview(savedInterview);
            paymentRepository.save(savedPayment);
        }
        return PaymentMapper.toDto(savedPayment);
    }


    /**
     * Retrieve a payment by ID.
     * @param paymentId the ID of the payment.
     * @return the PaymentDto.
     */
    public PaymentDto getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + paymentId));

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
        Payment existingPayment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + paymentId));

        Booking booking = bookingRepository.findById(paymentDto.getBookingId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + paymentDto.getBookingId()));

       
        Interview interview = interviewRepository.findById(paymentDto.getInterviewId())
                .orElseThrow(() -> new IllegalArgumentException("Interview not found with ID: " + paymentDto.getInterviewId()));

        existingPayment.setBooking(booking);
        existingPayment.setTransactionId(paymentDto.getTransactionId());
        existingPayment.setPaymentDate(paymentDto.getPaymentDate());
        existingPayment.setAmount(paymentDto.getAmount());
        existingPayment.setCurrency(paymentDto.getCurrency());
        existingPayment.setPaymentMethod(paymentDto.getPaymentMethod());
        existingPayment.setReceiptUrl(paymentDto.getReceiptUrl());
        existingPayment.setRefundAmount(paymentDto.getRefundAmount());
        existingPayment.setPaymentStatus(Payment.PaymentStatus.valueOf(paymentDto.getPaymentStatus()));
        existingPayment.setInterview(interview);

        Payment updatedPayment = paymentRepository.saveAndFlush(existingPayment);

        return PaymentMapper.toDto(updatedPayment);
    }

    /**
     * Delete a payment by ID.
     * @param paymentId the ID of the payment to delete.
     */
    @Transactional
    public void deletePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + paymentId));

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
    

    /**
     * Generates random UUID.
     * @return a id.
     */
    private String generateMeetingLink(Long bookingId) {
        return "https://meet.example.com/" + UUID.randomUUID();
    }

}

