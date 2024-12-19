package com.mockxpert.interview_marketplace.entities;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long skillId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 255)
    private String description;

    // Relationships
    @ManyToMany(mappedBy = "skills", fetch = FetchType.LAZY)
    private List<Interviewer> interviewers;

    @ManyToMany(mappedBy = "skills", fetch = FetchType.LAZY)
    private List<Interviewee> interviewees;

	public Long getSkillId() {
		return skillId;
	}

	public void setSkillId(Long skillId) {
		this.skillId = skillId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Interviewer> getInterviewers() {
		return interviewers;
	}

	public void setInterviewers(List<Interviewer> interviewers) {
		this.interviewers = interviewers;
	}

	public List<Interviewee> getInterviewees() {
		return interviewees;
	}

	public void setInterviewees(List<Interviewee> interviewees) {
		this.interviewees = interviewees;
	}
    
    
}
