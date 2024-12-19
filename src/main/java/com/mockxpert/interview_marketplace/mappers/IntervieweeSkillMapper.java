package com.mockxpert.interview_marketplace.mappers;

import com.mockxpert.interview_marketplace.dto.IntervieweeSkillDto;
import com.mockxpert.interview_marketplace.entities.Interviewee;
import com.mockxpert.interview_marketplace.entities.IntervieweeSkill;
import com.mockxpert.interview_marketplace.entities.Skill;

public class IntervieweeSkillMapper {

    // Map IntervieweeSkill entity to DTO
    public static IntervieweeSkillDto toDto(IntervieweeSkill entity) {
        if (entity == null) {
            return null;
        }

        IntervieweeSkillDto dto = new IntervieweeSkillDto();
        dto.setIntervieweeSkillId(entity.getIntervieweeSkillId());
        dto.setIntervieweeId(entity.getInterviewee().getIntervieweeId());
        dto.setSkillId(entity.getSkill().getSkillId());
        dto.setYearsOfExperience(entity.getYearsOfExperience());
        dto.setProficiencyLevel(entity.getProficiencyLevel());
        dto.setCertified(entity.isCertified());

        return dto;
    }

    // Map DTO to IntervieweeSkill entity
    public static IntervieweeSkill toEntity(IntervieweeSkillDto dto, Interviewee interviewee, Skill skill) {
        if (dto == null || interviewee == null || skill == null) {
            return null;
        }

        IntervieweeSkill entity = new IntervieweeSkill();
        entity.setInterviewee(interviewee);
        entity.setSkill(skill);
        entity.setYearsOfExperience(dto.getYearsOfExperience());
        entity.setProficiencyLevel(dto.getProficiencyLevel());
        entity.setCertified(dto.getCertified());

        return entity;
    }
}
