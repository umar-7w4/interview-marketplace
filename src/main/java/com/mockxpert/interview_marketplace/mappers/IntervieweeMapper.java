package com.mockxpert.interview_marketplace.mappers;

import com.mockxpert.interview_marketplace.dto.IntervieweeDto;
import com.mockxpert.interview_marketplace.entities.Interviewee;
import com.mockxpert.interview_marketplace.entities.Skill;
import com.mockxpert.interview_marketplace.entities.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class that converts Data Transfer Object to interviewee entity object.
 * 
 * @author Umar Mohammad
 */

public class IntervieweeMapper {
	
	/**
	 * Convert Interviewee Entity to IntervieweeDto
	 * 
	 * @param interviewee
	 * @return
	 */

    public static IntervieweeDto toDto(Interviewee interviewee) {
        if (interviewee == null) {
            return null;
        }

        IntervieweeDto intervieweeDto = new IntervieweeDto();
        intervieweeDto.setIntervieweeId(interviewee.getIntervieweeId());
        intervieweeDto.setUserId(interviewee.getUser() != null ? interviewee.getUser().getUserId() : null);
        intervieweeDto.setEducationLevel(interviewee.getEducationLevel());
        intervieweeDto.setLanguagesSpoken(interviewee.getLanguagesSpoken());
        intervieweeDto.setCurrentRole(interviewee.getCurrentJobRole());
        intervieweeDto.setFieldOfInterest(interviewee.getFieldOfInterest());
        intervieweeDto.setResume(interviewee.getResume());
        intervieweeDto.setTimezone(interviewee.getTimezone());
        intervieweeDto.setSkills(interviewee.getSkills().stream()
                .map(IntervieweeSkillMapper::toDto)
                .collect(Collectors.toList()));
        intervieweeDto.setLinkedinUrl(interviewee.getLinkedinUrl());

        return intervieweeDto;
    }
    
	/**
	 * Convert IntervieweeDto to Interviewee Entity
	 * 
	 * @param interviewee
	 * @return
	 */

    public static Interviewee toEntity(IntervieweeDto intervieweeDto, User user, List<Skill> skills) {
        if (intervieweeDto == null) {
            return null;
        }

        Interviewee interviewee = new Interviewee();
        interviewee.setIntervieweeId(intervieweeDto.getIntervieweeId());
        interviewee.setUser(user);
        interviewee.setEducationLevel(intervieweeDto.getEducationLevel());
        interviewee.setLanguagesSpoken(intervieweeDto.getLanguagesSpoken());
        interviewee.setCurrentJobRole(intervieweeDto.getCurrentRole());
        interviewee.setFieldOfInterest(intervieweeDto.getFieldOfInterest());
        interviewee.setResume(intervieweeDto.getResume());
        interviewee.setTimezone(intervieweeDto.getTimezone());
        interviewee.setLinkedinUrl(intervieweeDto.getLinkedinUrl());

        return interviewee;
    }
}
