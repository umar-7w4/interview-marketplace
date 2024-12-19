package com.mockxpert.interview_marketplace.dto;


import jakarta.validation.constraints.*;

import java.util.List;


public class InterviewerDto {

    private Long interviewerId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @Size(max = 500, message = "Bio must be less than 500 characters")
    private String bio;

    private String currentCompany;

    private Integer yearsOfExperience;

    private List<String> languagesSpoken;

    private List<String> certifications;

    @NotNull(message = "Session rate is required")
    private Double sessionRate;

    @NotBlank(message = "Timezone is required")
    private String timezone;

    private String status;

    private Double averageRating;

    @NotNull(message = "Profile completion status is required")
    private Boolean profileCompletionStatus;

    @NotNull(message = "Verification status is required")
    private Boolean isVerified;

    private List<Long> skillIds;

	public Long getInterviewerId() {
		return interviewerId;
	}

	public void setInterviewerId(Long interviewerId) {
		this.interviewerId = interviewerId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getCurrentCompany() {
		return currentCompany;
	}

	public void setCurrentCompany(String currentCompany) {
		this.currentCompany = currentCompany;
	}

	public Integer getYearsOfExperience() {
		return yearsOfExperience;
	}

	public void setYearsOfExperience(Integer yearsOfExperience) {
		this.yearsOfExperience = yearsOfExperience;
	}

	public List<String> getLanguagesSpoken() {
		return languagesSpoken;
	}

	public void setLanguagesSpoken(List<String> languagesSpoken) {
		this.languagesSpoken = languagesSpoken;
	}

	public List<String> getCertifications() {
		return certifications;
	}

	public void setCertifications(List<String> certifications) {
		this.certifications = certifications;
	}

	public Double getSessionRate() {
		return sessionRate;
	}

	public void setSessionRate(Double sessionRate) {
		this.sessionRate = sessionRate;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(Double averageRating) {
		this.averageRating = averageRating;
	}

	public Boolean getProfileCompletionStatus() {
		return profileCompletionStatus;
	}

	public void setProfileCompletionStatus(Boolean profileCompletionStatus) {
		this.profileCompletionStatus = profileCompletionStatus;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public List<Long> getSkillIds() {
		return skillIds;
	}

	public void setSkillIds(List<Long> skillIds) {
		this.skillIds = skillIds;
	}
    
}

