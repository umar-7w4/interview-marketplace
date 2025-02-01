package com.mockxpert.interview_marketplace.controllers;

import com.mockxpert.interview_marketplace.dto.InterviewerDto;
import com.mockxpert.interview_marketplace.exceptions.*;
import com.mockxpert.interview_marketplace.services.InterviewerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/interviewers")
public class InterviewerController {

    @Autowired
    private InterviewerService interviewerService;

    public InterviewerController() {
        System.out.println("InterviewerController Initialized");
    }

    /**
     * Register a new interviewer.
     * @param interviewerDto the interviewer data transfer object containing registration information.
     * @return the created InterviewerDto.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerInterviewer(@RequestBody @Valid InterviewerDto interviewerDto) {
        try {
            InterviewerDto savedInterviewer = interviewerService.registerInterviewer(interviewerDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedInterviewer);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Update interviewer profile.
     * @param interviewerId the ID of the interviewer to update.
     * @param interviewerDto the interviewer data transfer object containing updated information.
     * @return the updated InterviewerDto.
     */
    @PutMapping("/{interviewerId}")
    public ResponseEntity<?> updateInterviewerProfile(@PathVariable Long interviewerId, @RequestBody @Valid InterviewerDto interviewerDto) {
        try {
            InterviewerDto updatedInterviewer = interviewerService.updateInterviewerProfile(interviewerId, interviewerDto);
            return ResponseEntity.ok(updatedInterviewer);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Find interviewer by user ID.
     * @param userId the user ID linked to the interviewer.
     * @return the InterviewerDto if found.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> findInterviewerByUserId(@PathVariable Long userId) {
        try {
            Optional<InterviewerDto> interviewer = interviewerService.findInterviewerByUserId(userId);
            return ResponseEntity.ok(interviewer);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Optional.empty());
        }
    }

    /**
     * Verify an interviewer.
     * @param interviewerId the ID of the interviewer to verify.
     * @param verified the verification status.
     * @return the updated InterviewerDto.
     */
    @PutMapping("/{interviewerId}/verify")
    public ResponseEntity<?> verifyInterviewer(@PathVariable Long interviewerId, @RequestParam boolean verified) {
        try {
            InterviewerDto verifiedInterviewer = interviewerService.verifyInterviewer(interviewerId, verified);
            return ResponseEntity.ok(verifiedInterviewer);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Deactivate an interviewer.
     * @param interviewerId the ID of the interviewer to deactivate.
     * @return the updated InterviewerDto with status set to INACTIVE.
     */
    @PutMapping("/{interviewerId}/deactivate")
    public ResponseEntity<?> deactivateInterviewer(@PathVariable Long interviewerId) {
        try {
            InterviewerDto deactivatedInterviewer = interviewerService.deactivateInterviewer(interviewerId);
            return ResponseEntity.ok(deactivatedInterviewer);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Reactivate an interviewer.
     * @param interviewerId the ID of the interviewer to reactivate.
     * @return the updated InterviewerDto with status set to ACTIVE.
     */
    @PutMapping("/{interviewerId}/reactivate")
    public ResponseEntity<?> reactivateInterviewer(@PathVariable Long interviewerId) {
        try {
            InterviewerDto reactivatedInterviewer = interviewerService.reactivateInterviewer(interviewerId);
            return ResponseEntity.ok(reactivatedInterviewer);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}