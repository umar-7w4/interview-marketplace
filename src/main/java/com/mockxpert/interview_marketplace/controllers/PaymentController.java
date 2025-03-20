package com.mockxpert.interview_marketplace.controllers;

import com.mockxpert.interview_marketplace.dto.PaymentDto;
import com.mockxpert.interview_marketplace.exceptions.ResourceNotFoundException;
import com.mockxpert.interview_marketplace.services.PaymentService;
import com.mockxpert.interview_marketplace.services.StripePaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.List;


/**
 * Rest controller responsible for handling all the HTTP API requests related to payment operations.
 * 
 * @author Umar Mohammad
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private StripePaymentService stripePaymentService;

    public PaymentController() {
        logger.info("PaymentController Initialized");
    }

    /**
     * Create a new Stripe checkout session for a booking.
     * @param bookingId the ID of the booking.
     * @param amount the amount to be paid.
     * @return Stripe checkout session URL.
     */
    @PostMapping("/create-checkout-session")
    public ResponseEntity<?> createCheckoutSession(@RequestParam Long bookingId, @RequestParam double amount) {
        try {
            String checkoutUrl = stripePaymentService.createCheckoutSession(bookingId, amount);
            logger.info("Stripe Checkout initiated for Booking ID: {} | Amount: {}", bookingId, amount);
            return ResponseEntity.ok(checkoutUrl);
        } catch (Exception e) {
            logger.error("Failed to create Stripe checkout session for Booking ID: {}", bookingId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    } 

    /**
     * Handles successful payment and processes the interview creation.
     * @param sessionId the Stripe session ID for the transaction.
     * @return Confirmation message.
     */
    @GetMapping("/success")
    public ResponseEntity<?> paymentSuccess(@RequestParam("session_id") String sessionId) {
        try {
            logger.info("Hitting the payment success");

            // Process the payment and schedule the interview
            PaymentDto payment = paymentService.processSuccessfulPayment(sessionId);

            logger.info("Payment successful. Transaction ID: {} | Booking ID: {}", 
                        payment.getTransactionId(), payment.getBookingId());

            return ResponseEntity.ok("Payment Successful! Your interview has been scheduled.");

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Payment processing failed for Session ID: {}", sessionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Payment successful but interview scheduling failed.");
        }
    }


    /**
     * Retrieve a payment by ID.
     * @param paymentId the ID of the payment to retrieve.
     * @return the PaymentDto.
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long paymentId) {
        try {
            PaymentDto payment = paymentService.getPaymentById(paymentId);
            return ResponseEntity.ok(payment);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Update an existing payment.
     * @param paymentId the ID of the payment to update.
     * @param paymentDto the updated payment data transfer object.
     * @return the updated PaymentDto.
     */
    @PutMapping("/{paymentId}")
    public ResponseEntity<?> updatePayment(@PathVariable Long paymentId, @RequestBody @Valid PaymentDto paymentDto) {
        try {
            PaymentDto updatedPayment = paymentService.updatePayment(paymentId, paymentDto);
            return ResponseEntity.ok(updatedPayment);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Delete a payment by ID.
     * @param paymentId the ID of the payment to delete.
     * @return a response indicating success.
     */
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<String> deletePayment(@PathVariable Long paymentId) {
        try {
            paymentService.deletePayment(paymentId);
            return ResponseEntity.ok("Payment deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete payment");
        }
    }

    /**
     * Get a list of all payments.
     * @return a list of PaymentDto objects.
     */
    @GetMapping
    public ResponseEntity<?> getAllPayments() {
        try {
            List<PaymentDto> payments = paymentService.getAllPayments();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Get total earnings for an interviewer.
     *
     * @param interviewerId the ID of the interviewer.
     * @return total earnings amount.
     */
    @GetMapping("/interviewer/{userId}/earnings")
    public ResponseEntity<?> getTotalEarnings(@PathVariable Long userId) {
        BigDecimal totalEarnings = paymentService.getTotalEarningsForInterviewer(userId);
        return ResponseEntity.ok(totalEarnings);
    }
    
    /**
     * Fetch all payment transactions for a specific user.
     * The user may be either the payer (interviewee) or the receiver (interviewer).
     *
     * @param userId the ID of the user.
     * @return List of PaymentDto objects.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentDto>> getUserPayments(@PathVariable Long userId) {
        List<PaymentDto> payments = paymentService.getPaymentsForUser(userId);
        return ResponseEntity.ok(payments);
    }
    
    // Get total money spent by interviewee
    @GetMapping("/interviewee/{userId}/total-spent")
    public ResponseEntity<BigDecimal> getTotalSpentByInterviewee(@PathVariable Long userId) {
        BigDecimal totalSpent = paymentService.getTotalSpentByInterviewee(userId);
        return ResponseEntity.ok(totalSpent);
    }
}
