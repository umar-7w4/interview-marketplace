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

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing all the interviewer skills.
 * 
 * @author Umar Mohammad
 */
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
            InterviewerSkill saveAndFlushdEntity = interviewerSkillRepository.saveAndFlush(entity);
            return InterviewerSkillMapper.toDto(saveAndFlushdEntity);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to saveAndFlush Interviewer Skill due to server error.");
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
        if (dto.isCertified() != null) {
            interviewerSkill.setCertified(dto.isCertified());
        }

        try {
            InterviewerSkill updatedEntity = interviewerSkillRepository.saveAndFlush(interviewerSkill);
            return InterviewerSkillMapper.toDto(updatedEntity);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to update Interviewer Skill due to server error.");
        }
    }
    
    /**
     * Retrieve all skills for a specific interviewer.
     * @param interviewerId the ID of the interviewer.
     * @return a list of InterviewerSkillDto objects.
     */
    public List<InterviewerSkillDto> getSkillsByInterviewer(Long interviewerId) {
        try {
            List<InterviewerSkill> skills = interviewerSkillRepository.findByInterviewer_InterviewerId(interviewerId);
            return skills.stream()
                    .map(InterviewerSkillMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to retrieve skills for the interviewer due to server error.");
        }
    }
    
    /**
     * Delete a skill for a specific interviewer.
     * @param interviewerSkillId the ID of the interviewer skill to delete.
     * @return true if the skill was successfully deleted, false otherwise.
     */
    
    @Transactional
    public boolean deleteInterviewerSkill(Long interviewerSkillId) {
        try {
            InterviewerSkill skill = interviewerSkillRepository.findById(interviewerSkillId)
                    .orElseThrow(() -> new ResourceNotFoundException("Interviewer skill not found with ID: " + interviewerSkillId));
            interviewerSkillRepository.delete(skill);
            return true;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to delete the interviewer skill due to server error.");
        }
    }
    
}
