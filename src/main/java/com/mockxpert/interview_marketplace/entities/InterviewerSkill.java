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
@Table(name = "interviewer_skills")
public class InterviewerSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
}
