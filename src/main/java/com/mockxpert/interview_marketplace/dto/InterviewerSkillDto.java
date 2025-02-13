package com.mockxpert.interview_marketplace.dto;


import jakarta.validation.constraints.*;

/**
 * Data Transfer Object for the interviewer skill JSON object.
 * 
 * @author Umar Mohammad
 */

public class InterviewerSkillDto {

    private Long interviewerSkillId;

    @NotNull(message = "Interviewer ID is required")
    private Long interviewerId;

    @NotNull(message = "Skill ID is required")
    private Long skillId;

    @Min(value = 0, message = "Years of experience must be non-negative")
    private int yearsOfExperience;

    @NotBlank(message = "Proficiency level is required")
    private String proficiencyLevel;  // e.g., Beginner, Intermediate, Advanced

    @NotNull(message = "Certification status is required")
    private Boolean certified;
    
    public InterviewerSkillDto() {}

	public InterviewerSkillDto(long l, int i, String string, boolean b) {
		// TODO Auto-generated constructor stub
	}

	public Long getInterviewerSkillId() {
		return interviewerSkillId;
	}

	public void setInterviewerSkillId(Long interviewerSkillId) {
		this.interviewerSkillId = interviewerSkillId;
	}

	public Long getInterviewerId() {
		return interviewerId;
	}

	public void setInterviewerId(Long interviewerId) {
		this.interviewerId = interviewerId;
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