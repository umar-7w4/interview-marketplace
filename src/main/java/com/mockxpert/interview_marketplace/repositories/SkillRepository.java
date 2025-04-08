package com.mockxpert.interview_marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mockxpert.interview_marketplace.entities.Skill;

import java.util.List;

/**
 * 
 * Repository class thats reposible generating query methods related to skills.
 * 
 * @author Umar Mohammad
 */
@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    /**
     * Find skill by name.
     * 
     * @param name the name of the skill.
     * @return a skill with the specified name.
     */
    Skill findByName(String name);

    /**
     * Find skills by description containing a keyword.
     * 
     * @param keyword the keyword to search within the skill description.
     * @return a list of skills that contain the keyword in their description.
     */
    List<Skill> findByDescriptionContaining(String keyword);

    /**
     * Find skills by their IDs.
     * 
     * @param ids the list of skill IDs.
     * @return a list of skills with the specified IDs.
     */
    //List<Skill> findByIdIn(List<Long> ids);

    /**
     * Count skills by name.
     * 
     * @param name the name of the skill to count.
     * @return the count of skills with the specified name.
     */
    Long countByName(String name);

    /**
     * Find skills by a partial match of their name.
     * 
     * @param partialName the partial name to match.
     * @return a list of skills that match the partial name.
     */
    List<Skill> findByNameContaining(String partialName);

    /**
     * Find skills with no associated description.
     * 
     * @return a list of skills with no description.
     */
    List<Skill> findByDescriptionIsNull();

    /**
     * Check if a skill with the given name exists.
     * 
     * @param name the name of the skill to check.
     * @return true if a skill with the given name exists, false otherwise.
     */
    boolean existsByName(String name);
}
