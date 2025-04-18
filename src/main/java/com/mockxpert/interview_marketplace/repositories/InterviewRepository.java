package com.mockxpert.interview_marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mockxpert.interview_marketplace.entities.Availability;
import com.mockxpert.interview_marketplace.entities.Booking;
import com.mockxpert.interview_marketplace.entities.Interview;
import com.mockxpert.interview_marketplace.entities.Availability.AvailabilityStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
/**
 * 
 * Repository class thats reposible generating query methods related to interview.
 * 
 * @author Umar Mohammad
 */
@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
	
    /**
     * Check if an interview exists for a given booking ID.
     *
     * @param bookingId The ID of the booking.
     * @return true if an interview exists for the booking.
     */
    boolean existsByBooking_BookingId(Long bookingId);
    
    /**
     * Find interviews by interviewer ID.
     * 
     * @param interviewerId the ID of the interviewer
     * @return a list of interviews conducted by the specified interviewer
     */
    List<Interview> findByInterviewer_InterviewerId(Long interviewerId);

    /**
     * Find interviews by interviewee ID.
     * 
     * @param intervieweeId the ID of the interviewee
     * @return a list of interviews attended by the specified interviewee
     */
    List<Interview> findByInterviewee_IntervieweeId(Long intervieweeId);

    /**
     * Find an interview by booking ID.
     * 
     * @param bookingId the ID of the booking
     * @return an optional containing the interview with the specified booking ID, if found
     */
    Optional<Interview> findByBooking_BookingId(Long bookingId);

    /**
     * Find all interviews for a given status (e.g., booked, completed, cancelled).
     * 
     * @param status the status of the interviews to find
     * @return a list of interviews with the specified status
     */
    List<Interview> findByStatus(String status);

    /**
     * Find interviews by timezone.
     * 
     * @param timezone the timezone of the interviews to find
     * @return a list of interviews in the specified timezone
     */
    List<Interview> findByTimezone(String timezone);

    /**
     * Find interviews by a specific start and end time.
     * 
     * @param startTime the start time of the range
     * @param endTime the end time of the range
     * @return a list of interviews between the specified start and end times
     */
    List<Interview> findByActualStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Count total interviews for an interviewer.
     * 
     * @param interviewerId the ID of the interviewer
     * @return the total number of interviews conducted by the specified interviewer
     */
    Long countByInterviewer_InterviewerId(Long interviewerId);

    /**
     * Count total interviews for an interviewee.
     * 
     * @param intervieweeId the ID of the interviewee
     * @return the total number of interviews attended by the specified interviewee
     */
    Long countByInterviewee_IntervieweeId(Long intervieweeId);
    
    /**
     * Count scheduled interviews for a given interviewer.
     *
     * @param interviewerId the ID of the interviewer.
     * @param status        the interview status (e.g., BOOKED).
     * @return the count of scheduled interviews.
     */
    long countByInterviewer_InterviewerIdAndStatus(Long interviewerId, Interview.InterviewStatus status);


    /**
     * Feteches all the upcoming interviewes for the interviewer.
     * 
     * @param interviewerId
     * @param status
     * @param date
     * @return
     */
    List<Interview> findByInterviewer_InterviewerIdAndStatusAndDateAfterOrderByDateAsc(
    	    Long interviewerId, Interview.InterviewStatus status, LocalDateTime date);
    
    /**
     * Find all upcoming interviews for an interviewer, sorted by date.
     *
     * @param interviewerId the ID of the interviewer.
     * @param status        the interview status (e.g., BOOKED).
     * @return a list of upcoming interviews sorted by date.
     */
    @Query("SELECT i FROM Interview i WHERE i.interviewer.interviewerId = :interviewerId AND i.status = :status AND i.date >= CURRENT_TIMESTAMP ORDER BY i.date ASC")
    List<Interview> findByInterviewer_InterviewerIdAndStatusOrderByDateAsc(
        @Param("interviewerId") Long interviewerId,
        @Param("status") Interview.InterviewStatus status
    );
    
    /**
     * find all upcoming interv iewes for an interviewee, sorted by date.
     * 
     * @param intervieweeId
     * @param status
     * @return
     */
    @Query("SELECT i FROM Interview i WHERE i.interviewee.intervieweeId = :intervieweeId AND i.status = :status AND i.date >= CURRENT_TIMESTAMP ORDER BY i.date ASC")
    List<Interview> findByInterviewee_IntervieweeIdAndStatusOrderByDateAsc(
        @Param("intervieweeId") Long intervieweeId,
        @Param("status") Interview.InterviewStatus status
    );
    
    /**
     * 
     * @param intervieweeId
     * @return
     */
    @Query("""
           SELECT COUNT(i) 
           FROM Interview i 
           WHERE i.interviewee.intervieweeId = :intervieweeId
             AND (
               i.date > CURRENT_DATE
               OR (i.date = CURRENT_DATE AND i.endTime > CURRENT_TIME)
             )
           """)
    Long countUpcomingInterviews(@Param("intervieweeId") Long intervieweeId);

    /**
     * 
     * @param intervieweeId
     * @return
     */
    @Query("""
           SELECT COUNT(i)
           FROM Interview i
           WHERE i.interviewee.intervieweeId = :intervieweeId
             AND (
               i.date < CURRENT_DATE
               OR (i.date = CURRENT_DATE AND i.endTime <= CURRENT_TIME)
             )
           """)
    Long countCompletedInterviews(@Param("intervieweeId") Long intervieweeId);

    /**
     * 
     * 
     * @param intervieweeId
     * @return
     */
    @Query("""
           SELECT i
           FROM Interview i
           WHERE i.interviewee.intervieweeId = :intervieweeId
             AND (
               i.date < CURRENT_DATE
               OR (i.date = CURRENT_DATE AND i.endTime <= CURRENT_TIME)
             )
           ORDER BY i.date DESC, i.endTime DESC
           """)
    List<Interview> findCompletedInterviews(@Param("intervieweeId") Long intervieweeId);
    
    /**
     * Fetches the interviews by the date and the user.
     * 
     * @param date
     * @param userId
     * @return
     */
    @Query("""
            SELECT i
            FROM Interview i
            WHERE i.date = :date
              AND (
                i.interviewer.user.userId = :userId
                OR i.interviewee.user.userId = :userId
              )
            """)
     List<Interview> findByDateAndUser(@Param("date") LocalDate date,
                                       @Param("userId") Long userId);
    
    /**
     * Fetches the interviewes between the date range by user.
     * 
     * @param startDate
     * @param endDate
     * @param userId
     * @return
     */
    @Query("""
            SELECT i
            FROM Interview i
            WHERE i.date BETWEEN :startDate AND :endDate
              AND (
                i.interviewer.user.userId = :userId
                OR i.interviewee.user.userId = :userId
              )
            """)
     List<Interview> findByDateRangeAndUser(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate,
                                            @Param("userId") Long userId);
    
    /**
     * Fetches the interviews for the particular status and before certain end time.
     * 
     * @param status
     * @param endTime
     * @return
     */
    @Query(value = "SELECT * FROM interviews i " +
            "WHERE status = 'BOOKED' " +
            "AND end_time < CAST(CURRENT_TIMESTAMP AS time) " +
            "AND date < CURRENT_TIMESTAMP", 
    nativeQuery = true)
    List<Interview> findByStatusAndEndTimeBefore(@Param("status") Interview.InterviewStatus status,
                                                 @Param("endTime") LocalTime endTime);
    
    /**
     * Fetches all the past interviews
     * 
     * @param dbUserId
     * @return
     */
    @Query("""
    	    SELECT i
    	    FROM Interview i
    	    WHERE
    	        i.date < CURRENT_DATE
    	        OR (i.date = CURRENT_DATE AND i.endTime <= CURRENT_TIME)
    	    ORDER BY i.date DESC, i.endTime DESC
    	""")
    List<Interview> findPastInterviews(@Param("dbUserId") Long dbUserId);

}
