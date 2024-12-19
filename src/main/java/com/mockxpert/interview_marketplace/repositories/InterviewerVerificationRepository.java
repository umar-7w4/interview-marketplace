package com.mockxpert.interview_marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mockxpert.interview_marketplace.entities.InterviewerVerification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewerVerificationRepository extends JpaRepository<InterviewerVerification, Long> {

    /**
     * Find interviewer verification by interviewer ID.
     * @param interviewerId the interviewer ID associated with the verification.
     * @return an optional verification record for the given interviewer ID.
     */
    Optional<InterviewerVerification> findByInterviewer_InterviewerId(Long interviewerId);

    /**
     * Find all verifications with a specific status.
     * @param status the status of the verification (e.g., pending).
     * @return a list of verifications with the specified status.
     */
    List<InterviewerVerification> findByStatus(String status);

    /**
     * Find verifications reviewed by a specific admin (user ID).
     * @param userId the admin ID who verified the record.
     * @return a list of verifications verified by the given admin.
     */
    //List<InterviewerVerification> findByVerifiedBy_UserId(Long userId);

    /**
     * Find verifications by document type.
     * @param documentType the type of document uploaded for verification.
     * @return a list of verifications matching the specified document type.
     */
    List<InterviewerVerification> findByDocumentType(String documentType);

    /**
     * Count verifications by status.
     * @param status the status to filter verifications.
     * @return the count of verifications with the given status.
     */
    Long countByStatus(String status);

    /**
     * Find verifications by upload date.
     * @param uploadDate the date the document was uploaded.
     * @return a list of verifications uploaded on the specified date.
     */
    List<InterviewerVerification> findByUploadDate(LocalDate uploadDate);

    /**
     * Find verifications by verification date.
     * @param verificationDate the date the verification was completed.
     * @return a list of verifications verified on the specified date.
     */
    List<InterviewerVerification> findByVerificationDate(LocalDate verificationDate);

    /**
     * Find verifications with comments containing specific keywords.
     * @param keyword the keyword to search for in verification comments.
     * @return a list of verifications with comments containing the given keyword.
     */
    List<InterviewerVerification> findByVerificationCommentsContaining(String keyword);
}
