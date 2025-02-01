package com.mockxpert.interview_marketplace.entities;


import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id", nullable = false)
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "related_entity_type", nullable = false)
    private String relatedEntityType;  // Could be an enum for "BOOKING", "INTERVIEW", or "PAYMENT"

    @Column(name = "related_entity_id", nullable = false)
    private Long relatedEntityId;

    @Column(nullable = false, length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;  

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status; 
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "scheduled_send_time")
    private LocalDateTime scheduledSendTime;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(name = "time_before_interview")
    private Long timeBeforeInterview; 
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewee_id")
    private Interview interview;
    
    public enum NotificationType {
        EMAIL,
        SMS,
        APP_NOTIFICATION
    }

    public enum NotificationStatus {
        PENDING,
        SENT,
        FAILED
    }

	public Long getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(Long notificationId) {
		this.notificationId = notificationId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	public NotificationStatus getStatus() {
		return status;
	}

	public void setStatus(NotificationStatus status) {
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

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

	public Interview getInterview() {
		return interview;
	}

	public void setInterview(Interview interview) {
		this.interview = interview;
	}
    
    
}
