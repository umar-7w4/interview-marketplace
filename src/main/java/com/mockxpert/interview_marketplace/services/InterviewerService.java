package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.InterviewerDto;
import com.mockxpert.interview_marketplace.dto.InterviewerSkillDto;
import com.mockxpert.interview_marketplace.entities.Interviewer;
import com.mockxpert.interview_marketplace.entities.InterviewerSkill;
import com.mockxpert.interview_marketplace.entities.Skill;
import com.mockxpert.interview_marketplace.entities.User;
import com.mockxpert.interview_marketplace.exceptions.*;
import com.mockxpert.interview_marketplace.mappers.InterviewerMapper;
import com.mockxpert.interview_marketplace.mappers.InterviewerSkillMapper;
import com.mockxpert.interview_marketplace.repositories.InterviewerRepository;
import com.mockxpert.interview_marketplace.repositories.InterviewerSkillRepository;
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
 * Service class for managing all the interviewers.
 * 
 * @author Umar Mohammad
 */
@Service
public class InterviewerService {

    @Autowired
    private InterviewerRepository interviewerRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private InterviewerSkillRepository interviewerSkillRepository;
    
    @Autowired
    private SkillRepository skillRepository;

    /**
     * Register a new interviewer.
     * @param interviewerDto the interviewer data transfer object containing registration information.
     * @return the saveAndFlushd Interviewer entity.
     */
    @Transactional
    public InterviewerDto registerInterviewer(InterviewerDto interviewerDto) {
        // Step 1: Fetch the User
        User user = userRepository.findById(interviewerDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + interviewerDto.getUserId()));

        // Step 2: Convert DTO to Entity
        Interviewer interviewer = InterviewerMapper.toEntity(interviewerDto);
        interviewer.setUser(user);

        // Step 3: Handle Skills
        List<InterviewerSkill> interviewerSkills = new ArrayList<>();
        if (interviewerDto.getSkills() != null && !interviewerDto.getSkills().isEmpty()) {
            for (InterviewerSkillDto skillDto : interviewerDto.getSkills()) {
                // Fetch the Skill entity
                Skill skill = skillRepository.findById(skillDto.getSkillId())
                        .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + skillDto.getSkillId()));

