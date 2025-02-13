package com.mockxpert.interview_marketplace.controllers;

import com.mockxpert.interview_marketplace.dto.AvailabilityDto;
import com.mockxpert.interview_marketplace.exceptions.*;
import com.mockxpert.interview_marketplace.services.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST controller responsible for handling all the HTTP API requests related to availability operations.
 * 
 * @author Umar Mohammad
 */
@RestController
@RequestMapping("/api/availabilities")
public class AvailabilityController {

    @Autowired
    private AvailabilityService availabilityService;

    public AvailabilityController() {
        System.out.println("AvailabilityController Initialized");
    }

    /**
     * Register a new availability.
     * @param availabilityDto the availability data transfer object containing registration information.
     * @return the created AvailabilityDto.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerAvailability(@RequestBody @Valid AvailabilityDto availabilityDto) {
        try {
            AvailabilityDto savedAvailability = availabilityService.registerAvailability(availabilityDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAvailability);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ConflictException e) {  
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Update availability information.
     * @param availabilityId the ID of the availability to update.
     * @param availabilityDto the availability data transfer object containing updated information.
     * @return the updated AvailabilityDto.
     */
    @PutMapping("/{availabilityId}")
    public ResponseEntity<?> updateAvailability(@PathVariable Long availabilityId, @RequestBody @Valid AvailabilityDto availabilityDto) {
        try {
            AvailabilityDto updatedAvailability = availabilityService.updateAvailability(availabilityId, availabilityDto);
            return ResponseEntity.ok(updatedAvailability);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Find availability by ID.
     * @param availabilityId the ID of the availability to find.
     * @return the found AvailabilityDto.
     */
    @GetMapping("/{availabilityId}")
    public ResponseEntity<?> findAvailabilityById(@PathVariable Long availabilityId) {
        try {
            AvailabilityDto availability = availabilityService.findAvailabilityById(availabilityId);
            return ResponseEntity.ok(availability);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Delete availability by ID.
     * @param availabilityId the ID of the availability to delete.
     * @return a response indicating success.
     */
    @DeleteMapping("/{availabilityId}")
    public ResponseEntity<String> deleteAvailability(@PathVariable Long availabilityId) {
        try {
            boolean deleted = availabilityService.deleteAvailability(availabilityId);
            if (deleted) {
                return ResponseEntity.ok("Availability deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete availability");
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Get a list of all availabilities.
     * @return a list of all AvailabilityDto objects.
     */
    @GetMapping
    public ResponseEntity<?> findAllAvailabilities() {
        try {
            List<AvailabilityDto> availabilities = availabilityService.findAllAvailabilities();
            return ResponseEntity.ok(availabilities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
