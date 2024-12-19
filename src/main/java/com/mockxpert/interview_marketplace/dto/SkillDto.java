package com.mockxpert.interview_marketplace.dto;

import jakarta.validation.constraints.NotBlank;


public class SkillDto {

    private Long skillId;

    @NotBlank(message = "Skill name is required")
    private String name;

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
