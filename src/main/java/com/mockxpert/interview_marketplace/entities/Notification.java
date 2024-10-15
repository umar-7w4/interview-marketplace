package com.mockxpert.interview_marketplace.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
}
