package com.mockxpert.interview_marketplace.mappers;

import com.mockxpert.interview_marketplace.dto.InterviewerSkillDto;
import com.mockxpert.interview_marketplace.entities.Interviewer;
import com.mockxpert.interview_marketplace.entities.InterviewerSkill;
import com.mockxpert.interview_marketplace.entities.Skill;

public class InterviewerSkillMapper {

    // Map InterviewerSkill entity to DTO
    public static InterviewerSkillDto toDto(InterviewerSkill entity) {
        if (entity == null) {
            return null;
        }

        InterviewerSkillDto dto = new InterviewerSkillDto();
        dto.setInterviewerSkillId(entity.getInterviewerSkillId());
        dto.setInterviewerId(entity.getInterviewer().getInterviewerId());
        dto.setSkillId(entity.getSkill().getSkillId());
        dto.setYearsOfExperience(entity.getYearsOfExperience());
        dto.setProficiencyLevel(entity.getProficiencyLevel());
        dto.setCertified(entity.isCertified());

        return dto;
    }

    // Map DTO to InterviewerSkill entity
    public static InterviewerSkill toEntity(InterviewerSkillDto dto, Interviewer interviewer, Skill skill) {
        if (dto == null || interviewer == null || skill == null) {
            return null;
        }

        InterviewerSkill entity = new InterviewerSkill();
        entity.setInterviewer(interviewer);
        entity.setSkill(skill);
        entity.setYearsOfExperience(dto.getYearsOfExperience());
        entity.setProficiencyLevel(dto.getProficiencyLevel());
        entity.setCertified(dto.getCertified());

        return entity;
    }
}