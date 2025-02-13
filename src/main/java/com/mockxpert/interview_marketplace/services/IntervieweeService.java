package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.IntervieweeDto;
import com.mockxpert.interview_marketplace.dto.IntervieweeSkillDto;
import com.mockxpert.interview_marketplace.entities.Interviewee;
import com.mockxpert.interview_marketplace.entities.IntervieweeSkill;
import com.mockxpert.interview_marketplace.entities.Skill;
import com.mockxpert.interview_marketplace.entities.User;
import com.mockxpert.interview_marketplace.exceptions.*;
import com.mockxpert.interview_marketplace.mappers.IntervieweeMapper;
import com.mockxpert.interview_marketplace.mappers.IntervieweeSkillMapper;
import com.mockxpert.interview_marketplace.repositories.IntervieweeRepository;
import com.mockxpert.interview_marketplace.repositories.IntervieweeSkillRepository;
import com.mockxpert.interview_marketplace.repositories.SkillRepository;
import com.mockxpert.interview_marketplace.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Service class for geenrating google OAuth service.
 * 
 * @author Umar Mohammad
 */
@Service
public class IntervieweeService {

    @Autowired
    private IntervieweeRepository intervieweeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkillRepository skillRepository;
    
    @Autowired 
    private IntervieweeSkillRepository intervieweeSkillRepository;

    /**
     * Register a new Interviewee.
     * @param intervieweeDto the interviewee data transfer object containing registration information.
     * @return the saved Interviewee entity.
     */
    @Transactional
    public IntervieweeDto registerInterviewee(IntervieweeDto intervieweeDto) {
        // Step 1: Fetch the User
        User user = userRepository.findById(intervieweeDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + intervieweeDto.getUserId()));

        // Step 2: Convert IntervieweeDto to Interviewee entity
        Interviewee interviewee = IntervieweeMapper.toEntity(intervieweeDto, user, null);

        // Step 3: Handle Skills
        List<IntervieweeSkill> intervieweeSkills = new ArrayList<>();
        if (intervieweeDto.getSkills() != null && !intervieweeDto.getSkills().isEmpty()) {
            for (IntervieweeSkillDto skillDto : intervieweeDto.getSkills()) {
                Skill skill = skillRepository.findById(skillDto.getSkillId())
                        .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + skillDto.getSkillId()));

