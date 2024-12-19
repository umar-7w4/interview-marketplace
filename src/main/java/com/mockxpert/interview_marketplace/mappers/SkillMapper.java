package com.mockxpert.interview_marketplace.mappers;

import com.mockxpert.interview_marketplace.dto.SkillDto;
import com.mockxpert.interview_marketplace.entities.Skill;

public class SkillMapper {

    // Convert Skill Entity to SkillDto
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

    // Convert SkillDto to Skill Entity
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
