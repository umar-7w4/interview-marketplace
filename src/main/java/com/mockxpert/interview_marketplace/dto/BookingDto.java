package com.mockxpert.interview_marketplace.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.*;

/**
 * Data Transfer Object for the booking JSON object.
 * 
 * @author Umar Mohammad
 */
public class BookingDto {

    private Long bookingId;

    @NotNull(message = "Interviewee ID is required")
    private Long intervieweeId;

    @NotNull(message = "Availability ID is required")
    private Long availabilityId;

    @NotNull(message = "Booking date is required")
    private LocalDate bookingDate;

    @DecimalMin(value = "0.0", inclusive = false, message = "Total price must be greater than 0")
    private BigDecimal totalPrice;

    @NotBlank(message = "Payment status is required")
    private String paymentStatus;  // Accepts values such as "CONFIRMED", "PENDING", or "CANCELLED"

    private String cancellationReason;

    private String notes;

	public Long getBookingId() {
		return bookingId;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}

	public Long getIntervieweeId() {
		return intervieweeId;
	}

	public void setIntervieweeId(Long intervieweeId) {
		this.intervieweeId = intervieweeId;
	}

	public Long getAvailabilityId() {
		return availabilityId;
	}

	public void setAvailabilityId(Long availabilityId) {
		this.availabilityId = availabilityId;
	}

	public LocalDate getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(LocalDate bookingDate) {
		this.bookingDate = bookingDate;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getCancellationReason() {
		return cancellationReason;
	}

	public void setCancellationReason(String cancellationReason) {
		this.cancellationReason = cancellationReason;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
    
    
}
