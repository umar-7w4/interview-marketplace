package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.SkillDto;
import com.mockxpert.interview_marketplace.entities.Skill;
import com.mockxpert.interview_marketplace.exceptions.*;
import com.mockxpert.interview_marketplace.mappers.SkillMapper;
import com.mockxpert.interview_marketplace.repositories.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillService {

    @Autowired
    private SkillRepository skillRepository;

    /**
     * Register a new skill.
     * @param skillDto the skill data transfer object containing registration information.
     * @return the saved Skill entity.
     */
    @Transactional
    public SkillDto registerSkill(SkillDto skillDto) {
        if (skillRepository.existsByName(skillDto.getName())) {
            throw new ConflictException("Skill with name '" + skillDto.getName() + "' already exists.");
        }

        Skill skill = SkillMapper.toEntity(skillDto);
        try {
            Skill savedSkill = skillRepository.save(skill);
            return SkillMapper.toDto(savedSkill);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to register skill due to server error.");
        }
    }

    /**
     * Update skill information.
     * @param skillId the ID of the skill to update.
     * @param skillDto the skill data transfer object containing updated information.
     * @return the updated Skill entity.
     */
    @Transactional
    public SkillDto updateSkill(Long skillId, SkillDto skillDto) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + skillId));

        if (skillDto.getName() != null) {
            if (skillRepository.existsByName(skillDto.getName()) && !skill.getName().equals(skillDto.getName())) {
                throw new ConflictException("Skill with name '" + skillDto.getName() + "' already exists.");
            }
            skill.setName(skillDto.getName());
        }

        if (skillDto.getDescription() != null) {
            skill.setDescription(skillDto.getDescription());
        }

        try {
            Skill updatedSkill = skillRepository.save(skill);
            return SkillMapper.toDto(updatedSkill);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to update skill due to server error.");
        }
    }

    /**
     * Find a skill by its ID.
     * @param skillId the skill ID to look up.
     * @return the found Skill entity as a DTO.
     */
    public SkillDto findSkillById(Long skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + skillId));
        return SkillMapper.toDto(skill);
    }

    /**
     * Delete a skill by its ID.
     * @param skillId the ID of the skill to delete.
     * @return true if the skill was deleted successfully, false otherwise.
     */
    @Transactional
    public boolean deleteSkill(Long skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + skillId));

        try {
            skillRepository.delete(skill);
            return true;
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to delete skill due to server error.");
        }
    }

    /**
     * Get a list of all skills.
     * @return a list of all Skill entities as DTOs.
     */
    public List<SkillDto> findAllSkills() {
        try {
            List<Skill> skills = skillRepository.findAll();
            return skills.stream()
                    .map(SkillMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to retrieve skills due to server error.");
        }
    }
}
