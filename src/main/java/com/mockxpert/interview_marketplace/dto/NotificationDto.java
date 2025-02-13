package com.mockxpert.interview_marketplace.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.*;


/**
 * Data Transfer Object for the notification JSON object.
 * 
 * @author Umar Mohammad
 */

public class NotificationDto {

    private Long notificationId;

    @NotNull(message = "User ID is required")
    private Long userId;

    private Long bookingId;

    private Long interviewId;

    private Long paymentId;

    private Long feedbackId;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Message content is required")
    private String message;

    @NotBlank(message = "Notification type is required")
    private String type;

    @NotBlank(message = "Notification status is required")
    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime sentAt;

    private LocalDateTime readAt;

    private LocalDateTime scheduledSendTime;

    private boolean isRead;

    private Long timeBeforeInterview;

    // Getters and Setters
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

    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
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
