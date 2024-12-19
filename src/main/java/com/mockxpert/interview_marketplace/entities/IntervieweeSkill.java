package com.mockxpert.interview_marketplace.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "interviewee_skills")
public class IntervieweeSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long intervieweeSkillId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewee_id", nullable = false)
    private Interviewee interviewee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "years_of_experience", nullable = false)
    private int yearsOfExperience;

    @Column(name = "proficiency_level", nullable = false)
    private String proficiencyLevel; 
    
    @Column(name = "is_certified", nullable = false)
    private boolean certified;

	public Long getIntervieweeSkillId() {
		return intervieweeSkillId;
	}

	public void setIntervieweeSkillId(Long intervieweeSkillId) {
		this.intervieweeSkillId = intervieweeSkillId;
	}

	public Interviewee getInterviewee() {
		return interviewee;
	}

	public void setInterviewee(Interviewee interviewee) {
		this.interviewee = interviewee;
	}

	public Skill getSkill() {
		return skill;
	}

	public void setSkill(Skill skill) {
		this.skill = skill;
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

	public boolean isCertified() {
		return certified;
	}

	public void setCertified(boolean certified) {
		this.certified = certified;
	}
    
    
}
