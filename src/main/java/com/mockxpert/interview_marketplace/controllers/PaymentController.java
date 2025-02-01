package com.mockxpert.interview_marketplace.controllers;

import com.mockxpert.interview_marketplace.dto.PaymentDto;
import com.mockxpert.interview_marketplace.exceptions.*;
import com.mockxpert.interview_marketplace.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    public PaymentController() {
        System.out.println("PaymentController Initialized");
    }

    /**
     * Create a new payment.
     * @param paymentDto the payment data transfer object containing information.
     * @return the created PaymentDto.
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody @Valid PaymentDto paymentDto) {
        try {
            PaymentDto savedPayment = paymentService.createPayment(paymentDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPayment);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
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
}