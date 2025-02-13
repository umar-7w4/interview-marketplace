package com.mockxpert.interview_marketplace.mappers;

import com.mockxpert.interview_marketplace.dto.SkillDto;
import com.mockxpert.interview_marketplace.entities.Skill;

/**
 * Mapper class that converts Data Transfer Object to skill entity object.
 * 
 * @author Umar Mohammad
 */
public class SkillMapper {
	
	/**
	 * Convert Skill Entity to SkillDto
	 * 
	 * @param skill
	 * @return
	 */

    public static SkillDto toDto(Skill skill) {
        if (skill == null) {
            return null;
        }

        SkillDto skillDto = new SkillDto();
        skillDto.setSkillId(skill.getSkillId());
        skillDto.setName(skill.getName());
        skillDto.setDescription(skill.getDescription());

        return skillDto;
    }
    
    /**
     * Convert SkillDto to Skill Entity
     * 
     * @param skillDto
     * @return
     */

    public static Skill toEntity(SkillDto skillDto) {
        if (skillDto == null) {
            return null;
        }

        Skill skill = new Skill();
        skill.setSkillId(skillDto.getSkillId());
        skill.setName(skillDto.getName());
        skill.setDescription(skillDto.getDescription());

        return skill;
    }
}
