package com.mockxpert.interview_marketplace.mappers;

import com.mockxpert.interview_marketplace.dto.NotificationDto;
import com.mockxpert.interview_marketplace.entities.Booking;
import com.mockxpert.interview_marketplace.entities.Interview;
import com.mockxpert.interview_marketplace.entities.Notification;
import com.mockxpert.interview_marketplace.entities.User;

public class NotificationMapper {

    public static NotificationDto toDto(Notification notification) {
        if (notification == null) {
            return null;
        }

        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setNotificationId(notification.getNotificationId());
        notificationDto.setUserId(notification.getUser() != null ? notification.getUser().getUserId() : null);
        notificationDto.setBookingId(notification.getBooking() != null ? notification.getBooking().getBookingId() : null);
        notificationDto.setIntervieweeId(notification.getInterview() != null ? notification.getInterview().getInterviewId() : null);
        notificationDto.setRelatedEntityType(notification.getRelatedEntityType());
        notificationDto.setRelatedEntityId(notification.getRelatedEntityId());
        notificationDto.setMessage(notification.getMessage());
        notificationDto.setType(notification.getType().name());
        notificationDto.setStatus(notification.getStatus().name());
        notificationDto.setCreatedAt(notification.getCreatedAt());
        notificationDto.setScheduledSendTime(notification.getScheduledSendTime());
        notificationDto.setRead(notification.isRead());
        notificationDto.setTimeBeforeInterview(notification.getTimeBeforeInterview());

        return notificationDto;
    }

    public static Notification toEntity(NotificationDto notificationDto, User user, Booking booking, Interview interview) {
        if (notificationDto == null) {
            return null;
        }

        Notification notification = new Notification();
        notification.setNotificationId(notificationDto.getNotificationId());
        notification.setUser(user);
        notification.setBooking(booking);
        notification.setInterview(interview);
        notification.setRelatedEntityType(notificationDto.getRelatedEntityType());
        notification.setRelatedEntityId(notificationDto.getRelatedEntityId());
        notification.setMessage(notificationDto.getMessage());
        notification.setType(Notification.NotificationType.valueOf(notificationDto.getType()));
        notification.setStatus(Notification.NotificationStatus.valueOf(notificationDto.getStatus()));
        notification.setCreatedAt(notificationDto.getCreatedAt());
        notification.setScheduledSendTime(notificationDto.getScheduledSendTime());
        notification.setRead(notificationDto.isRead());
        notification.setTimeBeforeInterview(notificationDto.getTimeBeforeInterview());

        return notification;
    }
}
