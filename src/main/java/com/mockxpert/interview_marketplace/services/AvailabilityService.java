package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.AvailabilityDto;
import com.mockxpert.interview_marketplace.dto.NotificationDto;
import com.mockxpert.interview_marketplace.entities.Availability;
import com.mockxpert.interview_marketplace.entities.Availability.AvailabilityStatus;
import com.mockxpert.interview_marketplace.entities.Interviewer;
import com.mockxpert.interview_marketplace.exceptions.ResourceNotFoundException;
import com.mockxpert.interview_marketplace.exceptions.BadRequestException;
import com.mockxpert.interview_marketplace.exceptions.ConflictException;
import com.mockxpert.interview_marketplace.exceptions.InternalServerErrorException;
import com.mockxpert.interview_marketplace.mappers.AvailabilityMapper;
import com.mockxpert.interview_marketplace.repositories.AvailabilityRepository;
import com.mockxpert.interview_marketplace.repositories.InterviewerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.*;
/**
 * Service for managing interviewer availabilities and sending notifications.
 * 
 * @author Umar Mohammad
 */
@Service
public class AvailabilityService {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private InterviewerRepository interviewerRepository;

    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserService userService;

    /**
     * Registers availability slot of an interviewer.
     * 
     * @param availabilityDto
     * @return availability
     */
    @Transactional
    public AvailabilityDto registerAvailability(AvailabilityDto availabilityDto) {
        Interviewer interviewer = interviewerRepository.findById(availabilityDto.getInterviewerId())
                .orElseThrow(() -> new ResourceNotFoundException("Interviewer not found with ID: " + availabilityDto.getInterviewerId()));

        if (availabilityDto.getEndTime().isBefore(availabilityDto.getStartTime())) {
            throw new BadRequestException("End time cannot be before start time.");
        }

        List<Availability> existingAvailabilities = availabilityRepository
                .findByInterviewer_InterviewerIdAndDate(availabilityDto.getInterviewerId(), availabilityDto.getDate());

        for (Availability existing : existingAvailabilities) {
            if (isOverlapping(availabilityDto.getStartTime(), availabilityDto.getEndTime(),
                    existing.getStartTime(), existing.getEndTime())) {
                throw new ConflictException("Time slot conflict with existing availability: " +
                        existing.getStartTime() + " - " + existing.getEndTime());
            }
        }

        Availability availability = AvailabilityMapper.toEntity(availabilityDto, interviewer);
        try {
            Availability savedAvailability = availabilityRepository.saveAndFlush(availability);

            // Build subject and HTML message with dynamic details
            String subject = String.format("Availability Created: %s | %s - %s",
                    savedAvailability.getDate(), savedAvailability.getStartTime(), savedAvailability.getEndTime());
            String plainMessage = String.format("Your availability for %s from %s to %s has been successfully created.",
                    savedAvailability.getDate(), savedAvailability.getStartTime(), savedAvailability.getEndTime());

            // Notify the interviewer with the beautiful HTML email UI
            sendAvailabilityNotification(interviewer.getUser().getUserId(), subject, plainMessage);

            return AvailabilityMapper.toDto(savedAvailability);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to save Availability due to server error.");
        }
    }

