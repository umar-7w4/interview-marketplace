package com.mockxpert.interview_marketplace.entities;

import java.util.List;

import jakarta.persistence.*;

/**
 *  
 * Entity class thats responsible for table creation for skills and its fields
 * 
 * @author Umar Mohammad
 * 
 */
@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id", nullable = false)
    private Long skillId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 255)
    private String description;

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
	
	
}
