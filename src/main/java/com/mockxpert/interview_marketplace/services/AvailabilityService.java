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

    /**
     * Register a new availability and notify the interviewer.
     *
     * @param availabilityDto Availability data.
     * @return Created availability.
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

            //  Notify the interviewer about successful availability creation
            sendAvailabilityNotification(interviewer.getUser().getUserId(), " Availability Created",
                    "Your availability for " + availability.getDate() + " from " +
                            availability.getStartTime() + " to " + availability.getEndTime() + " has been successfully created.");

            return AvailabilityMapper.toDto(savedAvailability);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to save Availability due to server error.");
        }
    }

    /**
     * Update availability information and notify the interviewer.
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

        //  Notify interviewer when slot is booked
        sendAvailabilityNotification(availability.getInterviewer().getUser().getUserId(), "📅 Slot Booked",
                "Your availability on " + availability.getDate() + " has been booked.");
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

        //  Notify interviewer of cancellation
        sendAvailabilityNotification(availability.getInterviewer().getUser().getUserId(), "❌ Availability Canceled",
                "Your availability on " + availability.getDate() + " has been canceled.");
    }

    /**
     * Helper method to send availability notifications.
     *
     * @param userId  The user ID of the interviewer.
     * @param subject The subject of the notification.
     * @param message The body of the notification.
     */
    private void sendAvailabilityNotification(Long userId, String subject, String message) {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setUserId(userId);
        notificationDto.setSubject(subject);
        notificationDto.setMessage(message);
        notificationDto.setType("EMAIL");
        notificationDto.setStatus("SENT");

        notificationService.createNotification(notificationDto);
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
        return !newStart.isAfter(existingEnd) && !existingStart.isAfter(newEnd);
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
    public List<AvailabilityDto> filterAvailabilities(
            LocalDate startDate,
            LocalDate endDate,
            String timezone,
            AvailabilityStatus status
    ) {
        return availabilityRepository.filterAvailabilities(startDate, endDate, timezone, status).stream().map(AvailabilityMapper::toDto).collect(Collectors.toList());
    }
}
