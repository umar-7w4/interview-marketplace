package com.mockxpert.interview_marketplace.controllers;

import com.mockxpert.interview_marketplace.dto.InterviewDto;
import com.mockxpert.interview_marketplace.exceptions.*;
import com.mockxpert.interview_marketplace.services.InterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    @Autowired
    private InterviewService interviewService;

    public InterviewController() {
        System.out.println("InterviewController Initialized");
    }

    /**
     * Register a new interview.
     * @param interviewDto the interview data transfer object containing registration information.
     * @return the created InterviewDto.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerInterview(@RequestBody @Valid InterviewDto interviewDto) {
        try {
            InterviewDto savedInterview = interviewService.registerInterview(interviewDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedInterview);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Update an existing interview.
     * @param interviewId the ID of the interview to update.
     * @param interviewDto the updated interview data transfer object.
     * @return the updated InterviewDto.
     */
    @PutMapping("/{interviewId}")
    public ResponseEntity<?> updateInterview(@PathVariable Long interviewId, @RequestBody @Valid InterviewDto interviewDto) {
        try {
            InterviewDto updatedInterview = interviewService.updateInterview(interviewId, interviewDto);
            return ResponseEntity.ok(updatedInterview);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Find an interview by ID.
     * @param interviewId the ID of the interview to find.
     * @return the found InterviewDto.
     */
    @GetMapping("/{interviewId}")
    public ResponseEntity<?> findInterviewById(@PathVariable Long interviewId) {
        try {
            InterviewDto interview = interviewService.findInterviewById(interviewId);
            return ResponseEntity.ok(interview);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Cancel an interview by ID.
     * @param interviewId the ID of the interview to cancel.
     * @param reason the reason for cancellation.
     * @return the updated InterviewDto with status set to CANCELLED.
     */
    @PutMapping("/{interviewId}/cancel")
    public ResponseEntity<?> cancelInterview(@PathVariable Long interviewId, @RequestParam String reason) {
        try {
            InterviewDto cancelledInterview = interviewService.cancelInterview(interviewId, reason);
            return ResponseEntity.ok(cancelledInterview);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Get all interviews.
     * @return a list of InterviewDto objects.
     */
    @GetMapping
    public ResponseEntity<?> getAllInterviews() {
        try {
            List<InterviewDto> interviews = interviewService.getAllInterviews();
            return ResponseEntity.ok(interviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
