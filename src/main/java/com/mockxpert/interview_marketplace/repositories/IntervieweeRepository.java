package com.mockxpert.interview_marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mockxpert.interview_marketplace.entities.Interviewee;

import java.util.List;
import java.util.Optional;

/**
 * 
 * Repository class thats reposible generating query methods related to interviewee.
 * 
 * @author Umar Mohammad
 */
@Repository
public interface IntervieweeRepository extends JpaRepository<Interviewee, Long> {

    /**
     * Find interviewee by user ID.
     * @param userId the user ID associated with the interviewee.
     * @return an optional interviewee that matches the user ID.
     */
    Optional<Interviewee> findByUser_UserId(Long userId);

    /**
     * Find interviewees by current role.
     * @param currentRole the current role of the interviewee.
     * @return a list of interviewees with the specified current role.
     */
    List<Interviewee> findByCurrentJobRole(String currentJobRole);

    /**
     * Find interviewees by field of interest.
     * @param fieldOfInterest the field of interest of the interviewee.
     * @return a list of interviewees with the specified field of interest.
     */
    List<Interviewee> findByFieldOfInterest(String fieldOfInterest);

    /**
     * Find interviewees by education level.
     * @param educationLevel the education level of the interviewee.
     * @return a list of interviewees with the specified education level.
     */
    List<Interviewee> findByEducationLevel(String educationLevel);

    /**
     * Find interviewees who have uploaded a resume.
     * @return a list of interviewees who have uploaded a resume.
     */
    List<Interviewee> findByResumeIsNotNull();

    /**
     * Count total interviewees based on field of interest.
     * @param fieldOfInterest the field of interest to filter interviewees.
     * @return the count of interviewees with the specified field of interest.
     */
    Long countByFieldOfInterest(String fieldOfInterest);

    /**
     * Find interviewees by language spoken.
     * @param language the language spoken by the interviewee.
     * @return a list of interviewees who speak the specified language.
     */
    List<Interviewee> findByLanguagesSpokenContaining(String language);

    /**
     * Find interviewees by timezone.
     * @param timezone the timezone of the interviewee.
     * @return a list of interviewees in the specified timezone.
     */
    List<Interviewee> findByTimezone(String timezone);

    /**
     * Find interviewees by current role and field of interest.
     * @param currentRole the current role of the interviewee.
     * @param fieldOfInterest the field of interest of the interviewee.
     * @return a list of interviewees that match the current role and field of interest.
     */
    List<Interviewee> findByCurrentJobRoleAndFieldOfInterest(String currentJobRole, String fieldOfInterest);
    
    /**
     * Fetches the interviewee by user.
     * 
     * @param userId
     * @return
     */
    @Query("SELECT i.intervieweeId FROM Interviewee i WHERE i.user.userId = :userId")
    Long findIntervieweeIdByUserId(@Param("userId") Long userId);
    
    /**
     * Checks whether we have a interviewee profile created for current user.
     * 
     * @param userId
     * @return
     */
    boolean existsByUser_UserId(Long userId);


}
