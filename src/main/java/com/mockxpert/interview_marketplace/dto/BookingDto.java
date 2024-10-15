package com.mockxpert.interview_marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
