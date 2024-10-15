package com.mockxpert.interview_marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {

    private Long notificationId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Related entity type is required")
    private String relatedEntityType;  // Accepts "BOOKING", "INTERVIEW", or "PAYMENT"

    @NotNull(message = "Related entity ID is required")
    private Long relatedEntityId;

    @NotBlank(message = "Message content is required")
    private String message;

    @NotBlank(message = "Notification type is required")
    private String type;  // Accepts "EMAIL", "SMS", "APP_NOTIFICATION"

    @NotBlank(message = "Notification status is required")
    private String status;  // Accepts "PENDING", "SENT", "FAILED"

    private LocalDateTime createdAt;

    private LocalDateTime scheduledSendTime;

    private boolean isRead;

    private Long timeBeforeInterview;
}