                // Map DTO to InterviewerSkill entity
                InterviewerSkill interviewerSkill = InterviewerSkillMapper.toEntity(skillDto, interviewer, skill);
                interviewerSkills.add(interviewerSkill);
            }
        }

        // Save the Interviewer first
        Interviewer savedInterviewer = interviewerRepository.save(interviewer);

        // Set and save InterviewerSkills
        if (!interviewerSkills.isEmpty()) {
            for (InterviewerSkill interviewerSkill : interviewerSkills) {
                interviewerSkill.setInterviewer(savedInterviewer);
            }
            interviewerSkillRepository.saveAll(interviewerSkills);
        }

        return InterviewerMapper.toDto(savedInterviewer);
    }



    /**
     * Update interviewer profile information.
     * @param interviewerId the ID of the interviewer to update.
     * @param interviewerDto the interviewer data transfer object containing updated information.
     * @return the updated InterviewerDto.
     */
    @Transactional
    public InterviewerDto updateInterviewerProfile(Long interviewerId, InterviewerDto interviewerDto) {
        // Step 1: Fetch the Interviewer
        Interviewer interviewer = interviewerRepository.findById(interviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Interviewer not found with ID: " + interviewerId));

        // Step 2: Update Simple Fields
        if (interviewerDto.getBio() != null) {
            interviewer.setBio(interviewerDto.getBio());
        }
        if (interviewerDto.getCurrentCompany() != null) {
            interviewer.setCurrentCompany(interviewerDto.getCurrentCompany());
        }
        if (interviewerDto.getYearsOfExperience() != null) {
            interviewer.setYearsOfExperience(interviewerDto.getYearsOfExperience());
        }
        if (interviewerDto.getLanguagesSpoken() != null) {
            interviewer.setLanguagesSpoken(interviewerDto.getLanguagesSpoken());
        }
        if (interviewerDto.getCertifications() != null) {
            interviewer.setCertifications(interviewerDto.getCertifications());
        }
        if (interviewerDto.getSessionRate() != null) {
            interviewer.setSessionRate(interviewerDto.getSessionRate());
        }
        if (interviewerDto.getTimezone() != null) {
            interviewer.setTimezone(interviewerDto.getTimezone());
        }
        if (interviewerDto.getProfileCompletionStatus() != null) {
            interviewer.setProfileCompletionStatus(interviewerDto.getProfileCompletionStatus());
        }

        // Step 3: Handle Skills
        if (interviewerDto.getSkills() != null && !interviewerDto.getSkills().isEmpty()) {
            List<InterviewerSkill> updatedSkills = new ArrayList<>();
            for (InterviewerSkillDto skillDto : interviewerDto.getSkills()) {
                // Fetch the Skill entity by skillId
                Skill skill = skillRepository.findById(skillDto.getSkillId())
                        .orElseThrow(() -> new BadRequestException("Invalid skill ID: " + skillDto.getSkillId()));

                // Check if the skill already exists for the interviewer
                InterviewerSkill existingSkill = interviewer.getSkills().stream()
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
                    InterviewerSkill newSkill = InterviewerSkillMapper.toEntity(skillDto, interviewer, skill);
                    updatedSkills.add(newSkill);
                }
            }

            // Replace the interviewer's skills with the updated list
            interviewer.getSkills().clear();
            interviewer.getSkills().addAll(updatedSkills);
        }

        // Step 4: Save the Interviewer and Skills
        try {
            Interviewer updatedInterviewer = interviewerRepository.saveAndFlush(interviewer);
            return InterviewerMapper.toDto(updatedInterviewer);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to update interviewer profile due to server error.");
        }
    }


    /**
     * Find interviewer by user ID.
     * @param userId the user ID linked to the interviewer.
     * @return an Optional containing the InterviewerDto if found, or empty otherwise.
     */
    public Optional<InterviewerDto> findInterviewerByUserId(Long userId) {
        return interviewerRepository.findByUser_UserId(userId)
                .map(InterviewerMapper::toDto)
                .or(() -> {
                    throw new ResourceNotFoundException("Interviewer not found for user ID: " + userId);
                });
    }

    /**
     * Verify an interviewer.
     * @param interviewerId the ID of the interviewer to verify.
     * @param verified the verification status to be set.
     * @return the updated InterviewerDto.
     */
    @Transactional
    public InterviewerDto verifyInterviewer(Long interviewerId, boolean verified) {
        Interviewer interviewer = interviewerRepository.findById(interviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Interviewer not found with ID: " + interviewerId));

        if (interviewer.getIsVerified() == verified) {
            throw new BadRequestException("Interviewer already has the specified verification status.");
        }

        interviewer.setIsVerified(verified);

        try {
            Interviewer updatedInterviewer = interviewerRepository.saveAndFlush(interviewer);
            return InterviewerMapper.toDto(updatedInterviewer);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to update verification status due to server error.");
        }
    }
    
    /**
     * Updates the verification status of an interviewer.
     *
     * @param interviewerId the ID of the interviewer.
     * @param isVerified    the new verification status.
     */
    @Transactional
    public void updateInterviewerVerificationStatus(Long interviewerId, boolean isVerified) {
        Interviewer interviewer = interviewerRepository.findById(interviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Interviewer not found with ID: " + interviewerId));

        interviewer.setIsVerified(isVerified);
        interviewerRepository.save(interviewer);
    }

    /**
     * Deactivate an interviewer.
     * @param interviewerId the ID of the interviewer to deactivate.
     * @return the updated InterviewerDto with status set to INACTIVE.
     */
    @Transactional
    public InterviewerDto deactivateInterviewer(Long interviewerId) {
        Interviewer interviewer = interviewerRepository.findById(interviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Interviewer not found with ID: " + interviewerId));

        if (interviewer.getStatus() == Interviewer.Status.INACTIVE) {
            throw new BadRequestException("Interviewer is already inactive.");
        }

        interviewer.setStatus(Interviewer.Status.INACTIVE);

        try {
            Interviewer updatedInterviewer = interviewerRepository.saveAndFlush(interviewer);
            return InterviewerMapper.toDto(updatedInterviewer);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to deactivate interviewer due to server error.");
        }
    }

    /**
     * Reactivate an interviewer.
     * @param interviewerId the ID of the interviewer to reactivate.
     * @return the updated InterviewerDto with status set to ACTIVE.
     */
    @Transactional
    public InterviewerDto reactivateInterviewer(Long interviewerId) {
        Interviewer interviewer = interviewerRepository.findById(interviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Interviewer not found with ID: " + interviewerId));

        if (interviewer.getStatus() == Interviewer.Status.ACTIVE) {
            throw new BadRequestException("Interviewer is already active.");
        }

        interviewer.setStatus(Interviewer.Status.ACTIVE);

        try {
            Interviewer updatedInterviewer = interviewerRepository.saveAndFlush(interviewer);
            return InterviewerMapper.toDto(updatedInterviewer);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to reactivate interviewer due to server error.");
        }
    }

}
