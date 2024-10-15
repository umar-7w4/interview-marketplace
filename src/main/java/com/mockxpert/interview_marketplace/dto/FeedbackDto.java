package com.mockxpert.interview_marketplace.dto;


import java.time.LocalDateTime;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackDto {

    private Long feedbackId;

    @NotNull(message = "Interview ID is required")
    private Long interviewId;

    @NotNull(message = "Giver ID is required")
    private Long giverId;

    @NotNull(message = "Receiver ID is required")
    private Long receiverId;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 10, message = "Rating must not exceed 10")
    private int rating;

    private String comments;

    private String positives;

    private String negatives;

    private String improvements;

    private LocalDateTime createdAt;
}
