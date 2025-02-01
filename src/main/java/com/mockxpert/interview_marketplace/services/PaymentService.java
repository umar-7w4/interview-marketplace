package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.PaymentDto;
import com.mockxpert.interview_marketplace.entities.Booking;
import com.mockxpert.interview_marketplace.entities.Interview;
import com.mockxpert.interview_marketplace.entities.Interview.InterviewStatus;
import com.mockxpert.interview_marketplace.entities.Interviewer;
import com.mockxpert.interview_marketplace.entities.Payment;
import com.mockxpert.interview_marketplace.entities.User;
import com.mockxpert.interview_marketplace.exceptions.ResourceNotFoundException;
import com.mockxpert.interview_marketplace.mappers.PaymentMapper;
import com.mockxpert.interview_marketplace.repositories.BookingRepository;
import com.mockxpert.interview_marketplace.repositories.InterviewRepository;
import com.mockxpert.interview_marketplace.repositories.InterviewerRepository;
import com.mockxpert.interview_marketplace.repositories.PaymentRepository;
import com.mockxpert.interview_marketplace.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    /**
     * Create a new payment.
     * @param paymentDto the payment data transfer object.
     * @return the saveAndFlushd PaymentDto.
     */
    @Transactional
    public PaymentDto createPayment(PaymentDto paymentDto) {
        Booking booking = bookingRepository.findById(paymentDto.getBookingId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + paymentDto.getBookingId()));

        long interviewerId = booking.getAvailability().getInterviewer().getInterviewerId();
        Interviewer interviewer = interviewerRepository.findById(interviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Interviewer not found with ID: " + interviewerId));

        Payment payment = PaymentMapper.toEntity(paymentDto, booking, null);
        Payment savedPayment = paymentRepository.saveAndFlush(payment);

        if ("PAID".equalsIgnoreCase(paymentDto.getPaymentStatus())) {
            Interview scheduledInterview = new Interview();
            scheduledInterview.setBooking(booking);
            scheduledInterview.setInterviewee(booking.getInterviewee());
            scheduledInterview.setInterviewer(interviewer);
            scheduledInterview.setDate(booking.getBookingDate());
            scheduledInterview.setStartTime(booking.getAvailability().getStartTime());
            scheduledInterview.setDuration(Duration.ofMinutes(60)); // Assuming 60 min default
            scheduledInterview.setInterviewLink(generateMeetingLink(booking.getBookingId())); // Generate meeting link
            scheduledInterview.setStatus(InterviewStatus.BOOKED);
            scheduledInterview.setTimezone(booking.getAvailability().getTimezone());

            Interview savedInterview = interviewRepository.saveAndFlush(scheduledInterview);

            savedPayment.setInterview(savedInterview);
            paymentRepository.save(savedPayment);

            return PaymentMapper.toDto(savedPayment);
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

