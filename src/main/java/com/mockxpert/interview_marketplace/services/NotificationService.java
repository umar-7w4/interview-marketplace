package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.NotificationDto;
import com.mockxpert.interview_marketplace.entities.*;
import com.mockxpert.interview_marketplace.mappers.NotificationMapper;
import com.mockxpert.interview_marketplace.repositories.BookingRepository;
import com.mockxpert.interview_marketplace.repositories.InterviewRepository;
import com.mockxpert.interview_marketplace.repositories.NotificationRepository;
import com.mockxpert.interview_marketplace.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    /**
     * Create a new notification.
     *
     * @param notificationDto the notification DTO.
     * @return the saveAndFlushd NotificationDto.
     */
    @Transactional
    public NotificationDto createNotification(NotificationDto notificationDto) {
        User user = userRepository.findById(notificationDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + notificationDto.getUserId()));

        Booking booking = bookingRepository.findById(notificationDto.getBookingId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + notificationDto.getBookingId()));

        Interview interview = interviewRepository.findById(notificationDto.getIntervieweeId())
                .orElseThrow(() -> new IllegalArgumentException("Interview not found with ID: " + notificationDto.getIntervieweeId()));

        Notification notification = NotificationMapper.toEntity(notificationDto, user, booking, interview);
        Notification saveAndFlushdNotification = notificationRepository.saveAndFlush(notification);

        return NotificationMapper.toDto(saveAndFlushdNotification);
    }

    /**
     * Retrieve a notification by ID.
     *
     * @param notificationId the ID of the notification.
     * @return the NotificationDto.
     */
    public NotificationDto getNotificationById(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with ID: " + notificationId));

        return NotificationMapper.toDto(notification);
    }

    /**
     * Update a notification.
     *
     * @param notificationId  the ID of the notification to update.
     * @param notificationDto the updated notification data.
     * @return the updated NotificationDto.
     */
    @Transactional
    public NotificationDto updateNotification(Long notificationId, NotificationDto notificationDto) {
        Notification existingNotification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with ID: " + notificationId));

        User user = userRepository.findById(notificationDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + notificationDto.getUserId()));

        Booking booking = bookingRepository.findById(notificationDto.getBookingId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + notificationDto.getBookingId()));

        Interview interview = interviewRepository.findById(notificationDto.getIntervieweeId())
                .orElseThrow(() -> new IllegalArgumentException("Interview not found with ID: " + notificationDto.getIntervieweeId()));

        existingNotification.setUser(user);
        existingNotification.setBooking(booking);
        existingNotification.setInterview(interview);
        existingNotification.setRelatedEntityType(notificationDto.getRelatedEntityType());
        existingNotification.setRelatedEntityId(notificationDto.getRelatedEntityId());
        existingNotification.setMessage(notificationDto.getMessage());
        existingNotification.setType(Notification.NotificationType.valueOf(notificationDto.getType()));
        existingNotification.setStatus(Notification.NotificationStatus.valueOf(notificationDto.getStatus()));
        existingNotification.setScheduledSendTime(notificationDto.getScheduledSendTime());
        existingNotification.setRead(notificationDto.isRead());
        existingNotification.setTimeBeforeInterview(notificationDto.getTimeBeforeInterview());

        Notification updatedNotification = notificationRepository.saveAndFlush(existingNotification);

        return NotificationMapper.toDto(updatedNotification);
    }

    /**
     * Delete a notification.
     *
     * @param notificationId the ID of the notification to delete.
     */
    @Transactional
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with ID: " + notificationId));

        notificationRepository.delete(notification);
    }

    /**
     * Get all notifications for a user.
     *
     * @param userId the ID of the user.
     * @return a list of NotificationDto.
     */
    public List<NotificationDto> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUser_UserId(userId).stream()
                .map(NotificationMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Mark a notification as read.
     *
     * @param notificationId the ID of the notification to mark as read.
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with ID: " + notificationId));

        notification.setRead(true);
        notificationRepository.saveAndFlush(notification);
    }
}
