package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.NotificationDto;
import com.mockxpert.interview_marketplace.entities.*;
import com.mockxpert.interview_marketplace.mappers.NotificationMapper;
import com.mockxpert.interview_marketplace.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    /**
     * Create a new notification.
     *
     * @param notificationDto the notification DTO.
     * @return the saved NotificationDto.
     */
    @Transactional
    public NotificationDto createNotification(NotificationDto notificationDto) {
        User user = userRepository.findById(notificationDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + notificationDto.getUserId()));

        Booking booking = notificationDto.getBookingId() != null ?
                bookingRepository.findById(notificationDto.getBookingId()).orElse(null) : null;

        Interview interview = notificationDto.getInterviewId() != null ?
                interviewRepository.findById(notificationDto.getInterviewId()).orElse(null) : null;

        Payment payment = notificationDto.getPaymentId() != null ?
                paymentRepository.findById(notificationDto.getPaymentId()).orElse(null) : null;

        Feedback feedback = notificationDto.getFeedbackId() != null ?
                feedbackRepository.findById(notificationDto.getFeedbackId()).orElse(null) : null;

        Notification notification = NotificationMapper.toEntity(notificationDto, user, booking, interview, payment, feedback);
        Notification savedNotification = notificationRepository.save(notification);

        return NotificationMapper.toDto(savedNotification);
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

        Booking booking = notificationDto.getBookingId() != null ?
                bookingRepository.findById(notificationDto.getBookingId()).orElse(null) : null;

        Interview interview = notificationDto.getInterviewId() != null ?
                interviewRepository.findById(notificationDto.getInterviewId()).orElse(null) : null;

        Payment payment = notificationDto.getPaymentId() != null ?
                paymentRepository.findById(notificationDto.getPaymentId()).orElse(null) : null;

        Feedback feedback = notificationDto.getFeedbackId() != null ?
                feedbackRepository.findById(notificationDto.getFeedbackId()).orElse(null) : null;

        existingNotification.setUser(user);
        existingNotification.setBooking(booking);
        existingNotification.setInterview(interview);
        existingNotification.setPayment(payment);
        existingNotification.setFeedback(feedback);
        existingNotification.setSubject(notificationDto.getSubject());
        existingNotification.setMessage(notificationDto.getMessage());
        existingNotification.setType(Notification.NotificationType.valueOf(notificationDto.getType()));
        existingNotification.setStatus(Notification.NotificationStatus.valueOf(notificationDto.getStatus()));
        existingNotification.setScheduledSendTime(notificationDto.getScheduledSendTime());
        existingNotification.setRead(notificationDto.isRead());
        existingNotification.setTimeBeforeInterview(notificationDto.getTimeBeforeInterview());

        Notification updatedNotification = notificationRepository.save(existingNotification);

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
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }
}
