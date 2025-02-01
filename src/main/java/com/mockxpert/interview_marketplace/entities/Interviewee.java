package com.mockxpert.interview_marketplace.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;


@Entity
@Table(name = "interviewees")
public class Interviewee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interviewee_id", nullable = false)
    private Long intervieweeId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "education_level")
    private String educationLevel;

    @ElementCollection
    @CollectionTable(name = "interviewee_languages", joinColumns = @JoinColumn(name = "interviewee_id"))
    @Column(name = "language")
    private List<String> languagesSpoken;

    @Column(name = "current_job_role")
    private String currentJobRole;

    @Column(name = "field_of_interest")
    private String fieldOfInterest;

    @Column(name = "resume")
    private String resume;  

    @Column(nullable = false)
    private String timezone;

    @OneToMany(mappedBy = "interviewee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "interviewee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Interview> interviews;

    @OneToMany(mappedBy = "interviewee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IntervieweeSkill> skills = new ArrayList<>();

	public Long getIntervieweeId() {
		return intervieweeId;
	}

	public void setIntervieweeId(Long intervieweeId) {
		this.intervieweeId = intervieweeId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public String getCurrentJobRole() {
		return currentJobRole;
	}

	public void setCurrentJobRole(String currentJobRole) {
		this.currentJobRole = currentJobRole;
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

	public List<Booking> getBookings() {
		return bookings;
	}

	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}

	public List<Interview> getInterviews() {
		return interviews;
	}

	public void setInterviews(List<Interview> interviews) {
		this.interviews = interviews;
	}

	public List<IntervieweeSkill> getSkills() {
		return skills;
	}

	public void setSkills(List<IntervieweeSkill> skills) {
		this.skills = skills;
	}
    
    
}
