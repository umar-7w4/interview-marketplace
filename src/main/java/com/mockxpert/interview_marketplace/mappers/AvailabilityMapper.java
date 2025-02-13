package com.mockxpert.interview_marketplace.mappers;

import com.mockxpert.interview_marketplace.dto.AvailabilityDto;
import com.mockxpert.interview_marketplace.entities.Availability;
import com.mockxpert.interview_marketplace.entities.Interviewer;


/**
 * Mapper class that converts Data Transfer Object to availability entity object.
 * 
 * @author Umar Mohammad
 */

public class AvailabilityMapper {

	/**
	 * Availability dto to entity. 
	 * 
	 * @param availability
	 * @return
	 */
    public static AvailabilityDto toDto(Availability availability) {
        if (availability == null) {
            return null;
        }

        AvailabilityDto dto = new AvailabilityDto();
        dto.setAvailabilityId(availability.getAvailabilityId());
        dto.setInterviewerId(availability.getInterviewer().getInterviewerId());
        dto.setDate(availability.getDate());
        dto.setStartTime(availability.getStartTime());
        dto.setEndTime(availability.getEndTime());
        dto.setStatus(availability.getStatus() != null ? availability.getStatus().name() : null);
        dto.setTimezone(availability.getTimezone());

        return dto;
    }
    
    /**
     * Availability entity to dto. 
     * 
     * @param dto
     * @param interviewer
     * @return
     */

    public static Availability toEntity(AvailabilityDto dto, Interviewer interviewer) {
        if (dto == null || interviewer == null) {
            return null;
        }

        Availability availability = new Availability();
        availability.setInterviewer(interviewer);
        availability.setDate(dto.getDate());
        availability.setStartTime(dto.getStartTime());
        availability.setEndTime(dto.getEndTime());
        availability.setStatus(dto.getStatus() != null ? Availability.AvailabilityStatus.valueOf(dto.getStatus()) : null);
        availability.setTimezone(dto.getTimezone());

        return availability;
    }
}
