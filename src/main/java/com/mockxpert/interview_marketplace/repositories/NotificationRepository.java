package com.mockxpert.interview_marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mockxpert.interview_marketplace.entities.Notification;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find all notifications for a specific user.
     * @param userId the ID of the user.
     * @return a list of notifications for the specified user.
     */
    List<Notification> findByUser_UserId(Long userId);

    /**
     * Find all notifications by status.
     * @param status the status of the notification (e.g., pending, sent, failed).
     * @return a list of notifications with the specified status.
     */
    List<Notification> findByStatus(String status);

    /**
     * Find notifications by type.
     * @param type the type of the notification (e.g., email, sms, app notification).
     * @return a list of notifications of the specified type.
     */
    List<Notification> findByType(String type);

    /**
     * Find notifications by related entity type and related entity ID.
     * @param relatedEntityType the type of the related entity (e.g., Booking, Interview, Payment).
     * @param relatedEntityId the ID of the related entity.
     * @return a list of notifications related to the specified entity.
     */
    List<Notification> findByRelatedEntityTypeAndRelatedEntityId(String relatedEntityType, Long relatedEntityId);

    /**
     * Find all unread notifications for a specific user.
     * @param userId the ID of the user.
     * @param isRead whether the notification has been read or not.
     * @return a list of unread notifications for the specified user.
     */
    List<Notification> findByUser_UserIdAndIsRead(Long userId, Boolean isRead);

    /**
     * Find notifications scheduled to be sent before a specific time.
     * @param scheduledSendTime the time before which notifications are scheduled to be sent.
     * @return a list of notifications scheduled to be sent before the specified time.
     */
    List<Notification> findByScheduledSendTimeBefore(LocalDateTime scheduledSendTime);

    /**
     * Count notifications by user ID and status.
     * @param userId the ID of the user.
     * @param status the status of the notifications.
     * @return the count of notifications for the specified user with the given status.
     */
    Long countByUser_UserIdAndStatus(Long userId, String status);

    /**
     * Find notifications by user ID, type, and status.
     * @param userId the ID of the user.
     * @param type the type of the notification.
     * @param status the status of the notification.
     * @return a list of notifications for the specified user, type, and status.
     */
    List<Notification> findByUser_UserIdAndTypeAndStatus(Long userId, String type, String status);

    /**
     * Find notifications created within a specific date range.
     * @param startDate the start date of the range.
     * @param endDate the end date of the range.
     * @return a list of notifications created within the specified date range.
     */
    List<Notification> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