    /**
     * Update availability information.
     *
     * @param availabilityId  Availability ID.
     * @param availabilityDto Updated availability details.
     * @return Updated availability.
     */
    @Transactional
    public AvailabilityDto updateAvailability(Long availabilityId, AvailabilityDto availabilityDto) {
        Availability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found with ID: " + availabilityId));

        if (availabilityDto.getEndTime() != null && availabilityDto.getStartTime() != null
                && availabilityDto.getEndTime().isBefore(availabilityDto.getStartTime())) {
            throw new BadRequestException("End time cannot be before start time.");
        }

        List<Availability> existingAvailabilities = availabilityRepository
                .findByInterviewer_InterviewerIdAndDate(availability.getInterviewer().getInterviewerId(), availability.getDate())
                .stream()
                .filter(a -> !a.getAvailabilityId().equals(availabilityId))
                .toList();

        for (Availability existing : existingAvailabilities) {
            if (isOverlapping(availabilityDto.getStartTime() != null ? availabilityDto.getStartTime() : availability.getStartTime(),
                    availabilityDto.getEndTime() != null ? availabilityDto.getEndTime() : availability.getEndTime(),
                    existing.getStartTime(), existing.getEndTime())) {

                throw new ConflictException("Time slot conflict with existing availability: " +
                        existing.getStartTime() + " - " + existing.getEndTime());
            }
        }

        if (availabilityDto.getDate() != null) {
            availability.setDate(availabilityDto.getDate());
        }
        if (availabilityDto.getStartTime() != null) {
            availability.setStartTime(availabilityDto.getStartTime());
        }
        if (availabilityDto.getEndTime() != null) {
            availability.setEndTime(availabilityDto.getEndTime());
        }
        if (availabilityDto.getStatus() != null) {
            availability.setStatus(Availability.AvailabilityStatus.valueOf(availabilityDto.getStatus()));
        }
        if (availabilityDto.getTimezone() != null) {
            availability.setTimezone(availabilityDto.getTimezone());
        }

        try {
            Availability updatedAvailability = availabilityRepository.saveAndFlush(availability);
            return AvailabilityMapper.toDto(updatedAvailability);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to update Availability due to server error.");
        }
    }

    /**
     * Marks an availability as booked and notifies the interviewer.
     *
     * @param availabilityId Availability ID.
     */
    @Transactional
    public void bookAvailability(Long availabilityId) {
        Availability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found with ID: " + availabilityId));

        availability.setStatus(Availability.AvailabilityStatus.BOOKED);
        availabilityRepository.save(availability);

        String subject = String.format("Slot Booked: %s", availability.getDate());
        String plainMessage = String.format("Your availability on %s has been booked.", availability.getDate());

        sendAvailabilityNotification(availability.getInterviewer().getUser().getUserId(), subject, plainMessage);
    }

    /**
     * Cancels an availability and notifies the interviewer.
     *
     * @param availabilityId Availability ID.
     */
    @Transactional
    public void cancelAvailability(Long availabilityId) {
        Availability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found with ID: " + availabilityId));

        availability.setStatus(Availability.AvailabilityStatus.EXPIRED);
        availabilityRepository.save(availability);

        String subject = String.format("Availability Canceled: %s", availability.getDate());
        String plainMessage = String.format("Your availability on %s has been canceled.", availability.getDate());

        sendAvailabilityNotification(availability.getInterviewer().getUser().getUserId(), subject, plainMessage);
    }

    /**
     * Helper method to send availability notifications using a beautiful HTML email template.
     *
     * @param userId  The user ID of the interviewer.
     * @param subject The subject of the notification.
     * @param plainMessage The plain text message to include in the email.
     */
    private void sendAvailabilityNotification(Long userId, String subject, String plainMessage) {
        // Build the HTML email using the helper method below
        String htmlMessage = buildHtmlEmail(subject, plainMessage);

        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setUserId(userId);
        notificationDto.setSubject(subject);
        notificationDto.setMessage(htmlMessage);
        notificationDto.setType("EMAIL");
        notificationDto.setStatus("SENT");

        notificationService.createNotification(notificationDto);
    }

