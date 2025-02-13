package com.mockxpert.interview_marketplace.controllers;

import com.mockxpert.interview_marketplace.dto.BookingDto;
import com.mockxpert.interview_marketplace.exceptions.*;
import com.mockxpert.interview_marketplace.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 
 * REST controller responsible for handling all the HTTP API requests related to bookings operations.
 * 
 * @author Umar Mohammad
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    public BookingController() {
        System.out.println("BookingController Initialized");
    }

    /**
     * Register a new booking.
     * @param bookingDto the booking data transfer object containing registration information.
     * @return the created BookingDto.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerBooking(@RequestBody @Valid BookingDto bookingDto) {
        try {
            BookingDto savedBooking = bookingService.registerBooking(bookingDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Update booking information.
     * @param bookingId the ID of the booking to update.
     * @param bookingDto the booking data transfer object containing updated information.
     * @return the updated BookingDto.
     */
    @PutMapping("/{bookingId}")
    public ResponseEntity<?> updateBooking(@PathVariable Long bookingId, @RequestBody @Valid BookingDto bookingDto) {
        try {
            BookingDto updatedBooking = bookingService.updateBooking(bookingId, bookingDto);
            return ResponseEntity.ok(updatedBooking);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Find a booking by ID.
     * @param bookingId the ID of the booking to find.
     * @return the found BookingDto.
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<?> findBookingById(@PathVariable Long bookingId) {
        try {
            BookingDto booking = bookingService.findBookingById(bookingId);
            return ResponseEntity.ok(booking);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Cancel a booking by ID.
     * @param bookingId the ID of the booking to cancel.
     * @param reason the reason for cancellation.
     * @return the updated BookingDto with status set to CANCELLED.
     */
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId, @RequestParam String reason) {
        try {
            BookingDto cancelledBooking = bookingService.cancelBooking(bookingId, reason);
            return ResponseEntity.ok(cancelledBooking);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
