package com.mockxpert.interview_marketplace.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for the interviewee skill JSON object.
 * 
 * @author Umar Mohammad
 */
public class IntervieweeSkillDto {

    private Long intervieweeSkillId;

    @NotNull(message = "Interviewee ID is required")
    private Long intervieweeId;

    @NotNull(message = "Skill ID is required")
    private Long skillId;

    @Min(value = 0, message = "Years of experience must be non-negative")
    private int yearsOfExperience;

    @NotBlank(message = "Proficiency level is required")
    private String proficiencyLevel;  // e.g., Beginner, Intermediate, Advanced

    @NotNull(message = "Certification status is required")
    private Boolean certified;
    
    public IntervieweeSkillDto() {}

	public Long getIntervieweeSkillId() {
		return intervieweeSkillId;
	}

	public void setIntervieweeSkillId(Long intervieweeSkillId) {
		this.intervieweeSkillId = intervieweeSkillId;
	}

	public Long getIntervieweeId() {
		return intervieweeId;
	}

	public void setIntervieweeId(Long intervieweeId) {
		this.intervieweeId = intervieweeId;
	}

	public Long getSkillId() {
		return skillId;
	}

	public void setSkillId(Long skillId) {
		this.skillId = skillId;
	}

	public int getYearsOfExperience() {
		return yearsOfExperience;
	}

	public void setYearsOfExperience(int yearsOfExperience) {
		this.yearsOfExperience = yearsOfExperience;
	}

	public String getProficiencyLevel() {
		return proficiencyLevel;
	}

	public void setProficiencyLevel(String proficiencyLevel) {
		this.proficiencyLevel = proficiencyLevel;
	}

	public Boolean isCertified() {
		return certified;
	}

	public void setCertified(Boolean certified) {
		this.certified = certified;
	}
    
    
}

