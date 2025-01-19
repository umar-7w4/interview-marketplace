package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.UserDto;
import com.mockxpert.interview_marketplace.entities.User;
import com.mockxpert.interview_marketplace.exceptions.*;
import com.mockxpert.interview_marketplace.mappers.UserMapper;
import com.mockxpert.interview_marketplace.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FirebaseAuth firebaseAuth;

    /**
     * Register a new user using Firebase Token.
     * @param firebaseToken the Firebase token from the user.
     * @param userDto the user data transfer object containing registration information.
     * @return the saved UserDto.
     * @throws FirebaseAuthException 
     */
    @Transactional
    public UserDto registerUserUsingFirebaseToken(String firebaseToken, UserDto userDto) throws FirebaseAuthException {
        String token = firebaseToken.replace("Bearer ", "").trim();
        System.out.println("Validating token: " + token); // Debug Log

        // Validate Firebase Token
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
        String firebaseUid = decodedToken.getUid();
        System.out.println("Decoded Token UID: " + firebaseUid); // Debug Log

        // Check if user already exists
        if (userRepository.findByFirebaseUid(firebaseUid).isPresent()) {
            throw new ConflictException("User with Firebase UID already exists.");
        }

        userDto.setFirebaseUid(firebaseUid);
        return registerUser(userDto);
    }

    /**
     * Register a new user.
     *
     * @param userDto the user data transfer object containing registration information.
     * @return the saved UserDto.
     */
    @Transactional
    public UserDto registerUser(UserDto userDto) {
        // Validate if email already exists
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists: " + userDto.getEmail());
        }

        User user = UserMapper.toEntity(userDto);
        user.setCreatedAt(LocalDateTime.now());
        user.setStatus(User.Status.PENDING); // Set default status as PENDING

        User savedUser = userRepository.saveAndFlush(user); // Use saveAndFlush to immediately persist data
        System.out.println("Saved User ID: " + savedUser.getUserId());

        return UserMapper.toDto(savedUser);
    }

    /**
     * Update user profile information.
     * @param userId the ID of the user to update.
     * @param userDto the user data transfer object containing updated information.
     * @return the updated UserDto.
     */
    @Transactional
    public UserDto updateUserProfile(Long userId, UserDto userDto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Check if the email is being updated and if it is already used by another user
        if (!existingUser.getEmail().equals(userDto.getEmail()) && userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists: " + userDto.getEmail());
        }

        existingUser.setFirstName(userDto.getFirstName());
        existingUser.setLastName(userDto.getLastName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setPhoneNumber(userDto.getPhoneNumber());
        existingUser.setProfilePictureUrl(userDto.getProfilePictureUrl());
        existingUser.setPreferredLanguage(userDto.getPreferredLanguage());
        existingUser.setTimezone(userDto.getTimezone());

        User updatedUser = userRepository.saveAndFlush(existingUser);
        return UserMapper.toDto(updatedUser);
    }

    /**
     * Find user by ID.
     * @param userId the user ID to find.
     * @return the UserDto if found.
     */
    public UserDto findUserById(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    /**
     * Login using Firebase Token.
     * @param firebaseToken the Firebase token from the user.
     * @return the UserDto of the logged-in user.
     */
    public UserDto loginUserUsingFirebaseToken(String firebaseToken) {
        try {
            String token = firebaseToken.replace("Bearer ", "").trim();
            System.out.println("Validating token: " + token); // Debug Log

            // Validate Firebase Token
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String firebaseUid = decodedToken.getUid();
            System.out.println("Decoded Token UID: " + firebaseUid); // Debug Log

            // Fetch user by Firebase UID
            User user = userRepository.findByFirebaseUid(firebaseUid)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found for Firebase UID: " + firebaseUid));
            
            user.setLastLogin(LocalDateTime.now());
            userRepository.saveAndFlush(user);
            
            return UserMapper.toDto(user);

        } catch (FirebaseAuthException e) {
            System.err.println("Firebase token validation failed: " + e.getMessage()); // Debug Log
            throw new UnauthorizedException("Invalid Firebase token.", e);
        }
    }

    /**
     * Deactivate a user.
     * @param userId the ID of the user to deactivate.
     * @return true if the user was successfully deactivated, false otherwise.
     */
    @Transactional
    public boolean deactivateUser(Long userId) {
        return userRepository.findById(userId).map(user -> {
            if (user.getStatus() == User.Status.INACTIVE) {
                throw new BadRequestException("User is already inactive.");
            }
            user.setStatus(User.Status.INACTIVE);
            userRepository.saveAndFlush(user);
            return true;
        }).orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    /**
     * Reactivate a user.
     * @param userId the ID of the user to reactivate.
     * @return true if the user was successfully reactivated, false otherwise.
     */
    @Transactional
    public boolean reactivateUser(Long userId) {
        return userRepository.findById(userId).map(user -> {
            if (user.getStatus() == User.Status.ACTIVE) {
                throw new BadRequestException("User is already active.");
            }
            user.setStatus(User.Status.ACTIVE);
            userRepository.saveAndFlush(user);
            return true;
        }).orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    /**
     * Delete a user by ID.
     * @param userId the ID of the user to delete.
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        try {
            userRepository.delete(user);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to delete user with ID: " + userId);
        }
    }

    /**
     * Find user by email.
     * @param email the email of the user to find.
     * @return the UserDto if found.
     */
    public UserDto findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Change the user's password.
     * @param userId the ID of the user to change the password.
     * @param newPassword the new password for the user.
     */
    @Transactional
    public void changeUserPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        if (newPassword == null || newPassword.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long.");
        }

        user.setPasswordHash(newPassword); // Ensure password hashing is applied
        userRepository.saveAndFlush(user);
    }

    /**
     * Helper method to get Firebase UID from token.
     * @param firebaseToken Firebase token.
     * @return Firebase UID.
     */
    private String getFirebaseUid(String firebaseToken) {
        try {
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(firebaseToken);
            return decodedToken.getUid();
        } catch (FirebaseAuthException e) {
            throw new UnauthorizedException("Invalid Firebase token.", e);
        }
    }
}
