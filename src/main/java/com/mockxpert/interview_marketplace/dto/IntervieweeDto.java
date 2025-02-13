package com.mockxpert.interview_marketplace.dto;


import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for the interviewee JSON object.
 * 
 * @author Umar Mohammad
 */
public class IntervieweeDto {

    private Long intervieweeId;

    @NotNull(message = "User ID is required")
    private Long userId;

    private String educationLevel;

    private List<String> languagesSpoken;

    private String currentRole;

    private String fieldOfInterest;

    private String resume;  // URL or path to the resume file

    @NotBlank(message = "Timezone is required")
    private String timezone;

    private List<IntervieweeSkillDto> skills;

	public Long getIntervieweeId() {
		return intervieweeId;
	}

	public void setIntervieweeId(Long intervieweeId) {
		this.intervieweeId = intervieweeId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getEducationLevel() {
		return educationLevel;
	}

	public void setEducationLevel(String educationLevel) {
		this.educationLevel = educationLevel;
	}

	public List<String> getLanguagesSpoken() {
		return languagesSpoken;
	}

	public void setLanguagesSpoken(List<String> languagesSpoken) {
		this.languagesSpoken = languagesSpoken;
	}

	public String getCurrentRole() {
		return currentRole;
	}

	public void setCurrentRole(String currentRole) {
		this.currentRole = currentRole;
	}

	public String getFieldOfInterest() {
		return fieldOfInterest;
	}

	public void setFieldOfInterest(String fieldOfInterest) {
		this.fieldOfInterest = fieldOfInterest;
	}

	public String getResume() {
		return resume;
	}

	public void setResume(String resume) {
		this.resume = resume;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public List<IntervieweeSkillDto> getSkills() {
		return skills;
	}

	public void setSkills(List<IntervieweeSkillDto> skills) {
		this.skills = skills;
	}

    
    
}
