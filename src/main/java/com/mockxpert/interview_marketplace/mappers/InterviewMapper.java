package com.mockxpert.interview_marketplace.mappers;

import com.mockxpert.interview_marketplace.dto.InterviewDto;
import com.mockxpert.interview_marketplace.entities.Interview;
import com.mockxpert.interview_marketplace.entities.Booking;
import com.mockxpert.interview_marketplace.entities.Interviewee;
import com.mockxpert.interview_marketplace.entities.Interviewer;

/**
 * Mapper class that converts Data Transfer Object to interview entity object.
 * 
 * @author Umar Mohammad
 */

public class InterviewMapper {
	
	/**
	 * Map DTO to Interview entity
	 * 
	 * @param interview
	 * @return
	 */

    public static InterviewDto toDto(Interview interview) {
        if (interview == null) {
            return null;
        }

        InterviewDto dto = new InterviewDto();
        dto.setInterviewId(interview.getInterviewId());
        dto.setIntervieweeId(interview.getInterviewee().getIntervieweeId());
        dto.setInterviewerId(interview.getInterviewer().getInterviewerId());
        dto.setBookingId(interview.getBooking().getBookingId());
        dto.setDate(interview.getDate());
        dto.setStartTime(interview.getStartTime());
        dto.setDuration(interview.getDuration());
        dto.setInterviewLink(interview.getInterviewLink());
        dto.setInterviewStatus(interview.getStatus() != null ? interview.getStatus().name() : null);
        dto.setEndTime(interview.getEndTime());
        dto.setTimezone(interview.getTimezone());
        dto.setActualStartTime(interview.getActualStartTime());
        dto.setActualEndTime(interview.getActualEndTime());

        return dto;
    }
    
	/**
	 * Map entity to Interview DTO
	 * 
	 * @param interview
	 * @return
	 */

    public static Interview toEntity(InterviewDto dto, Interviewee interviewee, Interviewer interviewer, Booking booking) {
        if (dto == null || interviewee == null || interviewer == null || booking == null) {
            return null;
        }

        Interview interview = new Interview();
        interview.setInterviewee(interviewee);
        interview.setInterviewer(interviewer);
        interview.setBooking(booking);
        interview.setDate(dto.getDate());
        interview.setStartTime(dto.getStartTime());
        interview.setDuration(dto.getDuration());
        interview.setInterviewLink(dto.getInterviewLink());
        interview.setStatus(dto.getInterviewStatus() != null ? Interview.InterviewStatus.valueOf(dto.getInterviewStatus()) : null);
        interview.setTimezone(dto.getTimezone());
        interview.setActualStartTime(dto.getActualStartTime());
        interview.setActualEndTime(dto.getActualEndTime());

        return interview;
    }
}
