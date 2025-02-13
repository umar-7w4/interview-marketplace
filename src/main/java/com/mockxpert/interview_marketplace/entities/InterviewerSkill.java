package com.mockxpert.interview_marketplace.entities;

import jakarta.persistence.*;

/**
 *  
 * Entity class thats responsible for table creation for interviewer skills and its fields
 * 
 * @author Umar Mohammad
 * 
 */

@Entity
@Table(name = "interviewer_skills")
public class InterviewerSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interviewer_skill_id", nullable = false)
    private Long interviewerSkillId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewer_id", nullable = false)
    private Interviewer interviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "years_of_experience", nullable = false)
    private int yearsOfExperience;

    @Column(name = "proficiency_level", nullable = false)
    private String proficiencyLevel;  

    @Column(name = "is_certified", nullable = false)
    private boolean certified;

	public Long getInterviewerSkillId() {
		return interviewerSkillId;
	}

	public void setInterviewerSkillId(Long interviewerSkillId) {
		this.interviewerSkillId = interviewerSkillId;
	}

	public Interviewer getInterviewer() {
		return interviewer;
	}

	public void setInterviewer(Interviewer interviewer) {
		this.interviewer = interviewer;
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
