package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.BookingDto;
import com.mockxpert.interview_marketplace.dto.NotificationDto;
import com.mockxpert.interview_marketplace.entities.Availability;
import com.mockxpert.interview_marketplace.entities.Booking;
import com.mockxpert.interview_marketplace.entities.Interviewee;
import com.mockxpert.interview_marketplace.entities.Payment;
import com.mockxpert.interview_marketplace.exceptions.ConflictException;
import com.mockxpert.interview_marketplace.exceptions.ResourceNotFoundException;
import com.mockxpert.interview_marketplace.exceptions.InternalServerErrorException;
import com.mockxpert.interview_marketplace.mappers.BookingMapper;
import com.mockxpert.interview_marketplace.repositories.AvailabilityRepository;
import com.mockxpert.interview_marketplace.repositories.BookingRepository;
import com.mockxpert.interview_marketplace.repositories.IntervieweeRepository;
import com.mockxpert.interview_marketplace.repositories.PaymentRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private IntervieweeRepository intervieweeRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private PaymentService paymentService;

    /**
     * Register a new booking with Optimistic Locking.
     * @param bookingDto the booking data transfer object containing registration information.
     * @return the saved BookingDto.
     */
    @Transactional
    public BookingDto registerBooking(BookingDto bookingDto) {
        Interviewee interviewee = intervieweeRepository.findById(bookingDto.getIntervieweeId())
                .orElseThrow(() -> new ResourceNotFoundException("Interviewee not found with ID: " + bookingDto.getIntervieweeId()));

        Availability availability = availabilityRepository.findById(bookingDto.getAvailabilityId())
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found with ID: " + bookingDto.getAvailabilityId()));

        System.out.println("Availability Version Before Lock: " + availability.getVersion());

        // REMOVE entityManager.refresh(availability);
        entityManager.lock(availability, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

        boolean isSlotBooked = bookingRepository.existsByAvailability(availability);
        if (isSlotBooked) {
            throw new ConflictException("The selected time slot is already booked.");
        }

        Booking booking = BookingMapper.toEntity(bookingDto, interviewee, availability);
        try {
            Booking savedBooking = bookingRepository.saveAndFlush(booking);
            System.out.println("Availability Version After Lock: " + availability.getVersion());
            return BookingMapper.toDto(savedBooking);
        } catch (OptimisticLockException e) {
            throw new ConflictException("The time slot was booked by another user. Please choose a different slot.");
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to save Booking due to server error.");
        }
    }



    /**
     * Update booking information with Optimistic Locking.
     * @param bookingId the ID of the booking to update.
     * @param bookingDto the booking data transfer object containing updated information.
     * @return the updated BookingDto.
     */
    @Transactional
    public BookingDto updateBooking(Long bookingId, BookingDto bookingDto) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        if (bookingDto.getBookingDate() != null) {
            booking.setBookingDate(bookingDto.getBookingDate());
        }
        if (bookingDto.getTotalPrice() != null) {
            booking.setTotalPrice(bookingDto.getTotalPrice());
        }
        if (bookingDto.getPaymentStatus() != null) {
            booking.setPaymentStatus(Booking.PaymentStatus.valueOf(bookingDto.getPaymentStatus()));
        }
        if (bookingDto.getCancellationReason() != null) {
            booking.setCancellationReason(bookingDto.getCancellationReason());
        }
        if (bookingDto.getNotes() != null) {
            booking.setNotes(bookingDto.getNotes());
        }

        try {
            Booking updatedBooking = bookingRepository.saveAndFlush(booking);
            return BookingMapper.toDto(updatedBooking);
        } catch (OptimisticLockException e) {
            throw new ConflictException("The booking was modified by another user. Please try again.");
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to update Booking due to server error.");
        }
    }

    /**
     * Find a booking by ID.
     * @param bookingId the ID of the booking to find.
     * @return the found BookingDto.
     */
    public BookingDto findBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));
        return BookingMapper.toDto(booking);
    }

    /**
     * Cancel a booking by ID.
     * @param bookingId the ID of the booking to cancel.
     * @param reason the reason for cancellation.
     * @return the updated BookingDto with status set to CANCELLED.
     */
    @Transactional
    public BookingDto cancelBooking(Long bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        booking.setPaymentStatus(Booking.PaymentStatus.REFUNDED);
        booking.setCancellationReason(reason);

        try {
            Booking updatedBooking = bookingRepository.saveAndFlush(booking);
            
            sendBookingNotification(booking.getInterviewee().getUser().getUserId(), "Booking Canceled",
                    "Your booking on " + booking.getBookingDate() + " has been canceled.");

            sendBookingNotification(booking.getAvailability().getInterviewer().getUser().getUserId(), "Booking Canceled",
                    "An interview scheduled on " + booking.getBookingDate() + " has been canceled.");
            
            List<Payment> payments = paymentRepository.findByBooking_BookingId(bookingId);

            // Process refunds for all successful payments
            payments.stream()
                    .filter(payment -> payment.getPaymentStatus() == Payment.PaymentStatus.PAID)
                    .forEach(payment -> paymentService.processRefund(payment.getPaymentId()));
            
            return BookingMapper.toDto(updatedBooking);
        } catch (OptimisticLockException e) {
            throw new ConflictException("The booking was modified by another user. Please try again.");
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to cancel Booking due to server error.");
        }
    }
    
    /**
     * Helper method to send booking-related notifications.
     *
     * @param userId  The user ID.
     * @param subject Notification subject.
     * @param message Notification body.
     */
    private void sendBookingNotification(Long userId, String subject, String message) {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setUserId(userId);
        notificationDto.setSubject(subject);
        notificationDto.setMessage(message);
        notificationDto.setType("EMAIL");
        notificationDto.setStatus("SENT");

        notificationService.createNotification(notificationDto);
    }
}
