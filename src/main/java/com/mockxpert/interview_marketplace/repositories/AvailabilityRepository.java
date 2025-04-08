package com.mockxpert.interview_marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mockxpert.interview_marketplace.entities.Availability;
import com.mockxpert.interview_marketplace.entities.Availability.AvailabilityStatus;

import jakarta.persistence.LockModeType;

import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 
 * Repository class thats reposible generating query methods related to availability.
 * 
 * @author Umar Mohammad
 */
@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    /**
     * Find availability associated with the availability id by forcing optimistic locking at database level.
     * 
     * @param availability id is the ID of the Availability.
     * @return an optional availability for the specified availability ID.
     */
    @Lock(LockModeType.OPTIMISTIC)
    Optional<Availability> findById(Long id);
    
    /**
     * Find all availability slots for a specific interviewer.
     * 
     * @param interviewerId the ID of the interviewer.
     * @return a list of availability slots for the specified interviewer.
     */
    List<Availability> findByInterviewer_InterviewerId(Long interviewerId);

    /**
     * Find availability slots by date.
     * 
     * @param date the date of the availability slots.
     * @return a list of availability slots on the specified date.
     */
    List<Availability> findByDate(LocalDate date);

    /**
     * Find availability slots by status.
     * 
     * @param status the status of the availability (e.g., available/booked/expired).
     * @return a list of availability slots with the specified status.
     */
    List<Availability> findByStatus(Availability.AvailabilityStatus status);

    /**
     * Find availability slots by interviewer and date.
     * 
     * @param interviewerId the ID of the interviewer.
     * @param date the date of the availability slots.
     * @return a list of availability slots for the specified interviewer on the given date.
     */
    List<Availability> findByInterviewer_InterviewerIdAndDate(Long interviewerId, LocalDate date);

    /**
     * Find availability slots between specific start and end times.
     * 
     * @param startTime the start time of the availability slots.
     * @param endTime the end time of the availability slots.
     * @return a list of availability slots between the specified start and end times.
     */
    List<Availability> findByStartTimeBetween(LocalTime startTime, LocalTime endTime);

    /**
     * Find availability slots for a specific interviewer between specific start and end times.
     * 
     * @param interviewerId the ID of the interviewer.
     * @param startTime the start time of the availability slots.
     * @param endTime the end time of the availability slots.
     * @return a list of availability slots for the specified interviewer between the given times.
     */
    List<Availability> findByInterviewer_InterviewerIdAndStartTimeBetween(Long interviewerId, LocalTime startTime, LocalTime endTime);

    /**
     * Count availability slots by interviewer ID and status.
     * 
     * @param interviewerId the ID of the interviewer.
     * @param status the status of the availability slots.
     * @return the count of availability slots for the specified interviewer with the given status.
     */
    Long countByInterviewer_InterviewerIdAndStatus(Long interviewerId, Availability.AvailabilityStatus status);

    /**
     * Find all expired availability slots.
     * 
     * @param status the status indicating expired availability.
     * @param date the date before which the availability is considered expired.
     * @return a list of expired availability slots.
     */
    List<Availability> findByStatusAndDateBefore(Availability.AvailabilityStatus status, LocalDate date);

    /**
     * Find availability by interviewer, date, and time range.
     * 
     * @param interviewerId the ID of the interviewer.
     * @param date the date of the availability.
     * @param startTime the start time of the availability.
     * @param endTime the end time of the availability.
     * @return a list of availability slots matching the criteria.
     */
    List<Availability> findByInterviewer_InterviewerIdAndDateAndStartTimeAndEndTime(Long interviewerId, LocalDate date, LocalTime startTime, LocalTime endTime);

    /**
     * Find availability slots by interviewer ID and status.
     * 
     * @param interviewerId the ID of the interviewer.
     * @param status the status of the availability slots.
     * @return a list of availability slots for the specified interviewer with the given status.
     */
    List<Availability> findByInterviewer_InterviewerIdAndStatus(Long interviewerId, Availability.AvailabilityStatus status);
    
    
    /**
     * Fetches all the availabilities
     */
    List<Availability> findAll();
    
    /**
     * Find availability slots by filters.
     * 
     * @param start date, end date, timezone, and status.
     * @return a list of availability slots for the specified filter.
     */
    @Query("SELECT a FROM Availability a " +
            "WHERE (:startDate IS NULL OR a.date >= :startDate) " +
            "  AND (:endDate IS NULL OR a.date <= :endDate) " +
            "  AND (:timezone IS NULL OR a.timezone = :timezone) " +
            "  AND (:status IS NULL OR a.status = :status) " +
            "  AND a.interviewer.user.userId = :userId")
     List<Availability> filterAvailabilities(
         @Param("startDate") LocalDate startDate,
         @Param("endDate") LocalDate endDate,
         @Param("timezone") String timezone,
         @Param("status") AvailabilityStatus status,
         @Param("userId") Long userId
     );
}
