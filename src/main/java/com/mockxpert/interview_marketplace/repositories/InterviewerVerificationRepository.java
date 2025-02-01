package com.mockxpert.interview_marketplace.repositories;

import com.mockxpert.interview_marketplace.entities.InterviewerVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for InterviewerVerification entity.
 */
@Repository
public interface InterviewerVerificationRepository extends JpaRepository<InterviewerVerification, Long> {

    /**
     * Finds an InterviewerVerification record by its verification token.
     *
     * @param token the verification token.
     * @return an Optional containing the InterviewerVerification if found, otherwise empty.
     */
    Optional<InterviewerVerification> findByVerificationToken(String token);

    /**
     * Finds an InterviewerVerification record by the interviewer's ID.
     *
     * @param interviewerId the ID of the interviewer.
     * @return an Optional containing the InterviewerVerification if found, otherwise empty.
     */
    Optional<InterviewerVerification> findByInterviewer_InterviewerId(Long interviewerId);
}