    /**
     * Helper method to build a beautiful HTML email template using the MockXpert theme.
     *
     * @param headerTitle The header title (often the subject with dynamic details).
     * @param content     The main content/body of the email.
     * @return A complete HTML string.
     */
    private String buildHtmlEmail(String headerTitle, String content) {
        return String.format(
            // Note: use %% to escape % signs in inline CSS
            "<!DOCTYPE html>" +
            "<html>" +
              "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<title>%s</title>" +
              "</head>" +
              "<body style=\"margin: 0; padding: 0; background-color: #f4f4f4; font-family: Arial, sans-serif;\">" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%%\">" +
                  "<tr>" +
                    "<td align=\"center\" style=\"padding: 20px 10px;\">" +
                      "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" " +
                        "style=\"background-color: #ffffff; border-radius: 8px; overflow: hidden; " +
                        "box-shadow: 0 2px 8px rgba(0,0,0,0.1);\">" +
                        "<tr>" +
                          "<td align=\"center\" bgcolor=\"#6366f1\" " +
                            "style=\"padding: 30px 0; color: #ffffff; font-size: 28px; font-weight: bold;\">" +
                            "MockXpert" +
                          "</td>" +
                        "</tr>" +
                        "<tr>" +
                          "<td style=\"padding: 40px 30px; color: #333333;\">" +
                            "<p style=\"margin: 0; font-size: 16px; line-height: 1.5;\">Dear Interviewer,</p>" +
                            "<p style=\"margin: 20px 0 0 0; font-size: 16px; line-height: 1.5;\">%s</p>" +
                          "</td>" +
                        "</tr>" +
                        "<tr>" +
                          "<td align=\"center\" bgcolor=\"#f4f4f4\" " +
                            "style=\"padding: 20px; font-size: 12px; color: #777777;\">" +
                            "© 2025 MockXpert. All rights reserved." +
                          "</td>" +
                        "</tr>" +
                      "</table>" +
                    "</td>" +
                  "</tr>" +
                "</table>" +
              "</body>" +
            "</html>", headerTitle, content);
    }

    /**
     * Get a list of all availabilities.
     * @return a list of all Availability entities as DTOs.
     */
    public List<AvailabilityDto> findAllAvailabilities() {
        List<Availability> availabilities = availabilityRepository.findAll();
        return availabilities.stream()
                .map(AvailabilityMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Delete availability by ID.
     * @param availabilityId the ID of the availability to delete.
     * @return true if the availability was deleted successfully, false otherwise.
     */
    @Transactional
    public boolean deleteAvailability(Long availabilityId) {
        Availability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found with ID: " + availabilityId));

        try {
            availabilityRepository.delete(availability);
            return true;
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to delete Availability due to server error.");
        }
    }
    
    /**
     * Find availability by ID.
     * @param availabilityId the ID of the availability to find.
     * @return the found AvailabilityDto.
     */
    public AvailabilityDto findAvailabilityById(Long availabilityId) {
        Availability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found with ID: " + availabilityId));
        return AvailabilityMapper.toDto(availability);
    }
    
    /**
     * 
     * Handles time conflicts on availabilities.
     * 
     * @param newStart
     * @param newEnd
     * @param existingStart
     * @param existingEnd
     * @return
     */

    private boolean isOverlapping(LocalTime newStart, LocalTime newEnd, LocalTime existingStart, LocalTime existingEnd) {
        return newStart.isBefore(existingEnd) && existingStart.isBefore(newEnd);
    }
    
    /**
     * Returns all the availabilities based on filter
     * 
     * @param startDate
     * @param endDate
     * @param timezone
     * @param status
     * @return
     */
    public List<AvailabilityDto> filterAvailabilities(LocalDate startDate, LocalDate endDate, String timezone, AvailabilityStatus status) {
        Long currentUserId = userService.getCurrentUser().getUserId();
        List<Availability> availabilities = availabilityRepository.filterAvailabilities(startDate, endDate, timezone, status, currentUserId);
        return availabilities.stream().map(AvailabilityMapper::toDto).collect(Collectors.toList());
    }
    
    /**
     * Fetechs all availability slots based on interviewer and date
     * 
     * @param interviewerId
     * @param date
     * @return
     */
    public List<AvailabilityDto> getAvailabilitiesByInterviewerAndDate(Long interviewerId, LocalDate date) {
        List<Availability> availabilities = availabilityRepository.findByInterviewer_InterviewerIdAndDate(interviewerId, date);
        availabilities = availabilities.stream().filter((a) -> a.getStatus() == AvailabilityStatus.AVAILABLE).collect(Collectors.toList());
        return availabilities.stream().map(AvailabilityMapper::toDto).collect(Collectors.toList());
    }
}
