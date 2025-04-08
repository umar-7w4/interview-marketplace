package com.mockxpert.interview_marketplace.repositories;

import com.mockxpert.interview_marketplace.entities.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing UserVerification entities.
 * This interface extends JpaRepository to provide CRUD operations for UserVerification.
 * It also declares a custom method for finding a verification record by the associated user ID.
 *
 * Usage:
 * <pre>
 *     Optional&lt;UserVerification&gt; verification = userVerificationRepository.findByUserId(userId);
 * </pre>
 *
 * If a verification record exists for the given userId, it is returned wrapped in an Optional;
 * otherwise, an empty Optional is returned.
 *
 * Example:
 * <pre>
 *     userVerificationRepository.findByUserId(123L)
 *         .orElseThrow(() -> new ResourceNotFoundException("Verification not found for user ID: 123"));
 * </pre>
 */
@Repository
public interface UserVerificationRepository extends JpaRepository<UserVerification, Long> {

    /**
     * Finds a UserVerification record by the associated user's ID.
     *
     * @param userId the ID of the user whose verification record is to be retrieved.
     * @return an Optional containing the UserVerification record if found, or empty if not.
     */
    Optional<UserVerification> findByUser_UserId(Long userId);
}
