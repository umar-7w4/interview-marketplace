package com.mockxpert.interview_marketplace.controllers;

import com.mockxpert.interview_marketplace.dto.InterviewerDto;
import com.mockxpert.interview_marketplace.entities.Interviewer;
import com.mockxpert.interview_marketplace.exceptions.*;
import com.mockxpert.interview_marketplace.repositories.InterviewerRepository;
import com.mockxpert.interview_marketplace.services.InterviewerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * REST controller responsible for handling all the HTTP API requests related to interviewer operations.
 * 
 * @author Umar Mohammad
 */
@RestController
@RequestMapping("/api/interviewers")
public class InterviewerController {

    @Autowired
    private InterviewerService interviewerService;
    
    @Autowired
    private InterviewerRepository interviewerRepository;

    public InterviewerController() {
        System.out.println("InterviewerController Initialized");
    }

    /**
     * Register a new interviewer.
     * @param interviewerDto the interviewer data transfer object containing registration information.
     * @return the created InterviewerDto.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerInterviewer(@RequestBody @Valid InterviewerDto interviewerDto) {
        try {
            InterviewerDto savedInterviewer = interviewerService.registerInterviewer(interviewerDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedInterviewer);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Update interviewer profile.
     * @param interviewerId the ID of the interviewer to update.
     * @param interviewerDto the interviewer data transfer object containing updated information.
     * @return the updated InterviewerDto.
     */
    @PutMapping("/{interviewerId}")
    public ResponseEntity<?> updateInterviewerProfile(@PathVariable Long interviewerId, @RequestBody @Valid InterviewerDto interviewerDto) {
        try {
            InterviewerDto updatedInterviewer = interviewerService.updateInterviewerProfile(interviewerId, interviewerDto);
            return ResponseEntity.ok(updatedInterviewer);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Find interviewer by user ID.
     * @param userId the user ID linked to the interviewer.
     * @return the InterviewerDto if found.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> findInterviewerByUserId(@PathVariable Long userId) {
        try {
            Optional<InterviewerDto> interviewer = interviewerService.findInterviewerByUserId(userId);
            return ResponseEntity.ok(interviewer);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Optional.empty());
        }
    }

    /**
     * Verify an interviewer.
     * @param interviewerId the ID of the interviewer to verify.
     * @param verified the verification status.
     * @return the updated InterviewerDto.
     */
    @PutMapping("/{interviewerId}/verify")
    public ResponseEntity<?> verifyInterviewer(@PathVariable Long interviewerId, @RequestParam boolean verified) {
        try {
            InterviewerDto verifiedInterviewer = interviewerService.verifyInterviewer(interviewerId, verified);
            return ResponseEntity.ok(verifiedInterviewer);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Deactivate an interviewer.
     * @param interviewerId the ID of the interviewer to deactivate.
     * @return the updated InterviewerDto with status set to INACTIVE.
     */
    @PutMapping("/{interviewerId}/deactivate")
    public ResponseEntity<?> deactivateInterviewer(@PathVariable Long interviewerId) {
        try {
            InterviewerDto deactivatedInterviewer = interviewerService.deactivateInterviewer(interviewerId);
            return ResponseEntity.ok(deactivatedInterviewer);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Reactivate an interviewer.
     * @param interviewerId the ID of the interviewer to reactivate.
     * @return the updated InterviewerDto with status set to ACTIVE.
     */
    @PutMapping("/{interviewerId}/reactivate")
    public ResponseEntity<?> reactivateInterviewer(@PathVariable Long interviewerId) {
        try {
            InterviewerDto reactivatedInterviewer = interviewerService.reactivateInterviewer(interviewerId);
            return ResponseEntity.ok(reactivatedInterviewer);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }  
    
    /**
     * Reactivate an interviewer.
     * @param interviewerId the ID of the interviewer to reactivate.
     * @return the updated InterviewerDto with status set to ACTIVE.
     */
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<?> getInterviewerByUserId(@PathVariable Long userId) {
        InterviewerDto interviewer = interviewerService.findInterviewerByUserId(userId).get();
        return ResponseEntity.ok(interviewer);
    }
    
    /**
     * Gets user id of the current interviewer.
     * 
     * @param interviewerId
     * @return
     */
    @GetMapping("/{interviewerId}/user-id")
    public ResponseEntity<?> getUserIdForInterviewer(@PathVariable Long interviewerId) {
        Interviewer interviewer = interviewerRepository.findById(interviewerId)
            .orElseThrow(() -> new ResourceNotFoundException("Interviewer not found"));
        // interviewer.getUser() is the user entity
        Long userId = interviewer.getUser().getUserId();
        Map<String, Long> resp = Collections.singletonMap("userId", userId);
        return ResponseEntity.ok(resp);
    }
    
    /**
     * 
     * Fetches all the interviews.
     */
    @GetMapping
    public ResponseEntity<List<InterviewerDto>> getAllInterviewers() {
        List<InterviewerDto> interviewers = interviewerService.getAllInterviewers();
        return ResponseEntity.ok(interviewers);
    }
    
    /**
     * Fetches interviewers based on the current applied filters
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
    @GetMapping("/filter")
    public ResponseEntity<List<InterviewerDto>> getFilteredInterviewers(
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) String currentCompany,
            @RequestParam(required = false) Double minSessionRate,
            @RequestParam(required = false) Double maxSessionRate,
            @RequestParam(required = false) Double minAverageRating,
            @RequestParam(required = false) Double maxAverageRating,
            @RequestParam(required = false) Boolean verified,
            @RequestParam(defaultValue = "yearsOfExperience") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {

        List<InterviewerDto> dtos = interviewerService.getFilteredInterviewers(
                minExperience, maxExperience,
                currentCompany,
                minSessionRate, maxSessionRate,
                minAverageRating, maxAverageRating,
                verified,
                sortBy, sortOrder);
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Fetches all skill names of current interviewer.
     * 
     * @param interviewerId
     * @return string
     */
    @GetMapping("/{interviewerId}/skills")
    public ResponseEntity<List<String>> getSkillNames(@PathVariable Long interviewerId) {
        List<String> skillNames = interviewerService.getSkillNamesByInterviewerId(interviewerId);
        return ResponseEntity.ok(skillNames);
    }
    
    /**
     * Gets interviewer record by its id.
     * 
     * @param interviewerId
     * @return InterviewerDto
     */
    @GetMapping("/{interviewerId}")
    public ResponseEntity<InterviewerDto> getInterviewerById(@PathVariable Long interviewerId) {
        InterviewerDto interviewerDto = interviewerService.getInterviewerById(interviewerId);
        return ResponseEntity.ok(interviewerDto);
    }
    
    /**
     * Checks if current user has the interviewer profile created or not.
     * 
     * @param userId
     * @return boolean
     */
    @GetMapping("/user/{userId}/exists")
    public ResponseEntity<Boolean> doesInterviewerProfileExist(@PathVariable Long userId) {
        boolean exists = interviewerService.checkExistenceOfInterviewee(userId);
        return ResponseEntity.ok(exists);
    }
}