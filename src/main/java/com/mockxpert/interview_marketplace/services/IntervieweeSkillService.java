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

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service class for managing all the interviewees.
 * 
 * @author Umar Mohammad
 */
@Service
public class IntervieweeSkillService {

    @Autowired
    private IntervieweeSkillRepository intervieweeSkillRepository;

    @Autowired
    private IntervieweeRepository intervieweeRepository;

    @Autowired
    private SkillRepository skillRepository;

    /**
     * Adds Interviewee skill.
     * 
     * @param dto
     * @return
     */
    @Transactional
    public IntervieweeSkillDto addIntervieweeSkill(IntervieweeSkillDto dto) {
        Interviewee interviewee = intervieweeRepository.findById(dto.getIntervieweeId())
                .orElseThrow(() -> new ResourceNotFoundException("Interviewee not found with ID: " + dto.getIntervieweeId()));
        Skill skill = skillRepository.findById(dto.getSkillId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + dto.getSkillId()));

        IntervieweeSkill entity = IntervieweeSkillMapper.toEntity(dto, interviewee, skill);
        try {
            IntervieweeSkill saveAndFlushdEntity = intervieweeSkillRepository.saveAndFlush(entity);
            return IntervieweeSkillMapper.toDto(saveAndFlushdEntity);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to saveAndFlush Interviewee Skill due to server error.");
        }
    }

    /**
     * Updates interviewee skill.
     * 
     * @param intervieweeSkillId
     * @param dto
     * @return
     */
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
        if (dto.isCertified() != null) {
            intervieweeSkill.setCertified(dto.isCertified());
        }

        try {
            IntervieweeSkill updatedEntity = intervieweeSkillRepository.saveAndFlush(intervieweeSkill);
            return IntervieweeSkillMapper.toDto(updatedEntity);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to update Interviewee Skill due to server error.");
        }
    }
    
    /**
     * Retrieve all skills for a specific interviewee.
     * 
     * @param intervieweeId the ID of the interviewee.
     * @return a list of IntervieweeSkillDto objects.
     */
    public List<IntervieweeSkillDto> getSkillsByInterviewee(Long intervieweeId) {
        try {
            List<IntervieweeSkill> skills = intervieweeSkillRepository.findByInterviewee_IntervieweeId(intervieweeId);
            return skills.stream()
                    .map(IntervieweeSkillMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to retrieve skills for the interviewee due to server error.");
        }
    }
    
    /**
     * Delete a skill for a specific interviewee.
     * 
     * @param intervieweeSkillId the ID of the interviewee skill to delete.
     * @return true if the skill was successfully deleted, false otherwise.
     */
    
    @Transactional
    public boolean deleteIntervieweeSkill(Long intervieweeSkillId) {
        try {
            IntervieweeSkill skill = intervieweeSkillRepository.findById(intervieweeSkillId)
                    .orElseThrow(() -> new ResourceNotFoundException("Interviewee skill not found with ID: " + intervieweeSkillId));
            intervieweeSkillRepository.delete(skill);
            return true;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to delete the interviewee skill due to server error.");
        }
    }


}
