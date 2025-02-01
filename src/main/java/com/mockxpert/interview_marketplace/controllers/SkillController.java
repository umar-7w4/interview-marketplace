package com.mockxpert.interview_marketplace.controllers;

import com.mockxpert.interview_marketplace.dto.SkillDto;
import com.mockxpert.interview_marketplace.exceptions.*;
import com.mockxpert.interview_marketplace.services.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    @Autowired
    private SkillService skillService;

    public SkillController() {
        System.out.println("SkillController Initialized");
    }

    /**
     * Register a new skill.
     * @param skillDto the skill data transfer object containing registration information.
     * @return the created SkillDto.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerSkill(@RequestBody @Valid SkillDto skillDto) {
        try {
            SkillDto savedSkill = skillService.registerSkill(skillDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedSkill);
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Update skill information.
     * @param skillId the ID of the skill to update.
     * @param skillDto the skill data transfer object containing updated information.
     * @return the updated SkillDto.
     */
    @PutMapping("/{skillId}")
    public ResponseEntity<?> updateSkill(@PathVariable Long skillId, @RequestBody @Valid SkillDto skillDto) {
        try {
            SkillDto updatedSkill = skillService.updateSkill(skillId, skillDto);
            return ResponseEntity.ok(updatedSkill);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Find a skill by ID.
     * @param skillId the ID of the skill to retrieve.
     * @return the SkillDto.
     */
    @GetMapping("/{skillId}")
    public ResponseEntity<?> findSkillById(@PathVariable Long skillId) {
        try {
            SkillDto skill = skillService.findSkillById(skillId);
            return ResponseEntity.ok(skill);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Delete a skill by ID.
     * @param skillId the ID of the skill to delete.
     * @return a response indicating success.
     */
    @DeleteMapping("/{skillId}")
    public ResponseEntity<String> deleteSkill(@PathVariable Long skillId) {
        try {
            boolean deleted = skillService.deleteSkill(skillId);
            if (deleted) {
                return ResponseEntity.ok("Skill deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete skill");
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Retrieve all skills.
     * @return a list of SkillDto objects.
     */
    @GetMapping
    public ResponseEntity<?> getAllSkills() {
        try {
            List<SkillDto> skills = skillService.findAllSkills();
            return ResponseEntity.ok(skills);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}