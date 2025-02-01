package com.mockxpert.interview_marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mockxpert.interview_marketplace.entities.Payment;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find payments by booking ID.
     * @param bookingId the ID of the booking.
     * @return a list of payments associated with the given booking ID.
     */
    List<Payment> findByBooking_BookingId(Long bookingId);

    /**
     * Find payments by transaction ID.
     * @param transactionId the transaction ID from the payment gateway.
     * @return the payment associated with the given transaction ID.
     */
    Payment findByTransactionId(String transactionId);

    /**
     * Find payments by payment date.
     * @param paymentDate the date when the payment was made.
     * @return a list of payments made on the specified date.
     */
    List<Payment> findByPaymentDate(LocalDate paymentDate);

    /**
     * Find payments by payment status.
     * @param paymentStatus the status of the payment (e.g., paid, failed, refunded).
     * @return a list of payments with the specified status.
     */
    List<Payment> findByPaymentStatus(String paymentStatus);

    /**
     * Find payments by currency type.
     * @param currency the currency used for the payment (e.g., USD, EUR).
     * @return a list of payments made in the specified currency.
     */
    List<Payment> findByCurrency(String currency);

    /**
     * Find payments by payment method.
     * @param paymentMethod the method used for the payment (e.g., credit card, PayPal).
     * @return a list of payments made using the specified method.
     */
    List<Payment> findByPaymentMethod(String paymentMethod);

    /**
     * Count payments by status.
     * @param paymentStatus the status of the payment.
     * @return the count of payments with the given status.
     */
    Long countByPaymentStatus(String paymentStatus);

    /**
     * Find payments made within a specific date range.
     * @param startDate the start date of the range.
     * @param endDate the end date of the range.
     * @return a list of payments made within the specified date range.
     */
    List<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find payments by booking ID and payment status.
     * @param bookingId the ID of the booking.
     * @param paymentStatus the status of the payment.
     * @return a list of payments for the specified booking ID and status.
     */
    List<Payment> findByBooking_BookingIdAndPaymentStatus(Long bookingId, String paymentStatus);

    /**
     * Find payments by refund amount greater than a specified value.
     * @param refundAmount the minimum refund amount.
     * @return a list of payments where the refund amount is greater than the specified value.
     */
    List<Payment> findByRefundAmountGreaterThan(Double refundAmount);
}
