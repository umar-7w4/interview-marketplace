package com.mockxpert.interview_marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mockxpert.interview_marketplace.entities.User;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity.
 * Handles all CRUD operations related to User data.
 * 
 * @author Umar Mohammad
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
    // Finds the most recently logged-in user
    Optional<User> findFirstByOrderByLastLoginDesc();

    /**
     * Find a user by their email address.
     * 
     * @param email the email of the user
     * @return an optional user object
     */
    Optional<User> findByEmail(String email);

    /**
     * Find users by their role (Admin, Interviewer, Interviewee).
     * 
     * @param role the role of the user (Admin, Interviewer, Interviewee)
     * @return a list of users with the specified role
     */
    List<User> findByRole(String role);

    /**
     * Find users by their status (active, inactive, pending).
     * 
     * @param status the status of the user
     * @return a list of users with the specified status
     */
    List<User> findByStatus(User.Status status);

    /**
     * Find all users who have logged in after a certain date.
     * 
     * @param lastLogin the last login date to filter users
     * @return a list of users who logged in after the specified date
     */
    List<User> findByLastLoginAfter(java.time.LocalDateTime lastLogin);

    /**
     * Check if a user exists by email.
     * 
     * @param email the email to check
     * @return true if the user exists, otherwise false
     */
    boolean existsByEmail(String email);

    /**
     * Delete a user by their email address.
     * 
     * @param email the email of the user to delete
     */
    void deleteByEmail(String email);

    /**
     * Find users by preferred language.
     * 
     * @param preferredLanguage the preferred language of the user
     * @return a list of users with the specified preferred language
     */
    List<User> findByPreferredLanguage(String preferredLanguage);
    
    /**
     * Find user by firebase uid.
     * 
     * @param firebaseUid the firebase uid of the user
     * @return an Optional of user with the specified firebase uid
     */
    Optional<User> findByFirebaseUid(String firebaseUid);
    
    /**
     * Find user by reset token.
     * 
     * @param reset token is generated during forgot password of the user
     * @return an Optional of user with the specified reset token
     */
    Optional<User> findByResetToken(String resetToken);
    
    /**
     *Fetching user record by its id
     * 
     * @param email
     * @return
     */
    Optional<User> findByUserId(Long userId);
}
