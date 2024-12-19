package com.mockxpert.interview_marketplace.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.*;

public class NotificationDto {

    private Long notificationId;

    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Booking ID is required")
    private Long bookingId;
    
    @NotNull(message = "Interview ID is required")
    private Long intervieweeId;

    @NotBlank(message = "Related entity type is required")
    private String relatedEntityType; 

    @NotNull(message = "Related entity ID is required")
    private Long relatedEntityId;

    @NotBlank(message = "Message content is required")
    private String message;

    @NotBlank(message = "Notification type is required")
    private String type; 

    @NotBlank(message = "Notification status is required")
    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime scheduledSendTime;

    private boolean isRead;

    private Long timeBeforeInterview;

	public Long getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(Long notificationId) {
		this.notificationId = notificationId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getBookingId() {
		return bookingId;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}

	public Long getIntervieweeId() {
		return intervieweeId;
	}

	public void setIntervieweeId(Long intervieweeId) {
		this.intervieweeId = intervieweeId;
	}

	public String getRelatedEntityType() {
		return relatedEntityType;
	}

	public void setRelatedEntityType(String relatedEntityType) {
		this.relatedEntityType = relatedEntityType;
	}

	public Long getRelatedEntityId() {
		return relatedEntityId;
	}

	public void setRelatedEntityId(Long relatedEntityId) {
		this.relatedEntityId = relatedEntityId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getScheduledSendTime() {
		return scheduledSendTime;
	}

	public void setScheduledSendTime(LocalDateTime scheduledSendTime) {
		this.scheduledSendTime = scheduledSendTime;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public Long getTimeBeforeInterview() {
		return timeBeforeInterview;
	}

	public void setTimeBeforeInterview(Long timeBeforeInterview) {
		this.timeBeforeInterview = timeBeforeInterview;
	}
}
