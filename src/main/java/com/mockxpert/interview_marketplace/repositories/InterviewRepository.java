package com.mockxpert.interview_marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mockxpert.interview_marketplace.entities.Interview;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    /**
     * Find interviews by interviewer ID.
     * @param interviewerId the ID of the interviewer
     * @return a list of interviews conducted by the specified interviewer
     */
    List<Interview> findByInterviewer_InterviewerId(Long interviewerId);

    /**
     * Find interviews by interviewee ID.
     * @param intervieweeId the ID of the interviewee
     * @return a list of interviews attended by the specified interviewee
     */
    List<Interview> findByInterviewee_IntervieweeId(Long intervieweeId);

    /**
     * Find an interview by booking ID.
     * @param bookingId the ID of the booking
     * @return an optional containing the interview with the specified booking ID, if found
     */
    Optional<Interview> findByBooking_BookingId(Long bookingId);

    /**
     * Find all interviews for a given status (e.g., booked, completed, cancelled).
     * @param status the status of the interviews to find
     * @return a list of interviews with the specified status
     */
    List<Interview> findByStatus(String status);

    /**
     * Find interviews for an interviewer with a specific time slot.
     * @param interviewerId the ID of the interviewer
     * @param timeSlot the time slot of the interview
     * @return an optional containing the interview for the given interviewer and time slot, if found
     */
    //Optional<Interview> findByInterviewer_InterviewerIdAndTimeSlot(Long interviewerId, String timeSlot);

    /**
     * Find upcoming interviews for an interviewee.
     * @param intervieweeId the ID of the interviewee
     * @param status the status of the interviews to find (e.g., booked)
     * @return a list of upcoming interviews for the specified interviewee
     */
    //List<Interview> findByInterviewee_IntervieweeIdAndStatusOrderByTimeSlotAsc(Long intervieweeId, String status);

    /**
     * Find interviews by timezone.
     * @param timezone the timezone of the interviews to find
     * @return a list of interviews in the specified timezone
     */
    List<Interview> findByTimezone(String timezone);

    /**
     * Find interviews by a specific start and end time.
     * @param startTime the start time of the range
     * @param endTime the end time of the range
     * @return a list of interviews between the specified start and end times
     */
    List<Interview> findByActualStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Count total interviews for an interviewer.
     * @param interviewerId the ID of the interviewer
     * @return the total number of interviews conducted by the specified interviewer
     */
    Long countByInterviewer_InterviewerId(Long interviewerId);

    /**
     * Count total interviews for an interviewee.
     * @param intervieweeId the ID of the interviewee
     * @return the total number of interviews attended by the specified interviewee
     */
    Long countByInterviewee_IntervieweeId(Long intervieweeId);
}
