package com.mockxpert.interview_marketplace.entities;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
