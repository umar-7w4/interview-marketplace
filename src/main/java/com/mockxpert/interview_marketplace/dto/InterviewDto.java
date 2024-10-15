package com.mockxpert.interview_marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.validation.constraints.*;

import java.time.Duration;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewDto {

    private Long interviewId;

    @NotNull(message = "Interviewee ID is required")
    private Long intervieweeId;

    @NotNull(message = "Interviewer ID is required")
    private Long interviewerId;

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "Duration is required")
    private Duration duration;

    @NotBlank(message = "Interview link is required")
    private String interviewLink;

    private String status;  // Represented as String for flexibility in DTO

    private String timezone;

    private LocalTime endTime;

    private LocalDateTime actualStartTime;

    private LocalDateTime actualEndTime;
}
