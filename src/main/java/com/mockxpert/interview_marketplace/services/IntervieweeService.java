package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.IntervieweeDto;
import com.mockxpert.interview_marketplace.entities.Interviewee;
import com.mockxpert.interview_marketplace.entities.Skill;
import com.mockxpert.interview_marketplace.entities.User;
import com.mockxpert.interview_marketplace.exceptions.*;
import com.mockxpert.interview_marketplace.mappers.IntervieweeMapper;
import com.mockxpert.interview_marketplace.repositories.IntervieweeRepository;
import com.mockxpert.interview_marketplace.repositories.SkillRepository;
import com.mockxpert.interview_marketplace.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IntervieweeService {

    @Autowired
    private IntervieweeRepository intervieweeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkillRepository skillRepository;

    /**
     * Register a new Interviewee.
     * @param intervieweeDto the interviewee data transfer object containing registration information.
     * @return the saved Interviewee entity.
     */
    @Transactional
    public IntervieweeDto registerInterviewee(IntervieweeDto intervieweeDto) {
        User user = userRepository.findById(intervieweeDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + intervieweeDto.getUserId()));

        List<Skill> skills = skillRepository.findAllById(intervieweeDto.getSkillIds());
        if (skills.isEmpty() && !intervieweeDto.getSkillIds().isEmpty()) {
            throw new BadRequestException("One or more provided skill IDs are invalid.");
        }

        Interviewee interviewee = IntervieweeMapper.toEntity(intervieweeDto, user, skills);

        try {
            Interviewee savedInterviewee = intervieweeRepository.save(interviewee);
            return IntervieweeMapper.toDto(savedInterviewee);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to register interviewee due to server error.");
        }
    }

    /**
     * Update interviewee profile information.
     * @param intervieweeId the ID of the interviewee to update.
     * @param intervieweeDto the interviewee data transfer object containing updated information.
     * @return the updated Interviewee entity.
     */
    @Transactional
    public IntervieweeDto updateIntervieweeProfile(Long intervieweeId, IntervieweeDto intervieweeDto) {
        Interviewee interviewee = intervieweeRepository.findById(intervieweeId)
                .orElseThrow(() -> new ResourceNotFoundException("Interviewee not found with ID: " + intervieweeId));

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

        if (intervieweeDto.getSkillIds() != null) {
            List<Skill> skills = skillRepository.findAllById(intervieweeDto.getSkillIds());
            if (skills.isEmpty() && !intervieweeDto.getSkillIds().isEmpty()) {
                throw new BadRequestException("One or more provided skill IDs are invalid.");
            }
            interviewee.setSkills(skills);
        }

        try {
            Interviewee updatedInterviewee = intervieweeRepository.save(interviewee);
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
            intervieweeRepository.save(interviewee);
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
            intervieweeRepository.save(interviewee);
            return true;
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to reactivate interviewee due to server error.");
        }
    }
}
