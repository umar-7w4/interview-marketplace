package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.IntervieweeSkillDto;
import com.mockxpert.interview_marketplace.entities.Interviewee;
import com.mockxpert.interview_marketplace.entities.IntervieweeSkill;
import com.mockxpert.interview_marketplace.entities.Skill;
import com.mockxpert.interview_marketplace.exceptions.*;
import com.mockxpert.interview_marketplace.mappers.IntervieweeSkillMapper;
import com.mockxpert.interview_marketplace.repositories.IntervieweeRepository;
import com.mockxpert.interview_marketplace.repositories.IntervieweeSkillRepository;
import com.mockxpert.interview_marketplace.repositories.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IntervieweeSkillService {

    @Autowired
    private IntervieweeSkillRepository intervieweeSkillRepository;

    @Autowired
    private IntervieweeRepository intervieweeRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Transactional
    public IntervieweeSkillDto addIntervieweeSkill(IntervieweeSkillDto dto) {
        Interviewee interviewee = intervieweeRepository.findById(dto.getIntervieweeId())
                .orElseThrow(() -> new ResourceNotFoundException("Interviewee not found with ID: " + dto.getIntervieweeId()));
        Skill skill = skillRepository.findById(dto.getSkillId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + dto.getSkillId()));

        IntervieweeSkill entity = IntervieweeSkillMapper.toEntity(dto, interviewee, skill);
        try {
            IntervieweeSkill savedEntity = intervieweeSkillRepository.save(entity);
            return IntervieweeSkillMapper.toDto(savedEntity);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to save Interviewee Skill due to server error.");
        }
    }

    @Transactional
    public IntervieweeSkillDto updateIntervieweeSkill(Long intervieweeSkillId, IntervieweeSkillDto dto) {
        IntervieweeSkill intervieweeSkill = intervieweeSkillRepository.findById(intervieweeSkillId)
                .orElseThrow(() -> new ResourceNotFoundException("Interviewee Skill not found with ID: " + intervieweeSkillId));

        if (dto.getYearsOfExperience() >= 0) {
            intervieweeSkill.setYearsOfExperience(dto.getYearsOfExperience());
        }
        if (dto.getProficiencyLevel() != null) {
            intervieweeSkill.setProficiencyLevel(dto.getProficiencyLevel());
        }
        if (dto.getCertified() != null) {
            intervieweeSkill.setCertified(dto.getCertified());
        }

        try {
            IntervieweeSkill updatedEntity = intervieweeSkillRepository.save(intervieweeSkill);
            return IntervieweeSkillMapper.toDto(updatedEntity);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to update Interviewee Skill due to server error.");
        }
    }
}
