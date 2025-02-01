package com.mockxpert.interview_marketplace.controllers;

import com.mockxpert.interview_marketplace.dto.FeedbackDto;
import com.mockxpert.interview_marketplace.exceptions.*;
import com.mockxpert.interview_marketplace.services.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    public FeedbackController() {
        System.out.println("FeedbackController Initialized");
    }

    /**
     * Register new feedback.
     * @param feedbackDto the feedback data transfer object containing information.
     * @return the created FeedbackDto.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerFeedback(@RequestBody @Valid FeedbackDto feedbackDto) {
        try {
            FeedbackDto savedFeedback = feedbackService.registerFeedback(feedbackDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedFeedback);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Retrieve feedback by ID.
     * @param feedbackId the ID of the feedback to retrieve.
     * @return the FeedbackDto.
     */
    @GetMapping("/{feedbackId}")
    public ResponseEntity<?> getFeedbackById(@PathVariable Long feedbackId) {
        try {
            FeedbackDto feedback = feedbackService.findFeedbackById(feedbackId);
            return ResponseEntity.ok(feedback);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Update an existing feedback.
     * @param feedbackId the ID of the feedback to update.
     * @param feedbackDto the updated feedback data transfer object.
     * @return the updated FeedbackDto.
     */
    @PutMapping("/{feedbackId}")
    public ResponseEntity<?> updateFeedback(@PathVariable Long feedbackId, @RequestBody @Valid FeedbackDto feedbackDto) {
        try {
            FeedbackDto updatedFeedback = feedbackService.updateFeedback(feedbackId, feedbackDto);
            return ResponseEntity.ok(updatedFeedback);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Delete feedback by ID.
     * @param feedbackId the ID of the feedback to delete.
     * @return a response indicating success.
     */
    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<String> deleteFeedback(@PathVariable Long feedbackId) {
        try {
            boolean deleted = feedbackService.deleteFeedback(feedbackId);
            if (deleted) {
                return ResponseEntity.ok("Feedback deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete feedback");
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Retrieve all feedback.
     * @return a list of FeedbackDto objects.
     */
    @GetMapping
    public ResponseEntity<?> getAllFeedback() {
        try {
            List<FeedbackDto> feedbackList = feedbackService.findAllFeedbacks();
            return ResponseEntity.ok(feedbackList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}