                IntervieweeSkill intervieweeSkill = IntervieweeSkillMapper.toEntity(skillDto, interviewee, skill);
                intervieweeSkills.add(intervieweeSkill);
            }
        }

        // Save the Interviewee
        Interviewee savedInterviewee = intervieweeRepository.save(interviewee);

        // Set and Save IntervieweeSkills
        if (!intervieweeSkills.isEmpty()) {
            for (IntervieweeSkill intervieweeSkill : intervieweeSkills) {
                intervieweeSkill.setInterviewee(savedInterviewee);
            }
            intervieweeSkillRepository.saveAll(intervieweeSkills);
        }

        return IntervieweeMapper.toDto(savedInterviewee);
    }


    /**
     * Update interviewee profile information.
     * @param intervieweeId the ID of the interviewee to update.
     * @param intervieweeDto the interviewee data transfer object containing updated information.
     * @return the updated Interviewee entity.
     */
    @Transactional
    public IntervieweeDto updateIntervieweeProfile(Long intervieweeId, IntervieweeDto intervieweeDto) {
        // Step 1: Fetch the Interviewee
        Interviewee interviewee = intervieweeRepository.findById(intervieweeId)
                .orElseThrow(() -> new ResourceNotFoundException("Interviewee not found with ID: " + intervieweeId));

        // Step 2: Update Simple Fields
        if (intervieweeDto.getEducationLevel() != null) {
            interviewee.setEducationLevel(intervieweeDto.getEducationLevel());
        }

        if (intervieweeDto.getLanguagesSpoken() != null) {
            interviewee.setLanguagesSpoken(intervieweeDto.getLanguagesSpoken());
        }

        if (intervieweeDto.getCurrentRole() != null) {
            interviewee.setCurrentJobRole(intervieweeDto.getCurrentRole());
        }

        if (intervieweeDto.getFieldOfInterest() != null) {
            interviewee.setFieldOfInterest(intervieweeDto.getFieldOfInterest());
        }

        if (intervieweeDto.getResume() != null) {
            interviewee.setResume(intervieweeDto.getResume());
        }

        // Step 3: Handle Skills with Metadata
        if (intervieweeDto.getSkills() != null && !intervieweeDto.getSkills().isEmpty()) {
            // Fetch and validate all skills from the database
            List<IntervieweeSkill> updatedSkills = new ArrayList<>();
            for (IntervieweeSkillDto skillDto : intervieweeDto.getSkills()) {
                Skill skill = skillRepository.findById(skillDto.getSkillId())
                        .orElseThrow(() -> new BadRequestException("Invalid skill ID: " + skillDto.getSkillId()));

                // Check if the skill already exists for the interviewee
                IntervieweeSkill existingSkill = interviewee.getSkills().stream()
                        .filter(s -> s.getSkill().getSkillId().equals(skillDto.getSkillId()))
                        .findFirst()
                        .orElse(null);

                if (existingSkill != null) {
                    // Update existing skill
                    existingSkill.setYearsOfExperience(skillDto.getYearsOfExperience());
                    existingSkill.setProficiencyLevel(skillDto.getProficiencyLevel());
                    existingSkill.setCertified(skillDto.isCertified());
                    updatedSkills.add(existingSkill);
                } else {
                    // Add new skill
                    IntervieweeSkill newSkill = IntervieweeSkillMapper.toEntity(skillDto, interviewee, skill);
                    updatedSkills.add(newSkill);
                }
            }

            // Replace the interviewee's skills with the updated list
            interviewee.getSkills().clear();
            interviewee.getSkills().addAll(updatedSkills);
        }

        // Step 4: Save the Interviewee and Skills
        try {
            Interviewee updatedInterviewee = intervieweeRepository.saveAndFlush(interviewee);
            return IntervieweeMapper.toDto(updatedInterviewee);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to update interviewee profile due to server error.");
        }
    }


    /**
     * Find interviewee by user ID.
     * @param userId the user ID linked to the interviewee.
     * @return an Optional containing the IntervieweeDto if found, or empty otherwise.
     */
    public Optional<IntervieweeDto> findIntervieweeByUserId(Long userId) {
        return intervieweeRepository.findByUser_UserId(userId)
                .map(IntervieweeMapper::toDto)
                .or(() -> {
                    throw new ResourceNotFoundException("Interviewee not found for user ID: " + userId);
                });
    }

    /**
     * Deactivate an interviewee.
     * @param intervieweeId the ID of the interviewee to deactivate.
     * @return true if the interviewee was successfully deactivated, false otherwise.
     */
    @Transactional
    public boolean deactivateInterviewee(Long intervieweeId) {
        Interviewee interviewee = intervieweeRepository.findById(intervieweeId)
                .orElseThrow(() -> new ResourceNotFoundException("Interviewee not found with ID: " + intervieweeId));

        if (interviewee.getUser().getStatus() == User.Status.INACTIVE) {
            throw new BadRequestException("Interviewee is already inactive.");
        }

        interviewee.getUser().setStatus(User.Status.INACTIVE);
        try {
            intervieweeRepository.saveAndFlush(interviewee);
            return true;
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to deactivate interviewee due to server error.");
        }
    }

    /**
     * Reactivate an interviewee.
     * @param intervieweeId the ID of the interviewee to reactivate.
     * @return true if the interviewee was successfully reactivated, false otherwise.
     */
    @Transactional
    public boolean reactivateInterviewee(Long intervieweeId) {
        Interviewee interviewee = intervieweeRepository.findById(intervieweeId)
                .orElseThrow(() -> new ResourceNotFoundException("Interviewee not found with ID: " + intervieweeId));

        if (interviewee.getUser().getStatus() == User.Status.ACTIVE) {
            throw new BadRequestException("Interviewee is already active.");
        }

        interviewee.getUser().setStatus(User.Status.ACTIVE);
        try {
            intervieweeRepository.saveAndFlush(interviewee);
            return true;
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to reactivate interviewee due to server error.");
        }
    }
}
