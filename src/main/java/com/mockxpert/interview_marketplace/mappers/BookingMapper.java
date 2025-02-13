package com.mockxpert.interview_marketplace.mappers;

import com.mockxpert.interview_marketplace.dto.BookingDto;
import com.mockxpert.interview_marketplace.entities.Availability;
import com.mockxpert.interview_marketplace.entities.Booking;
import com.mockxpert.interview_marketplace.entities.Interviewee;

/**
 * Mapper class that converts Data Transfer Object to booking entity object.
 * 
 * @author Umar Mohammad
 */

public class BookingMapper {
	
	/**
	 * Booking dto to entity. 
	 * 
	 * @param booking
	 * @return
	 */

    public static BookingDto toDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingDto dto = new BookingDto();
        dto.setBookingId(booking.getBookingId());
        dto.setIntervieweeId(booking.getInterviewee().getIntervieweeId());
        dto.setAvailabilityId(booking.getAvailability().getAvailabilityId());
        dto.setBookingDate(booking.getBookingDate());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setPaymentStatus(booking.getPaymentStatus() != null ? booking.getPaymentStatus().name() : null);
        dto.setCancellationReason(booking.getCancellationReason());
        dto.setNotes(booking.getNotes());

        return dto;
    }
    
    /**
     * Booking entity to dto. 
     *
     * @param dto
     * @param interviewee
     * @param availability
     * @return
     */

    public static Booking toEntity(BookingDto dto, Interviewee interviewee, Availability availability) {
        if (dto == null || interviewee == null || availability == null) {
            return null;
        }

        Booking booking = new Booking();
        booking.setInterviewee(interviewee);
        booking.setAvailability(availability);
        booking.setBookingDate(dto.getBookingDate());
        booking.setTotalPrice(dto.getTotalPrice());
        booking.setPaymentStatus(dto.getPaymentStatus() != null ? Booking.PaymentStatus.valueOf(dto.getPaymentStatus()) : null);
        booking.setCancellationReason(dto.getCancellationReason());
        booking.setNotes(dto.getNotes());

        return booking;
    }
}
