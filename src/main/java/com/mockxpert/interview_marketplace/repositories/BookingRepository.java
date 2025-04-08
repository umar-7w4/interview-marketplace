package com.mockxpert.interview_marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.mockxpert.interview_marketplace.entities.Availability;
import com.mockxpert.interview_marketplace.entities.Booking;

import jakarta.persistence.LockModeType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 
 * Repository class thats reposible generating query methods related to booking.
 * 
 * @author Umar Mohammad
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
	
    /**
     * Checks if the availability exists or not.
     * 
     * @param availability entity object.
     * @return a boolean value that tells if availability exists or not.
     */
    boolean existsByAvailability(Availability availability);

    /**
     * Find booking associated with the booking id.
     * 
     * @param booking id is the ID of the Booking.
     * @return an optional booking for the specified booking ID.
     */

    Optional<Booking> findById(Long bookingId);

    /**
     * Find all bookings for a specific interviewee.
     * 
     * @param intervieweeId the ID of the interviewee.
     * @return a list of bookings associated with the interviewee.
     */
    List<Booking> findByInterviewee_IntervieweeId(Long intervieweeId);

    /**
     * Find all bookings for a specific availability.
     * 
     * @param availabilityId the ID of the availability.
     * @return a list of bookings associated with the availability.
     */
    List<Booking> findByAvailability_AvailabilityId(Long availabilityId);

    /**
     * Find all bookings on a specific date.
     * 
     * @param bookingDate the date on which the bookings were made.
     * @return a list of bookings created on the specified date.
     */
    List<Booking> findByBookingDate(LocalDate bookingDate);

    /**
     * Find bookings by payment status.
     * 
     * @param paymentStatus the payment status (e.g., confirmed, pending, cancelled).
     * @return a list of bookings with the specified payment status.
     */
    List<Booking> findByPaymentStatus(String paymentStatus);

    /**
     * Find bookings by cancellation reason containing a keyword.
     * 
     * @param keyword the keyword to search in the cancellation reason.
     * @return a list of bookings with the cancellation reason containing the given keyword.
     */
    List<Booking> findByCancellationReasonContaining(String keyword);

    /**
     * Find bookings by notes containing specific keywords.
     * 
     * @param notesKeyword the keyword to search in the notes.
     * @return a list of bookings with notes containing the specified keyword.
     */
    List<Booking> findByNotesContaining(String notesKeyword);

    /**
     * Count bookings by payment status.
     * 
     * @param paymentStatus the payment status to filter bookings.
     * @return the count of bookings with the given payment status.
     */
    Long countByPaymentStatus(String paymentStatus);

    /**
     * Find all bookings associated with a specific interview.
     * 
     * @param interviewId the ID of the interview.
     * @return an optional booking for the specified interview ID.
     */
    Optional<Booking> findByInterview_InterviewId(Long interviewId);

    /**
     * Find all bookings between two dates.
     * 
     * @param startDate the start date of the range.
     * @param endDate the end date of the range.
     * @return a list of bookings made within the specified date range.
     */
    List<Booking> findByBookingDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find all confirmed bookings for a specific interviewee.
     * 
     * @param intervieweeId the ID of the interviewee.
     * @param paymentStatus the payment status (e.g., confirmed).
     * @return a list of confirmed bookings for the interviewee.
     */
    List<Booking> findByInterviewee_IntervieweeIdAndPaymentStatus(Long intervieweeId, Booking.PaymentStatus paymentStatus);

    /**
     * Find all bookings with a specific total price.
     * 
     * @param totalPrice the total price of the booking.
     * @return a list of bookings with the specified total price.
     */
    List<Booking> findByTotalPrice(Double totalPrice);
}
