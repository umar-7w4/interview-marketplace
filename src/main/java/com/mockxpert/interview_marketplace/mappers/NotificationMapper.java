package com.mockxpert.interview_marketplace.mappers;

import com.mockxpert.interview_marketplace.dto.NotificationDto;
import com.mockxpert.interview_marketplace.entities.*;

/**
 * Mapper class that converts Data Transfer Object to notification entity object.
 * 
 * @author Umar Mohammad
 */

public class NotificationMapper {
	
	/**
	 * Map Notification DTO to entity.
	 * 
	 * @param notification
	 * @return
	 */

    public static NotificationDto toDto(Notification notification) {
        if (notification == null) {
            return null;
        }

        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setNotificationId(notification.getNotificationId());
        notificationDto.setUserId(notification.getUser() != null ? notification.getUser().getUserId() : null);
        notificationDto.setBookingId(notification.getBooking() != null ? notification.getBooking().getBookingId() : null);
        notificationDto.setInterviewId(notification.getInterview() != null ? notification.getInterview().getInterviewId() : null);
        notificationDto.setPaymentId(notification.getPayment() != null ? notification.getPayment().getPaymentId() : null);
        notificationDto.setFeedbackId(notification.getFeedback() != null ? notification.getFeedback().getFeedbackId() : null);
        notificationDto.setSubject(notification.getSubject());
        notificationDto.setMessage(notification.getMessage());
        notificationDto.setType(notification.getType().name());
        notificationDto.setStatus(notification.getStatus().name());
        notificationDto.setCreatedAt(notification.getSentAt());
        notificationDto.setSentAt(notification.getSentAt());
        notificationDto.setReadAt(notification.getReadAt());
        notificationDto.setScheduledSendTime(notification.getScheduledSendTime());
        notificationDto.setRead(notification.isRead());
        notificationDto.setTimeBeforeInterview(notification.getTimeBeforeInterview());

        return notificationDto;
    }
    
	
	/**
	 * Map Notification entity to DTO.
	 * 
	 * @param notification
	 * @return
	 */

    public static Notification toEntity(NotificationDto notificationDto, User user, Booking booking, Interview interview, Payment payment, Feedback feedback) {
        if (notificationDto == null) {
            return null;
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setBooking(booking);
        notification.setInterview(interview);
        notification.setPayment(payment);
        notification.setFeedback(feedback);
        notification.setSubject(notificationDto.getSubject());
        notification.setMessage(notificationDto.getMessage());
        notification.setType(Notification.NotificationType.valueOf(notificationDto.getType()));
        notification.setStatus(Notification.NotificationStatus.valueOf(notificationDto.getStatus()));
        notification.setSentAt(notificationDto.getSentAt());
        notification.setReadAt(notificationDto.getReadAt());
        notification.setScheduledSendTime(notificationDto.getScheduledSendTime());
        notification.setRead(notificationDto.isRead());
        notification.setTimeBeforeInterview(notificationDto.getTimeBeforeInterview());

        return notification;
    }
}
