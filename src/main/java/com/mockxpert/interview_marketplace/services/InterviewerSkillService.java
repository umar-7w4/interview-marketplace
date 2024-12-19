package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.InterviewerSkillDto;
import com.mockxpert.interview_marketplace.entities.Interviewer;
import com.mockxpert.interview_marketplace.entities.InterviewerSkill;
import com.mockxpert.interview_marketplace.entities.Skill;
import com.mockxpert.interview_marketplace.exceptions.*;
import com.mockxpert.interview_marketplace.mappers.InterviewerSkillMapper;
import com.mockxpert.interview_marketplace.repositories.InterviewerRepository;
import com.mockxpert.interview_marketplace.repositories.InterviewerSkillRepository;
import com.mockxpert.interview_marketplace.repositories.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InterviewerSkillService {

    @Autowired
    private InterviewerSkillRepository interviewerSkillRepository;

    @Autowired
    private InterviewerRepository interviewerRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Transactional
    public InterviewerSkillDto addInterviewerSkill(InterviewerSkillDto dto) {
        Interviewer interviewer = interviewerRepository.findById(dto.getInterviewerId())
                .orElseThrow(() -> new ResourceNotFoundException("Interviewer not found with ID: " + dto.getInterviewerId()));
        Skill skill = skillRepository.findById(dto.getSkillId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + dto.getSkillId()));

        InterviewerSkill entity = InterviewerSkillMapper.toEntity(dto, interviewer, skill);
        try {
            InterviewerSkill savedEntity = interviewerSkillRepository.save(entity);
            return InterviewerSkillMapper.toDto(savedEntity);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to save Interviewer Skill due to server error.");
        }
    }

    @Transactional
    public InterviewerSkillDto updateInterviewerSkill(Long interviewerSkillId, InterviewerSkillDto dto) {
        InterviewerSkill interviewerSkill = interviewerSkillRepository.findById(interviewerSkillId)
                .orElseThrow(() -> new ResourceNotFoundException("Interviewer Skill not found with ID: " + interviewerSkillId));

        if (dto.getYearsOfExperience() >= 0) {
            interviewerSkill.setYearsOfExperience(dto.getYearsOfExperience());
        }
        if (dto.getProficiencyLevel() != null) {
            interviewerSkill.setProficiencyLevel(dto.getProficiencyLevel());
        }
        if (dto.getCertified() != null) {
            interviewerSkill.setCertified(dto.getCertified());
        }

        try {
            InterviewerSkill updatedEntity = interviewerSkillRepository.save(interviewerSkill);
            return InterviewerSkillMapper.toDto(updatedEntity);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to update Interviewer Skill due to server error.");
        }
    }
}
