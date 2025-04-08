package com.mockxpert.interview_marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mockxpert.interview_marketplace.entities.InterviewerSkill;

import java.util.List;
import java.util.Optional;

/**
 * 
 * Repository class thats reposible generating query methods related to interviewer skills.
 * 
 * @author Umar Mohammad
 */
@Repository
public interface InterviewerSkillRepository extends JpaRepository<InterviewerSkill, Long> {

    /**
     * Find skills by interviewer ID.
     * 
     * @param interviewerId the ID of the interviewer.
     * @return a list of InterviewerSkill records associated with the given interviewer.
     */
    List<InterviewerSkill> findByInterviewer_InterviewerId(Long interviewerId);

    /**
     * Find all InterviewerSkill records by skill ID.
     * 
     * @param skillId the ID of the skill.
     * @return a list of InterviewerSkill records associated with the given skill.
     */
    List<InterviewerSkill> findBySkill_SkillId(Long skillId);

    /**
     * Find an InterviewerSkill record by interviewer ID and skill ID.
     * 
     * @param interviewerId the ID of the interviewer.
     * @param skillId the ID of the skill.
     * @return an optional InterviewerSkill record matching the given interviewer and skill IDs.
     */
    Optional<InterviewerSkill> findByInterviewer_InterviewerIdAndSkill_SkillId(Long interviewerId, Long skillId);

    /**
     * Find InterviewerSkill records by proficiency level.
     * 
     * @param proficiencyLevel the proficiency level (e.g., Beginner, Intermediate, Expert).
     * @return a list of InterviewerSkill records matching the specified proficiency level.
     */
    List<InterviewerSkill> findByProficiencyLevel(String proficiencyLevel);

    /**
     * Find InterviewerSkill records by certification status.
     * 
     * @param certified whether the skill is certified (true/false).
     * @return a list of InterviewerSkill records with the given certification status.
     */
    List<InterviewerSkill> findByCertified(Boolean certified);

    /**
     * Find InterviewerSkill records by years of experience.
     * 
     * @param yearsOfExperience the years of experience in the skill.
     * @return a list of InterviewerSkill records matching the specified years of experience.
     */
    List<InterviewerSkill> findByYearsOfExperience(int yearsOfExperience);

    /**
     * Count InterviewerSkill records by skill ID.
     * 
     * @param skillId the ID of the skill.
     * @return the count of InterviewerSkill records associated with the given skill ID.
     */
    Long countBySkill_SkillId(Long skillId);

    /**
     * Find InterviewerSkill records where proficiency level contains specific keywords.
     * 
     * @param keyword the keyword to search for in proficiency level.
     * @return a list of InterviewerSkill records with proficiency level containing the given keyword.
     */
    List<InterviewerSkill> findByProficiencyLevelContaining(String keyword);
}
