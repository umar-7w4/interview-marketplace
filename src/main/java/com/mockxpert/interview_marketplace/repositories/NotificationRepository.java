package com.mockxpert.interview_marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.mockxpert.interview_marketplace.entities.Notification;
import com.mockxpert.interview_marketplace.entities.Notification.NotificationStatus;
import com.mockxpert.interview_marketplace.entities.Notification.NotificationType;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 
 * Repository class thats reposible generating query methods related to notifications.
 * 
 * @author Umar Mohammad
 */
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
     * @param status the status of the notification (e.g., PENDING, SENT, FAILED).
     * @return a list of notifications with the specified status.
     */
    List<Notification> findByStatus(NotificationStatus status);

    /**
     * Find notifications by type.
     * @param type the type of the notification (e.g., EMAIL).
     * @return a list of notifications of the specified type.
     */
    List<Notification> findByType(NotificationType type);

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
    Long countByUser_UserIdAndStatus(Long userId, NotificationStatus status);

    /**
     * Find notifications by user ID, type, and status.
     * @param userId the ID of the user.
     * @param type the type of the notification.
     * @param status the status of the notification.
     * @return a list of notifications for the specified user, type, and status.
     */
    List<Notification> findByUser_UserIdAndTypeAndStatus(Long userId, NotificationType type, NotificationStatus status);
}
