package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.InterviewerDto;
import com.mockxpert.interview_marketplace.entities.Interviewer;
import com.mockxpert.interview_marketplace.entities.User;
import com.mockxpert.interview_marketplace.exceptions.*;
import com.mockxpert.interview_marketplace.mappers.InterviewerMapper;
import com.mockxpert.interview_marketplace.repositories.InterviewerRepository;
import com.mockxpert.interview_marketplace.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class InterviewerService {

    @Autowired
    private InterviewerRepository interviewerRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Register a new interviewer.
     * @param interviewerDto the interviewer data transfer object containing registration information.
     * @return the saved Interviewer entity.
     */
    @Transactional
    public InterviewerDto registerInterviewer(InterviewerDto interviewerDto) {
        User user = userRepository.findById(interviewerDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + interviewerDto.getUserId()));

        if (user.getRole() != User.Role.INTERVIEWER) {
            throw new BadRequestException("User with ID " + interviewerDto.getUserId() + " is not eligible to be an interviewer.");
        }

        Interviewer interviewer = InterviewerMapper.toEntity(interviewerDto);
        interviewer.setUser(user);

        try {
            Interviewer savedInterviewer = interviewerRepository.save(interviewer);
            return InterviewerMapper.toDto(savedInterviewer);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to register interviewer due to server error.");
        }
    }

    /**
     * Update interviewer profile information.
     * @param interviewerId the ID of the interviewer to update.
     * @param interviewerDto the interviewer data transfer object containing updated information.
     * @return the updated InterviewerDto.
     */
    @Transactional
    public InterviewerDto updateInterviewerProfile(Long interviewerId, InterviewerDto interviewerDto) {
        Interviewer interviewer = interviewerRepository.findById(interviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Interviewer not found with ID: " + interviewerId));

        interviewer.setBio(interviewerDto.getBio());
        interviewer.setCurrentCompany(interviewerDto.getCurrentCompany());
        interviewer.setYearsOfExperience(interviewerDto.getYearsOfExperience());
        interviewer.setLanguagesSpoken(interviewerDto.getLanguagesSpoken());
        interviewer.setCertifications(interviewerDto.getCertifications());
        interviewer.setSessionRate(interviewerDto.getSessionRate());
        interviewer.setTimezone(interviewerDto.getTimezone());
        interviewer.setProfileCompletionStatus(interviewerDto.getProfileCompletionStatus());

        try {
            Interviewer updatedInterviewer = interviewerRepository.save(interviewer);
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
            Interviewer updatedInterviewer = interviewerRepository.save(interviewer);
            return InterviewerMapper.toDto(updatedInterviewer);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to update verification status due to server error.");
        }
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
            Interviewer updatedInterviewer = interviewerRepository.save(interviewer);
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
            Interviewer updatedInterviewer = interviewerRepository.save(interviewer);
            return InterviewerMapper.toDto(updatedInterviewer);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to reactivate interviewer due to server error.");
        }
    }
}
