package com.mockxpert.interview_marketplace.controllers;

import com.mockxpert.interview_marketplace.dto.InterviewerSkillDto;
import com.mockxpert.interview_marketplace.dto.IntervieweeSkillDto;
import com.mockxpert.interview_marketplace.exceptions.*;
import com.mockxpert.interview_marketplace.services.InterviewerSkillService;
import com.mockxpert.interview_marketplace.services.IntervieweeSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/interviewer-skills")
public class InterviewerSkillController {

    @Autowired
    private InterviewerSkillService interviewerSkillService;

    public InterviewerSkillController() {
        System.out.println("InterviewerSkillController Initialized");
    }

    /**
     * Add a new skill for an interviewer.
     * @param interviewerSkillDto the interviewer skill data transfer object.
     * @return the created InterviewerSkillDto.
     */
    @PostMapping("/add")
    public ResponseEntity<?> addInterviewerSkill(@RequestBody @Valid InterviewerSkillDto interviewerSkillDto) {
        try {
            InterviewerSkillDto savedSkill = interviewerSkillService.addInterviewerSkill(interviewerSkillDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedSkill);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Update an existing skill for an interviewer.
     * @param interviewerSkillId the ID of the interviewer skill to update.
     * @param interviewerSkillDto the updated interviewer skill data transfer object.
     * @return the updated InterviewerSkillDto.
     */
    @PutMapping("/{interviewerSkillId}")
    public ResponseEntity<?> updateInterviewerSkill(@PathVariable Long interviewerSkillId, @RequestBody @Valid InterviewerSkillDto interviewerSkillDto) {
        try {
            InterviewerSkillDto updatedSkill = interviewerSkillService.updateInterviewerSkill(interviewerSkillId, interviewerSkillDto);
            return ResponseEntity.ok(updatedSkill);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Retrieve all skills for an interviewer.
     * @param interviewerId the ID of the interviewer.
     * @return a list of InterviewerSkillDto objects.
     */
    @GetMapping("/interviewer/{interviewerId}")
    public ResponseEntity<?> getSkillsByInterviewer(@PathVariable Long interviewerId) {
        try {
            List<InterviewerSkillDto> skills = interviewerSkillService.getSkillsByInterviewer(interviewerId);
            return ResponseEntity.ok(skills);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Delete a skill for an interviewer.
     * @param interviewerSkillId the ID of the interviewer skill to delete.
     * @return a response indicating success.
     */
    @DeleteMapping("/{interviewerSkillId}")
    public ResponseEntity<String> deleteInterviewerSkill(@PathVariable Long interviewerSkillId) {
        try {
            boolean deleted = interviewerSkillService.deleteInterviewerSkill(interviewerSkillId);
            if (deleted) {
                return ResponseEntity.ok("Interviewer skill deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete interviewer skill");
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}