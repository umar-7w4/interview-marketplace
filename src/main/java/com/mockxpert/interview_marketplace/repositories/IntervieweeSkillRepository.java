package com.mockxpert.interview_marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mockxpert.interview_marketplace.entities.IntervieweeSkill;

import java.util.List;
import java.util.Optional;

@Repository
public interface IntervieweeSkillRepository extends JpaRepository<IntervieweeSkill, Long> {

    /**
     * Find all skills associated with a specific interviewee.
     * @param intervieweeId the interviewee ID to filter skills.
     * @return a list of IntervieweeSkill records for the specified interviewee.
     */
    List<IntervieweeSkill> findByInterviewee_IntervieweeId(Long intervieweeId);

    /**
     * Find all interviewees with a specific skill.
     * @param skillId the skill ID to filter interviewees.
     * @return a list of IntervieweeSkill records with the specified skill ID.
     */
    List<IntervieweeSkill> findBySkill_SkillId(Long skillId);

    /**
     * Find skills by interviewee and proficiency level.
     * @param intervieweeId the interviewee ID to filter skills.
     * @param proficiencyLevel the level of proficiency for the skills.
     * @return a list of IntervieweeSkill records for the specified interviewee and proficiency level.
     */
    List<IntervieweeSkill> findByInterviewee_IntervieweeIdAndProficiencyLevel(Long intervieweeId, String proficiencyLevel);

    /**
     * Find skills by interviewee and certification status.
     * @param intervieweeId the interviewee ID to filter skills.
     * @param certified the certification status (true/false).
     * @return a list of IntervieweeSkill records for the specified interviewee and certification status.
     */
    List<IntervieweeSkill> findByInterviewee_IntervieweeIdAndCertified(Long intervieweeId, Boolean certified);

    /**
     * Find interviewees with a specific skill and proficiency level.
     * @param skillId the skill ID to filter interviewees.
     * @param proficiencyLevel the level of proficiency.
     * @return a list of IntervieweeSkill records matching the skill ID and proficiency level.
     */
    List<IntervieweeSkill> findBySkill_SkillIdAndProficiencyLevel(Long skillId, String proficiencyLevel);

    /**
     * Count interviewees with a specific skill.
     * @param skillId the skill ID to count interviewees.
     * @return the count of IntervieweeSkill records for the specified skill ID.
     */
    Long countBySkill_SkillId(Long skillId);

    /**
     * Find interviewees with a specific certification status.
     * @param certified the certification status (true/false) to filter interviewees.
     * @return a list of IntervieweeSkill records with the specified certification status.
     */
    List<IntervieweeSkill> findByCertified(Boolean certified);

    /**
     * Find interviewee skills by years of experience.
     * @param intervieweeId the interviewee ID to filter skills.
     * @param yearsOfExperience the years of experience to filter skills.
     * @return a list of IntervieweeSkill records for the specified interviewee and years of experience.
     */
    List<IntervieweeSkill> findByInterviewee_IntervieweeIdAndYearsOfExperience(Long intervieweeId, Integer yearsOfExperience);

    /**
     * Find skills with a specific proficiency level across all interviewees.
     * @param proficiencyLevel the level of proficiency to filter skills.
     * @return a list of IntervieweeSkill records with the specified proficiency level.
     */
    List<IntervieweeSkill> findByProficiencyLevel(String proficiencyLevel);

    /**
     * Find interviewee skill by interviewee ID and skill ID.
     * @param intervieweeId the interviewee ID to filter.
     * @param skillId the skill ID to filter.
     * @return an optional IntervieweeSkill record for the specified interviewee and skill.
     */
    Optional<IntervieweeSkill> findByInterviewee_IntervieweeIdAndSkill_SkillId(Long intervieweeId, Long skillId);
}
