package com.mockxpert.interview_marketplace.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mockxpert.interview_marketplace.dto.IntervieweeSkillDto;
import com.mockxpert.interview_marketplace.exceptions.ResourceNotFoundException;
import com.mockxpert.interview_marketplace.services.IntervieweeSkillService;

import jakarta.validation.Valid;


/**
 * REST controller responsible for handling all the HTTP API requests related to interviewee skills operations.
 * 
 * @author Umar Mohammad
 */
@RestController
@RequestMapping("/api/interviewee-skills")
public class IntervieweeSkillController {

    @Autowired
    private IntervieweeSkillService intervieweeSkillService;

    public IntervieweeSkillController() {
        System.out.println("IntervieweeSkillController Initialized");
    }

    /**
     * Add a new skill for an interviewee.
     * @param intervieweeSkillDto the interviewee skill data transfer object.
     * @return the created IntervieweeSkillDto.
     */
    @PostMapping("/add")
    public ResponseEntity<?> addIntervieweeSkill(@RequestBody @Valid IntervieweeSkillDto intervieweeSkillDto) {
        try {
            IntervieweeSkillDto savedSkill = intervieweeSkillService.addIntervieweeSkill(intervieweeSkillDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedSkill);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Update an existing skill for an interviewee.
     * @param intervieweeSkillId the ID of the interviewee skill to update.
     * @param intervieweeSkillDto the updated interviewee skill data transfer object.
     * @return the updated IntervieweeSkillDto.
     */
    @PutMapping("/{intervieweeSkillId}")
    public ResponseEntity<?> updateIntervieweeSkill(@PathVariable Long intervieweeSkillId, @RequestBody @Valid IntervieweeSkillDto intervieweeSkillDto) {
        try {
            IntervieweeSkillDto updatedSkill = intervieweeSkillService.updateIntervieweeSkill(intervieweeSkillId, intervieweeSkillDto);
            return ResponseEntity.ok(updatedSkill);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Retrieve all skills for an interviewee.
     * @param intervieweeId the ID of the interviewee.
     * @return a list of IntervieweeSkillDto objects.
     */
    @GetMapping("/interviewee/{intervieweeId}")
    public ResponseEntity<?> getSkillsByInterviewee(@PathVariable Long intervieweeId) {
        try {
            List<IntervieweeSkillDto> skills = intervieweeSkillService.getSkillsByInterviewee(intervieweeId);
            return ResponseEntity.ok(skills);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Delete a skill for an interviewee.
     * @param intervieweeSkillId the ID of the interviewee skill to delete.
     * @return a response indicating success.
     */
    @DeleteMapping("/{intervieweeSkillId}")
    public ResponseEntity<String> deleteIntervieweeSkill(@PathVariable Long intervieweeSkillId) {
        try {
            boolean deleted = intervieweeSkillService.deleteIntervieweeSkill(intervieweeSkillId);
            if (deleted) {
                return ResponseEntity.ok("Interviewee skill deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete interviewee skill");
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
