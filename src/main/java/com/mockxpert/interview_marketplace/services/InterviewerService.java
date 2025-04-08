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

import java.util.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    
    private InterviewerMapper interviewerMapper;

    /**
     * Register a new interviewer.
     * 
     * @param interviewerDto the interviewer data transfer object containing registration information.
     * @return the saveAndFlushd Interviewer entity.
     */
    @Transactional
    public InterviewerDto registerInterviewer(InterviewerDto interviewerDto) {
        User user = userRepository.findById(interviewerDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + interviewerDto.getUserId()));

        Interviewer interviewer = InterviewerMapper.toEntity(interviewerDto);
        interviewer.setUser(user);

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

        Interviewer savedInterviewer = interviewerRepository.save(interviewer);

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
     * 
     * @param interviewerId the ID of the interviewer to update.
     * @param interviewerDto the interviewer data transfer object containing updated information.
     * @return the updated InterviewerDto.
     */
    @Transactional
    public InterviewerDto updateInterviewerProfile(Long interviewerId, InterviewerDto interviewerDto) {
        Interviewer interviewer = interviewerRepository.findById(interviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Interviewer not found with ID: " + interviewerId));

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
        if (interviewerDto.getLinkedinUrl() != null) {
            interviewer.setLinkedinUrl(interviewerDto.getLinkedinUrl());
        }
        if(interviewerDto.getProfileImage() != null) {
        	interviewer.setProfileImage(interviewerDto.getProfileImage());
        }


        if (interviewerDto.getSkills() != null && !interviewerDto.getSkills().isEmpty()) {
            List<InterviewerSkill> updatedSkills = new ArrayList<>();
            for (InterviewerSkillDto skillDto : interviewerDto.getSkills()) {
                Skill skill = skillRepository.findById(skillDto.getSkillId())
                        .orElseThrow(() -> new BadRequestException("Invalid skill ID: " + skillDto.getSkillId()));

                InterviewerSkill existingSkill = interviewer.getSkills().stream()
                        .filter(s -> s.getSkill().getSkillId().equals(skillDto.getSkillId()))
                        .findFirst()
                        .orElse(null);

                if (existingSkill != null) {
                    existingSkill.setYearsOfExperience(skillDto.getYearsOfExperience());
                    existingSkill.setProficiencyLevel(skillDto.getProficiencyLevel());
                    existingSkill.setCertified(skillDto.isCertified());
                    updatedSkills.add(existingSkill);
                } else {
                    InterviewerSkill newSkill = InterviewerSkillMapper.toEntity(skillDto, interviewer, skill);
                    updatedSkills.add(newSkill);
                }
            }

            interviewer.getSkills().clear();
            interviewer.getSkills().addAll(updatedSkills);
        }

        try {
            Interviewer updatedInterviewer = interviewerRepository.saveAndFlush(interviewer);
            return InterviewerMapper.toDto(updatedInterviewer);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to update interviewer profile due to server error.");
        }
    }


    /**
     * Find interviewer by user ID.
     * 
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
     * 
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
     * 
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
     * 
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
    
    /**
     * 
     *Fetches all the interviewers.
     *
     */
    public List<InterviewerDto> getAllInterviewers() {
        return interviewerRepository.findAll().stream()
                .map(InterviewerMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Extended method that supports filtering by experience range and skills, and sorting by name or yearsOfExperience.
     * 
     * @param minExperience
     * @param maxExperience
     * @param currentCompany
     * @param minSessionRate
     * @param maxSessionRate
     * @param minAverageRating
     * @param maxAverageRating
     * @param verified
     * @param sortBy
     * @param sortOrder
     * @return
     */
    public List<InterviewerDto> getFilteredInterviewers(
            Integer minExperience, Integer maxExperience,
            String currentCompany,
            Double minSessionRate, Double maxSessionRate,
            Double minAverageRating, Double maxAverageRating,
            Boolean verified,
            String sortBy, String sortOrder) {
        List<Interviewer> interviewers = interviewerRepository.findAll();

        Stream<Interviewer> stream = interviewers.stream();

        if (minExperience != null) {
            stream = stream.filter(i -> i.getYearsOfExperience() >= minExperience);
        }
        if (maxExperience != null) {
            stream = stream.filter(i -> i.getYearsOfExperience() <= maxExperience);
        }
        if (currentCompany != null && !currentCompany.isEmpty()) {
            stream = stream.filter(i -> i.getCurrentCompany() != null &&
                i.getCurrentCompany().toLowerCase().contains(currentCompany.toLowerCase()));
        }
        if (minSessionRate != null) {
            stream = stream.filter(i -> i.getSessionRate() >= minSessionRate);
        }
        if (maxSessionRate != null) {
            stream = stream.filter(i -> i.getSessionRate() <= maxSessionRate);
        }
        if (minAverageRating != null) {
            stream = stream.filter(i -> i.getAverageRating() >= minAverageRating);
        }
        if (maxAverageRating != null) {
            stream = stream.filter(i -> i.getAverageRating() <= maxAverageRating);
        }
        if (verified != null && verified) {
            stream = stream.filter(i -> i.getIsVerified());
        }

        List<Interviewer> filtered = stream.collect(Collectors.toList());

        Comparator<Interviewer> comparator;
        switch (sortBy.toLowerCase()) {
            case "name":
                comparator = Comparator.comparing(i -> i.getUser().getFullName(), String.CASE_INSENSITIVE_ORDER);
                break;
            case "sessionrate":
                comparator = Comparator.comparing(Interviewer::getSessionRate);
                break;
            case "averagerating":
                comparator = Comparator.comparing(Interviewer::getAverageRating);
                break;
            case "yearsofexperience":
            default:
                comparator = Comparator.comparing(Interviewer::getYearsOfExperience);
                break;
        }
        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }
        filtered.sort(comparator);

        return filtered.stream().map(InterviewerMapper::toDto).collect(Collectors.toList());
    }
    
    /**
     * Gets all skill names of a interviewer
     * 
     * @param interviewerId
     * @return
     */
    public List<String> getSkillNamesByInterviewerId(Long interviewerId) {
    	Interviewer interviewer = interviewerRepository.findById(interviewerId)
                    .orElseThrow(() -> new RuntimeException("Interviewer not found with id: " + interviewerId));

        return interviewer.getSkills()
                    .stream()
                    .map(interviewerSkill -> interviewerSkill.getSkill().getName())
                    .collect(Collectors.toList());
    }
      
    /**
     * Gets interviewer by its id.
     * 
     * @param interviewerId
     * @return
     */
    public InterviewerDto getInterviewerById(Long interviewerId) {
        Interviewer interviewer = interviewerRepository.findById(interviewerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Interviewer not found"));
        return interviewerMapper.toDto(interviewer);
    }
      
    /**
     * Checks if there exists a interviewee with current userid.
     * 
     * @param userId
     * @return
     */
    @Transactional
    public boolean checkExistenceOfInterviewee(Long userId) {
    	return interviewerRepository.existsByUser_UserId(userId);
    }
        
        
}
