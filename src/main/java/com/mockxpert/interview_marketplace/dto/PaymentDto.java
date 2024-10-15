package com.mockxpert.interview_marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {

    private Long paymentId;

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    @NotNull(message = "Payment date is required")
    private LocalDateTime paymentDate;

    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String receiptUrl;

    private BigDecimal refundAmount;

    @NotBlank(message = "Payment status is required")
    private String paymentStatus;  // Accepts values such as "PAID", "FAILED", "REFUNDED"
}
