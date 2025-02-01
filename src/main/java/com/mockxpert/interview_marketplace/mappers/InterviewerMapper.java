package com.mockxpert.interview_marketplace.mappers;

import com.mockxpert.interview_marketplace.dto.InterviewerDto;
import com.mockxpert.interview_marketplace.dto.InterviewerSkillDto;
import com.mockxpert.interview_marketplace.entities.Interviewer;
import com.mockxpert.interview_marketplace.entities.InterviewerSkill;
import com.mockxpert.interview_marketplace.entities.Skill;

import java.util.List;
import java.util.stream.Collectors;

public class InterviewerMapper {

    /**
     * Converts Interviewer entity to InterviewerDto.
     *
     * @param interviewer the Interviewer entity
     * @return the corresponding InterviewerDto
     */
    public static InterviewerDto toDto(Interviewer interviewer) {
        if (interviewer == null) {
            return null;
        }

        InterviewerDto dto = new InterviewerDto();
        dto.setInterviewerId(interviewer.getInterviewerId());
        dto.setUserId(interviewer.getUser().getUserId());
        dto.setBio(interviewer.getBio());
        dto.setCurrentCompany(interviewer.getCurrentCompany());
        dto.setYearsOfExperience(interviewer.getYearsOfExperience());
        dto.setLanguagesSpoken(interviewer.getLanguagesSpoken());
        dto.setCertifications(interviewer.getCertifications());
        dto.setSessionRate(interviewer.getSessionRate());
        dto.setTimezone(interviewer.getTimezone());
        dto.setStatus(interviewer.getStatus() != null ? interviewer.getStatus().name() : null); // Convert enum to string
        dto.setAverageRating(interviewer.getAverageRating());
        dto.setProfileCompletionStatus(interviewer.getProfileCompletionStatus());
        dto.setIsVerified(interviewer.getIsVerified());
        dto.setSkills(interviewer.getSkills().stream()
                .map(InterviewerSkillMapper::toDto)
                .collect(Collectors.toList()));

        return dto;
    }

    /**
     * Converts InterviewerDto to Interviewer entity.
     *
     * @param dto the InterviewerDto
     * @return the corresponding Interviewer entity
     */
    public static Interviewer toEntity(InterviewerDto dto) {
        if (dto == null) {
            return null;
        }

        Interviewer interviewer = new Interviewer();
        interviewer.setInterviewerId(dto.getInterviewerId());
        // Note: User must be set separately in service layer, as it is linked through foreign key.
        interviewer.setBio(dto.getBio());
        interviewer.setCurrentCompany(dto.getCurrentCompany());
        interviewer.setYearsOfExperience(dto.getYearsOfExperience());
        interviewer.setLanguagesSpoken(dto.getLanguagesSpoken());
        interviewer.setCertifications(dto.getCertifications());
        interviewer.setSessionRate(dto.getSessionRate());
        interviewer.setTimezone(dto.getTimezone());
        interviewer.setStatus(mapStatusFromString(dto.getStatus())); // Convert string to enum
        interviewer.setAverageRating(dto.getAverageRating());
        interviewer.setProfileCompletionStatus(dto.getProfileCompletionStatus());
        interviewer.setIsVerified(dto.getIsVerified());

        return interviewer;
    }

    /**
     * Converts String status from DTO to Interviewer.Status enum.
     *
     * @param status the status string to convert
     * @return the corresponding Interviewer.Status enum
     */
    private static Interviewer.Status mapStatusFromString(String status) {
        if (status == null) {
            return null;
        }

        try {
            return Interviewer.Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status, e);
        }
    }

    /**
     * Finds the corresponding InterviewerSkill entity for the given interviewer and skill.
     *
     * @param interviewer the Interviewer entity
     * @param skill the Skill entity
     * @return the corresponding InterviewerSkill entity
     */
    private static InterviewerSkill findInterviewerSkill(Interviewer interviewer, Skill skill) {
        return interviewer.getSkills().stream()
                .filter(interviewerSkill -> interviewerSkill.getSkill().equals(skill))
                .findFirst()
                .orElse(null);
    }
}