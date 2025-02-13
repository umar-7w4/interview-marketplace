package com.mockxpert.interview_marketplace.controllers;

import com.mockxpert.interview_marketplace.dto.NotificationDto;
import com.mockxpert.interview_marketplace.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;


/**
 * Rest controller responsible for handling all the HTTP API requests related to notification operations.
 * 
 * @author Umar Mohammad
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    public NotificationController() {
        System.out.println("NotificationController Initialized");
    }

    /**
     * Create a new notification.
     * @param notificationDto the notification data transfer object containing information.
     * @return the created NotificationDto.
     */
    @PostMapping("/create")
    public ResponseEntity<?> createNotification(@RequestBody @Valid NotificationDto notificationDto) {
        try {
            NotificationDto savedNotification = notificationService.createNotification(notificationDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedNotification);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create notification");
        }
    }

    /**
     * Retrieve a notification by ID.
     * @param notificationId the ID of the notification to retrieve.
     * @return the NotificationDto.
     */
    @GetMapping("/{notificationId}")
    public ResponseEntity<?> getNotificationById(@PathVariable Long notificationId) {
        try {
            NotificationDto notification = notificationService.getNotificationById(notificationId);
            return ResponseEntity.ok(notification);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve notification");
        }
    }

    /**
     * Update an existing notification.
     * @param notificationId the ID of the notification to update.
     * @param notificationDto the updated notification data transfer object.
     * @return the updated NotificationDto.
     */
    @PutMapping("/{notificationId}")
    public ResponseEntity<?> updateNotification(@PathVariable Long notificationId, @RequestBody @Valid NotificationDto notificationDto) {
        try {
            NotificationDto updatedNotification = notificationService.updateNotification(notificationId, notificationDto);
            return ResponseEntity.ok(updatedNotification);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update notification");
        }
    }

    /**
     * Delete a notification by ID.
     * @param notificationId the ID of the notification to delete.
     * @return a response indicating success.
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long notificationId) {
        try {
            notificationService.deleteNotification(notificationId);
            return ResponseEntity.ok("Notification deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete notification");
        }
    }

    /**
     * Retrieve all notifications for a user.
     * @param userId the ID of the user.
     * @return a list of NotificationDto objects.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getNotificationsByUser(@PathVariable Long userId) {
        try {
            List<NotificationDto> notifications = notificationService.getNotificationsByUser(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve notifications");
        }
    }

    /**
     * Mark a notification as read.
     * @param notificationId the ID of the notification to mark as read.
     * @return a response indicating success.
     */
    @PutMapping("/{notificationId}/markAsRead")
    public ResponseEntity<String> markNotificationAsRead(@PathVariable Long notificationId) {
        try {
            notificationService.markAsRead(notificationId);
            return ResponseEntity.ok("Notification marked as read");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to mark notification as read");
        }
    }
}
