package com.mockxpert.interview_marketplace.entities;



import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

/**
 *  
 * Entity class thats responsible for table creation for interviewers and its fields
 * 
 * @author Umar Mohammad
 * 
 */

@Entity
@Table(name = "interviewers")
public class Interviewer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interviewer_id", nullable = false)
    private Long interviewerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 500)
    private String bio;

    @Column(name = "current_company")
    private String currentCompany;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @ElementCollection
    @CollectionTable(name = "interviewer_languages", joinColumns = @JoinColumn(name = "interviewer_id"))
    @Column(name = "language")
    private List<String> languagesSpoken;

    @ElementCollection
    @CollectionTable(name = "interviewer_certifications", joinColumns = @JoinColumn(name = "interviewer_id"))
    @Column(name = "certification")
    private List<String> certifications;

    @Column(name = "session_rate", nullable = false)
    private Double sessionRate;

    @Column(nullable = false)
    private String timezone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "average_rating")
    private Double averageRating;

    @Column(name = "profile_completion_status", nullable = false)
    private Boolean profileCompletionStatus;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;

    @OneToMany(mappedBy = "interviewer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Interview> interviews;

    @OneToMany(mappedBy = "interviewer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Availability> availabilities;

    @OneToOne(mappedBy = "interviewer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private InterviewerVerification interviewerVerification;

    @OneToMany(mappedBy = "interviewer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterviewerSkill> skills = new ArrayList<>();
  

	public enum Status {
        ACTIVE,
        INACTIVE,
        PENDING_VERIFICATION
    }

	public Long getInterviewerId() {
		return interviewerId;
	}

	public void setInterviewerId(Long interviewerId) {
		this.interviewerId = interviewerId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
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

	public List<Interview> getInterviews() {
		return interviews;
	}

	public void setInterviews(List<Interview> interviews) {
		this.interviews = interviews;
	}

	public List<Availability> getAvailabilities() {
		return availabilities;
	}

	public void setAvailabilities(List<Availability> availabilities) {
		this.availabilities = availabilities;
	}

	public InterviewerVerification getInterviewerVerification() {
		return interviewerVerification;
	}

	public void setInterviewerVerification(InterviewerVerification interviewerVerification) {
		this.interviewerVerification = interviewerVerification;
	}
	
    public List<InterviewerSkill> getSkills() {
		return skills;
	}

	public void setSkills(List<InterviewerSkill> skills) {
		this.skills = skills;
	}
    

}
