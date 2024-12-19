package com.mockxpert.interview_marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mockxpert.interview_marketplace.entities.Feedback;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    /**
     * Find all feedback for a specific interview.
     * @param interviewId the ID of the interview.
     * @return a list of feedback associated with the given interview ID.
     */
    List<Feedback> findByInterview_InterviewId(Long interviewId);

    /**
     * Find all feedback given by a specific user.
     * @param userId the ID of the user who provided the feedback.
     * @return a list of feedback given by the specified user.
     */
    List<Feedback> findByGiver_UserId(Long userId);

    /**
     * Find all feedback received by a specific user.
     * @param userId the ID of the user who received the feedback.
     * @return a list of feedback received by the specified user.
     */
    List<Feedback> findByReceiver_UserId(Long userId);

    /**
     * Find feedback by rating.
     * @param rating the rating given in the feedback (e.g., 1 to 5 or 1 to 10).
     * @return a list of feedback with the specified rating.
     */
    List<Feedback> findByRating(Integer rating);

    /**
     * Find feedback by creation date.
     * @param createdAt the date when the feedback was created.
     * @return a list of feedback created on the specified date.
     */
    List<Feedback> findByCreatedAt(LocalDate createdAt);

    /**
     * Find feedback containing specific keywords in comments.
     * @param keyword the keyword to search for in the feedback comments.
     * @return a list of feedback containing the specified keyword in comments.
     */
    List<Feedback> findByCommentsContaining(String keyword);

    /**
     * Find feedback by positives containing specific keywords.
     * @param keyword the keyword to search for in the positives.
     * @return a list of feedback where the positives contain the specified keyword.
     */
    List<Feedback> findByPositivesContaining(String keyword);

    /**
     * Find feedback by negatives containing specific keywords.
     * @param keyword the keyword to search for in the negatives.
     * @return a list of feedback where the negatives contain the specified keyword.
     */
    List<Feedback> findByNegativesContaining(String keyword);

    /**
     * Find feedback by improvements containing specific keywords.
     * @param keyword the keyword to search for in the improvements section.
     * @return a list of feedback where the improvements contain the specified keyword.
     */
    List<Feedback> findByImprovementsContaining(String keyword);

    /**
     * Count feedback entries by rating.
     * @param rating the rating to filter feedback entries.
     * @return the count of feedback entries with the given rating.
     */
    Long countByRating(Integer rating);

    /**
     * Find feedback given within a specific date range.
     * @param startDate the start date of the range.
     * @param endDate the end date of the range.
     * @return a list of feedback created within the specified date range.
     */
    List<Feedback> findByCreatedAtBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find feedback by giver ID and interview ID.
     * @param userId the ID of the user who provided the feedback.
     * @param interviewId the ID of the interview.
     * @return a list of feedback given by the specified user for the specified interview.
     */
    List<Feedback> findByGiver_UserIdAndInterview_InterviewId(Long userId, Long interviewId);

    /**
     * Find feedback by receiver ID and interview ID.
     * @param userId the ID of the user who received the feedback.
     * @param interviewId the ID of the interview.
     * @return a list of feedback received by the specified user for the specified interview.
     */
    List<Feedback> findByReceiver_UserIdAndInterview_InterviewId(Long userId, Long interviewId);
}
