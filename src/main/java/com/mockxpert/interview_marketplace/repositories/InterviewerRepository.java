package com.mockxpert.interview_marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mockxpert.interview_marketplace.entities.Interviewer;

import java.util.List;
import java.util.Optional;

/**
 * 
 * Repository class thats reposible generating query methods related to interviewer.
 * 
 * @author Umar Mohammad
 */
@Repository
public interface InterviewerRepository extends JpaRepository<Interviewer, Long> {

    /**
     * Find an interviewer by user ID.
     * 
     * @param userId the ID of the user.
     * @return an optional containing the interviewer if found, otherwise empty.
     */
    Optional<Interviewer> findByUser_UserId(Long userId);

    /**
     * Find all interviewers with a specific status.
     * 
     * @param status the status to filter interviewers (e.g., verified, not verified).
     * @return a list of interviewers matching the specified status.
     */
    List<Interviewer> findByStatus(String status);

    /**
     * Find interviewers by the company they work at.
     * 
     * @param company the company name to filter interviewers.
     * @return a list of interviewers working at the specified company.
     */
    List<Interviewer> findByCurrentCompany(String company);

    /**
     * Find interviewers by years of experience.
     * 
     * @param yearsOfExperience the number of years of experience to filter interviewers.
     * @return a list of interviewers with the specified years of experience.
     */
    List<Interviewer> findByYearsOfExperience(Integer yearsOfExperience);

    /**
     * Find interviewers by skill name.
     * 
     * @param skillName the name of the skill to filter interviewers.
     * @return a list of interviewers having the specified skill.
     */
    //List<Interviewer> findBySkills_Name(String skillName);

    /**
     * Find interviewers who speak a specific language.
     * 
     * @param language the language to filter interviewers by.
     * @return a list of interviewers who speak the specified language.
     */
    List<Interviewer> findByLanguagesSpokenContaining(String language);

    /**
     * Find all interviewers within a specified session rate range.
     * 
     * @param minRate the minimum session rate.
     * @param maxRate the maximum session rate.
     * @return a list of interviewers whose session rate falls within the specified range.
     */
    List<Interviewer> findBySessionRateBetween(Double minRate, Double maxRate);

    /**
     * Count the total number of interviewers with a specific status.
     * 
     * @param status the status to filter interviewers.
     * @return the count of interviewers with the specified status.
     */
    Long countByStatus(String status);

    /**
     * Find all verified interviewers.
     * 
     * @return a list of interviewers who have been verified.
     */
    List<Interviewer> findByIsVerifiedTrue();

    /**
     * Find interviewers who have completed their profile.
     * 
     * @param profileCompletionStatus the profile completion status to filter interviewers.
     * @return a list of interviewers with the specified profile completion status.
     */
    List<Interviewer> findByProfileCompletionStatus(Boolean profileCompletionStatus);
    
    /**
     * Checks if the interviewer record exists or not.
     * 
     * @param userId
     * @return
     */
    boolean existsByUser_UserId(Long userId);

}
