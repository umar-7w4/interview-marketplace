package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.AvailabilityDto;
import com.mockxpert.interview_marketplace.entities.Availability;
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

import java.time.LocalTime;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvailabilityService {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private InterviewerRepository interviewerRepository;

    /**
     * Register a new availability.
     * @param availabilityDto the availability data transfer object containing registration information.
     * @return the saveAndFlushd AvailabilityDto.
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
            	System.out.print("Conflict" + availabilityDto.getStartTime() +" "+ availabilityDto.getEndTime()  +" "+ existing.getStartTime()  +" "+  existing.getEndTime());
                throw new ConflictException("Time slot conflict with existing availability: " +
                        existing.getStartTime() + " - " + existing.getEndTime());
            }
        }
        
        Availability availability = AvailabilityMapper.toEntity(availabilityDto, interviewer);
        try {
            Availability savedAvailability = availabilityRepository.saveAndFlush(availability);
            return AvailabilityMapper.toDto(savedAvailability);
        } catch (Exception e) {
        	System.out.println(e.getMessage());
            throw new InternalServerErrorException("Failed to save Availability due to server error.");
        }
    }


    /**
     * Update availability information.
     * @param availabilityId the ID of the availability to update.
     * @param availabilityDto the availability data transfer object containing updated information.
     * @return the updated AvailabilityDto.
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
     * Get a list of all availabilities.
     * @return a list of all Availability entities as DTOs.
     */
    public List<AvailabilityDto> findAllAvailabilities() {
        List<Availability> availabilities = availabilityRepository.findAll();
        return availabilities.stream()
                .map(AvailabilityMapper::toDto)
                .collect(Collectors.toList());
    }
    
    
    
    private boolean isOverlapping(LocalTime newStart, LocalTime newEnd, LocalTime existingStart, LocalTime existingEnd) {
        return !newStart.isAfter(existingEnd) && !existingStart.isAfter(newEnd);

    }
}
