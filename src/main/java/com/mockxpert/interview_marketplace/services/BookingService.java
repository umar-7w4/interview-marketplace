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

/**
 * Service for managing bookings of the interviewees.
 * 
 * @author Umar Mohammad
 */
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
     * 
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
        entityManager.lock(availability, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

        boolean isSlotBooked = bookingRepository.existsByAvailability(availability);
        if (isSlotBooked) {
            throw new ConflictException("The selected time slot is already booked.");
        }

        Booking booking = BookingMapper.toEntity(bookingDto, interviewee, availability);
        try {
            Booking savedBooking = bookingRepository.saveAndFlush(booking);
            System.out.println("Availability Version After Lock: " + availability.getVersion());

            String bookingDate = savedBooking.getBookingDate().toString(); 
            String intervieweeName = interviewee.getUser().getFullName();
            String interviewerName = availability.getInterviewer().getUser().getFullName();

            String subjectInterviewee = String.format("Booking Confirmed: %s with %s", bookingDate, interviewerName);
            String messageInterviewee = String.format("Dear %s, your interview booking with %s on %s has been confirmed.",
                                                      intervieweeName, interviewerName, bookingDate);
            sendBookingNotification(interviewee.getUser().getUserId(), subjectInterviewee, messageInterviewee);

            String subjectInterviewer = String.format("New Booking Received: %s with %s", bookingDate, intervieweeName);
            String messageInterviewer = String.format("Dear %s, you have received a new booking for an interview with %s on %s.",
                                                      interviewerName, intervieweeName, bookingDate);
            sendBookingNotification(availability.getInterviewer().getUser().getUserId(), subjectInterviewer, messageInterviewer);

            return BookingMapper.toDto(savedBooking);
        } catch (OptimisticLockException e) {
            throw new ConflictException("The time slot was booked by another user. Please choose a different slot.");
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to save Booking due to server error.");
        }
    }

    /**
     * Update booking information with Optimistic Locking.
     * 
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

            String bookingDate = updatedBooking.getBookingDate().toString();
            String subject = String.format("Booking Updated: %s", bookingDate);
            String message = String.format("Your booking scheduled on %s has been updated. Please review the changes.", bookingDate);
            sendBookingNotification(updatedBooking.getInterviewee().getUser().getUserId(), subject, message);

            return BookingMapper.toDto(updatedBooking);
        } catch (OptimisticLockException e) {
            throw new ConflictException("The booking was modified by another user. Please try again.");
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to update Booking due to server error.");
        }
    }

    /**
     * 
     * Find a booking by ID.
     * 
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
     * 
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
            String bookingDate = updatedBooking.getBookingDate().toString();

            String subjectInterviewee = String.format("Booking Canceled: %s", bookingDate);
            String messageInterviewee = String.format("Dear %s, your booking on %s has been canceled. Reason: %s",
                                                      updatedBooking.getInterviewee().getUser().getFullName(), bookingDate, reason);
            sendBookingNotification(updatedBooking.getInterviewee().getUser().getUserId(), subjectInterviewee, messageInterviewee);

            String subjectInterviewer = String.format("Booking Canceled: %s", bookingDate);
            String messageInterviewer = String.format("Dear %s, the booking scheduled on %s has been canceled.",
                                                      updatedBooking.getAvailability().getInterviewer().getUser().getFullName(), bookingDate);
            sendBookingNotification(updatedBooking.getAvailability().getInterviewer().getUser().getUserId(), subjectInterviewer, messageInterviewer);

            List<Payment> payments = paymentRepository.findByBooking_BookingId(bookingId);
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
     * Helper method to send booking-related notifications using a beautiful HTML email template.
     *
     * @param userId        The user ID.
     * @param subject       Notification subject.
     * @param plainMessage  Notification body (plain text).
     */
    private void sendBookingNotification(Long userId, String subject, String plainMessage) {
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
     * Helper method to build a beautiful HTML email template using the MockXpert theme.
     *
     * @param headerTitle The header title (includes dynamic details such as dates or names).
     * @param content     The main content/body of the email.
     * @return A complete HTML string.
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
                            "<p style=\"margin: 0; font-size: 16px; line-height: 1.5;\">Dear User,</p>" +
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

}
