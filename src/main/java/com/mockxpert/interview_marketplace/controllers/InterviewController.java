package com.mockxpert.interview_marketplace.controllers;

import com.mockxpert.interview_marketplace.dto.InterviewDto;
import com.mockxpert.interview_marketplace.exceptions.*;
import com.mockxpert.interview_marketplace.services.InterviewService;
import com.mockxpert.interview_marketplace.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


/**
 * REST controller responsible for handling all the HTTP API requests related to interview operations.
 * 
 * @author Umar Mohammad
 */
@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    @Autowired
    private InterviewService interviewService;
    
    @Autowired
    private UserService userService;

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
    
    /**
     * Get scheduled interviews count for an interviewer.
     *
     * @param interviewerId the ID of the interviewer.
     * @return count of scheduled interviews.
     */
    @GetMapping("/interviewer/{userId}/count")
    public ResponseEntity<Long> getScheduledInterviewsCount(@PathVariable Long userId) {
        long count = interviewService.countScheduledInterviewsForInterviewer(userId);
        return ResponseEntity.ok(count);
    }

    /**
     * Get upcoming interviews for an interviewer.
     *
     * @param interviewerId the ID of the interviewer.
     * @return list of InterviewDto.
     */
    @GetMapping("/interviewer/{userId}/upcoming")
    public ResponseEntity<List<InterviewDto>> getUpcomingInterviewsFromInterviewer(@PathVariable Long userId) {
        List<InterviewDto> interviews = interviewService.getUpcomingInterviewsForInterviewer(userId);
        return ResponseEntity.ok(interviews);
    }
    
    /**
     *  Get upcoming interviews for an interviewee.
     * 
     * @param userId
     * @return
     */
    @GetMapping("/interviewee/{userId}/upcoming")
    public ResponseEntity<List<InterviewDto>> getUpcomingInterviewsFromInterviewee(@PathVariable Long userId) {
        List<InterviewDto> interviews = interviewService.getUpcomingInterviewsForInterviewee(userId);
        return ResponseEntity.ok(interviews);
    }
    
    /**
     * Get upcoming interviews count
     * 
     * @param userId
     * @return
     */
    @GetMapping("/interviewee/{userId}/upcoming-count")
    public ResponseEntity<Long> getUpcomingInterviewCount(@PathVariable Long userId) {
        Long count = interviewService.getUpcomingInterviewCount(userId);
        return ResponseEntity.ok(count);
    }

    /**
     * Get completed interviews count
     * 
     * @param userId
     * @return
     */
    @GetMapping("/interviewee/{userId}/completed-count")
    public ResponseEntity<Long> getCompletedInterviewCount(@PathVariable Long userId) {
        Long count = interviewService.getCompletedInterviewCount(userId);
        return ResponseEntity.ok(count);
    }

    /**
     * Get list of completed interviews
     * 
     * @param userId
     * @return
     */
    @GetMapping("/interviewee/{userId}/completed")
    public ResponseEntity<List<InterviewDto>> getCompletedInterviews(@PathVariable Long userId) {
        List<InterviewDto> interviews = interviewService.getCompletedInterviews(userId);
        return ResponseEntity.ok(interviews);
    }
    
    /**
     * Get all interviews for a single date (YYYY-MM-DD).
     * 
     * @param date
     * @return
     */
    @GetMapping("/by-date")
    public ResponseEntity<?> getInterviewsByDate(
            @RequestParam String date,
            @RequestParam Long userId
    ) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            List<InterviewDto> interviews =
                interviewService.getInterviewsByDateForUser(localDate, userId);
            return ResponseEntity.ok(interviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body("Invalid date format or server error: " + e.getMessage());
        }
    }

    /**
     * 
     * Get all interviews within a date range (e.g. for a week).
     * Example: /by-week?start=2025-02-05&end=2025-02-11
     * 
     * @param start
     * @param end
     * @return
     */
    @GetMapping("/by-week")
    public ResponseEntity<?> getInterviewsByWeek(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam Long userId
    ) {
        try {
            LocalDate startDate = LocalDate.parse(start);
            LocalDate endDate = LocalDate.parse(end);
            List<InterviewDto> interviews =
                interviewService.getInterviewsInRangeForUser(startDate, endDate, userId);
            return ResponseEntity.ok(interviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body("Invalid date range or server error: " + e.getMessage());
        }
    }
    
    /**
     * Get all interviews for a specific month.
     * e.g. /by-month?year=2025&month=2 => All interviews in Feb 2025
     * 
     * @param year
     * @param month
     * @return
     */
    @GetMapping("/by-month")
    public ResponseEntity<?> getInterviewsByMonth(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam Long userId
    ) {
        try {
            List<InterviewDto> interviews =
                interviewService.getInterviewsByMonthForUser(year, month, userId);
            return ResponseEntity.ok(interviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body("Could not fetch interviews for month: " + e.getMessage());
        }
    }
    /**
     * Reschedule an interview (send new date/time in the interviewDto).
     * 
     * @param interviewId
     * @param interviewDto
     * @return
     */
    @PutMapping("/{interviewId}/reschedule")
    public ResponseEntity<?> rescheduleInterview(
            @PathVariable Long interviewId,
            @RequestBody @Valid InterviewDto interviewDto
    ) {
        try {
            InterviewDto updated = interviewService.rescheduleInterview(interviewId, interviewDto);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reschedule interview: " + e.getMessage());
        }
    }

    /**
     * Cancel an interview by ID.
     * @param interviewId the ID of the interview to cancel.
     * @param reason the reason for cancellation.
     * @return the updated InterviewDto with status set to CANCELLED.
     */
    @PutMapping("/{interviewId}/cancel")
    public ResponseEntity<?> cancelInterview(
            @PathVariable Long interviewId,
            @RequestBody @Valid InterviewDto interviewDto
    ) {
        try {
            InterviewDto cancelled = interviewService.cancelInterview(interviewId, "Cancel from interviewDto");
            return ResponseEntity.ok(cancelled);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * 
     * Get all past interviews for an interviewee with dynamic filters.
     * 
     * @param startDate
     * @param endDate
     * @param status
     * @param timezone
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping("/past-sessions")
    public ResponseEntity<?> getPastInterviewsWithFilters(
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String timezone,
        @RequestParam(required = false) String startTime,
        @RequestParam(required = false) String endTime
    ) {
        try {
            Long dbUserId = userService.getCurrentUser().getUserId(); 
            // or intervieweeRepository.findByUid(firebaseUid)...

            // 2) parse optional date/time
            LocalDate sDate = (startDate != null && !startDate.isBlank()) ? LocalDate.parse(startDate) : null;
            LocalDate eDate = (endDate != null && !endDate.isBlank()) ? LocalDate.parse(endDate) : null;
            LocalTime filterStartTime = (startTime != null && !startTime.isBlank()) ? LocalTime.parse(startTime) : null;
            LocalTime filterEndTime = (endTime != null && !endTime.isBlank()) ? LocalTime.parse(endTime) : null;

            // 3) call service
            List<InterviewDto> pastSessions = interviewService.getPastSessionsWithFiltersForCurrentUser(
                dbUserId,
                sDate,
                eDate,
                status,
                timezone,
                filterStartTime,
                filterEndTime
            );
            return ResponseEntity.ok(pastSessions);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Failed to fetch past sessions: " + e.getMessage());
        }
    }

}
