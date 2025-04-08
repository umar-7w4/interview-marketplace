package com.mockxpert.interview_marketplace.controllers;

import com.mockxpert.interview_marketplace.dto.IntervieweeDto;
import com.mockxpert.interview_marketplace.exceptions.*;
import com.mockxpert.interview_marketplace.services.IntervieweeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

/**
 * 
 * REST controller responsible for handling all the HTTP API requests related to interviewee operations.
 * 
 * @author Umar Mohammad
 */
@RestController
@RequestMapping("/api/interviewees")
public class IntervieweeController {

    @Autowired
    private IntervieweeService intervieweeService;

    public IntervieweeController() {
        System.out.println("IntervieweeController Initialized");
    }

    /**
     * Register a new interviewee.
     * @param intervieweeDto the interviewee data transfer object containing registration information.
     * @return the created IntervieweeDto.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerInterviewee(@RequestBody @Valid IntervieweeDto intervieweeDto) {
        try {
            IntervieweeDto savedInterviewee = intervieweeService.registerInterviewee(intervieweeDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedInterviewee);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
        	System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Update interviewee profile.
     * @param intervieweeId the ID of the interviewee to update.
     * @param intervieweeDto the interviewee data transfer object containing updated information.
     * @return the updated IntervieweeDto.
     */
    @PutMapping("/{intervieweeId}")
    public ResponseEntity<?> updateIntervieweeProfile(@PathVariable Long intervieweeId, @RequestBody @Valid IntervieweeDto intervieweeDto) {
        try {
            IntervieweeDto updatedInterviewee = intervieweeService.updateIntervieweeProfile(intervieweeId, intervieweeDto);
            return ResponseEntity.ok(updatedInterviewee);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Find interviewee by user ID.
     * @param userId the user ID linked to the interviewee.
     * @return the IntervieweeDto if found.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> findIntervieweeByUserId(@PathVariable Long userId) {
        try {
            Optional<IntervieweeDto> interviewee = intervieweeService.findIntervieweeByUserId(userId);
            return ResponseEntity.ok(interviewee);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Optional.empty());
        }
    }
    
    /**
     * checks existence of interviewee
     * 
     * @param userId
     * @return
     */
    @GetMapping("/user/{userId}/exists")
    public ResponseEntity<Boolean> doesIntervieweeProfileExist(@PathVariable Long userId) {
        boolean exists = intervieweeService.checkExistenceOfInterviewee(userId);
        return ResponseEntity.ok(exists);
    }

    /**
     * Deactivate an interviewee.
     * @param intervieweeId the ID of the interviewee to deactivate.
     * @return a response indicating success.
     */
    @PutMapping("/{intervieweeId}/deactivate")
    public ResponseEntity<String> deactivateInterviewee(@PathVariable Long intervieweeId) {
        try {
            boolean success = intervieweeService.deactivateInterviewee(intervieweeId);
            return ResponseEntity.ok("Interviewee deactivated successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to deactivate interviewee");
        }
    }

    /**
     * Reactivate an interviewee.
     * @param intervieweeId the ID of the interviewee to reactivate.
     * @return a response indicating success.
     */
    @PutMapping("/{intervieweeId}/reactivate")
    public ResponseEntity<String> reactivateInterviewee(@PathVariable Long intervieweeId) {
        try {
            boolean success = intervieweeService.reactivateInterviewee(intervieweeId);
            return ResponseEntity.ok("Interviewee reactivated successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reactivate interviewee");
        }
    }
   
}