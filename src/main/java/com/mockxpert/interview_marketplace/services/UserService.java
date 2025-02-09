package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.UserDto;
import com.mockxpert.interview_marketplace.dto.LoginRequest;
import com.mockxpert.interview_marketplace.dto.LoginResponse;
import com.mockxpert.interview_marketplace.dto.NotificationDto;
import com.mockxpert.interview_marketplace.dto.FirebaseLoginResponse;
import com.mockxpert.interview_marketplace.dto.FirebaseTokenResponse;
import com.mockxpert.interview_marketplace.entities.User;
import com.mockxpert.interview_marketplace.exceptions.BadRequestException;
import com.mockxpert.interview_marketplace.exceptions.ConflictException;
import com.mockxpert.interview_marketplace.exceptions.InternalServerErrorException;
import com.mockxpert.interview_marketplace.exceptions.ResourceNotFoundException;
import com.mockxpert.interview_marketplace.exceptions.UnauthorizedException;
import com.mockxpert.interview_marketplace.exceptions.ValidationException;
import com.mockxpert.interview_marketplace.mappers.UserMapper;
import com.mockxpert.interview_marketplace.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FirebaseAuth firebaseAuth;
    
    @Autowired
    private FirebaseTokenService firebaseTokenService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private NotificationService notificationService;
    
    @Value("${firebase.api.key}")
    private String firebaseApiKey;
    
    private static final String FIREBASE_LOGIN_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=";

    /**
     * Registers a new user.
     * Validates that the password and confirm password match, encrypts the password before storage,
     * creates a user in Firebase Authentication, and stores user details in the local database.
     *
     * @param userDto User registration details.
     * @return Registered user details.
     */
    @Transactional
    public UserDto registerUser(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists: " + userDto.getEmail());
        }
        if (!userDto.getPasswordHash().equals(userDto.getConfirmPassword())) {
            throw new ValidationException("Password and confirm password do not match.");
        }
        
        // Create a new user in Firebase Authentication.
        final String firebaseUid = createFirebaseUser(userDto.getEmail(), userDto.getPasswordHash());
        
        // Authenticate with Firebase to retrieve tokens.
        final FirebaseLoginResponse firebaseResponse = authenticateWithFirebase(userDto.getEmail(), userDto.getPasswordHash());
        
        // Convert DTO to entity and update fields.
        final User user = UserMapper.toEntity(userDto);
        final String encryptedPassword = passwordEncoder.encode(userDto.getPasswordHash());
        user.setPassword(encryptedPassword);
        user.setFirebaseUid(firebaseUid);
        user.setRefreshToken(firebaseResponse.getRefreshToken());
        user.setCreatedAt(LocalDateTime.now());
        user.setStatus(user.getRole() == User.Role.INTERVIEWER ? User.Status.PENDING : User.Status.ACTIVE);
        
        final User savedUser = userRepository.saveAndFlush(user);
        
        sendUserNotification(
        	    savedUser.getUserId(),
        	    "Welcome to MockXpert - Your Interview Prep Partner!",
        	    "Hi " + savedUser.getFirstName() + ",<br><br>" +
        	    "Thank you for signing up with <strong>MockXpert</strong>. " +
        	    "We're excited to help you ace your interviews!<br><br>" +
        	    "You can start booking interview sessions now.<br><br>" +
        	    "<a href='https://mockxpert.com/dashboard'>Go to Dashboard</a><br><br>" +
        	    "Best Regards,<br>MockXpert Team"
        	);

        return UserMapper.toDto(savedUser);
    }

    /**
     * Creates a new user in Firebase Authentication.
     * Firebase handles password storage, encryption, and authentication.
     *
     * @param email    User email.
     * @param password User password.
     * @return Firebase UID of the newly created user.
     */
    private String createFirebaseUser(String email, String password) {
        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password)
                    .setEmailVerified(false)
                    .setDisabled(false);
            UserRecord userRecord = firebaseAuth.createUser(request);
            return userRecord.getUid();
        } catch (FirebaseAuthException e) {
            logger.error("Error creating Firebase user for email: {}", email, e);
            throw new InternalServerErrorException("Failed to create user in Firebase: " + e.getMessage());
        }
    }

    /**
     * Authenticates a user with Firebase.
     * Calls Firebase Authentication API to verify credentials and retrieve authentication tokens.
     *
     * @param email    User email.
     * @param password User password.
     * @return FirebaseLoginResponse containing authentication tokens.
     */
    private FirebaseLoginResponse authenticateWithFirebase(String email, String password) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("password", password);
        requestBody.put("returnSecureToken", true);
        RestTemplate restTemplate = new RestTemplate();
        FirebaseLoginResponse firebaseResponse;
        try {
            firebaseResponse = restTemplate.postForObject(
                    FIREBASE_LOGIN_URL + firebaseApiKey,
                    requestBody,
                    FirebaseLoginResponse.class
            );
        } catch (Exception e) {
            logger.error("Firebase authentication failed for email: {}", email, e);
            throw new UnauthorizedException("Invalid email or password.");
        }
        return firebaseResponse;
    }

    /**
     * Logs in a user using Firebase authentication.
     * Retrieves the Firebase UID, ID token, and refresh token.
     *
     * @param loginRequest Contains user login credentials.
     * @return LoginResponse with authentication tokens and user details.
     */
    @Transactional
    public LoginResponse loginUser(LoginRequest loginRequest) {
        final FirebaseLoginResponse firebaseResponse = authenticateWithFirebase(loginRequest.getEmail(), loginRequest.getPassword());
        final String firebaseUid = firebaseResponse.getLocalId();
        final String idToken = firebaseResponse.getIdToken();
        final String refreshToken = firebaseResponse.getRefreshToken();
        final User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + loginRequest.getEmail()));
        user.setFirebaseUid(firebaseUid);
        user.setRefreshToken(refreshToken);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        return new LoginResponse(idToken, refreshToken, user.getRole(), user.getEmail());
    }

    /**
     * Refreshes a Firebase token using the stored refresh token.
     *
     * @param firebaseRefreshToken The Firebase refresh token.
     * @return FirebaseTokenResponse containing the new ID token and related fields.
     */
    public FirebaseTokenResponse refreshFirebaseToken(String firebaseRefreshToken) {
        return firebaseTokenService.refreshIdToken(firebaseRefreshToken);
    }

    /**
     * Retrieves user details by email.
     *
     * @param email User email.
     * @return UserDto containing user details.
     */
    public UserDto findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Updates the user's profile information.
     *
     * @param userId  User ID.
     * @param userDto Updated user details.
     * @return UserDto containing updated user information.
     */
    @Transactional
    public UserDto updateUserProfile(Long userId, UserDto userDto) {
        final User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        if (!existingUser.getEmail().equals(userDto.getEmail()) && userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists: " + userDto.getEmail());
        }
        existingUser.setFirstName(userDto.getFirstName());
        existingUser.setLastName(userDto.getLastName());
        existingUser.setPhoneNumber(userDto.getPhoneNumber());
        existingUser.setProfilePictureUrl(userDto.getProfilePictureUrl());
        existingUser.setPreferredLanguage(userDto.getPreferredLanguage());
        existingUser.setTimezone(userDto.getTimezone());
        
        sendUserNotification(
        	    userId,
        	    "Profile Updated Successfully!",
        	    "Hi " + existingUser.getFirstName() + ",<br><br>" +
        	    "Your profile details have been successfully updated.<br><br>" +
        	    "If you didn't make these changes, please contact support immediately.<br><br>" +
        	    "<a href='https://mockxpert.com/settings'>Review Your Profile</a><br><br>" +
        	    "Best Regards,<br>MockXpert Team"
        	);
        
        return UserMapper.toDto(userRepository.saveAndFlush(existingUser));
    }

    /**
     * Changes the user's password.
     * Validates that the new password and confirm password match, then encrypts the new password.
     *
     * @param userId          User ID.
     * @param newPassword     New password.
     * @param confirmPassword Confirmation of the new password.
     */
    @Transactional
    public void changeUserPassword(Long userId, String newPassword, String confirmPassword) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        if (newPassword == null || newPassword.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new ValidationException("Password and confirm password do not match.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        
        sendUserNotification(
        	    userId,
        	    "Your Password Has Been Changed",
        	    "Hi " + user.getFirstName() + ",<br><br>" +
        	    "Your password has been successfully updated. If this wasn't you, " +
        	    "please reset your password immediately.<br><br>" +
        	    "<a href='https://mockxpert.com/reset-password'>Reset Password</a><br><br>" +
        	    "Best Regards,<br>MockXpert Team"
        	);

        userRepository.saveAndFlush(user);
    }

    /**
     * Generates and stores a reset token for forgot password functionality.
     *
     * @param email User email.
     * @return The generated reset token.
     */
    @Transactional
    public String generateResetToken(String email) {
        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        final String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        userRepository.saveAndFlush(user);
        
        sendUserNotification(
        	    user.getUserId(),
        	    "Password Reset Request",
        	    "Hi " + user.getFirstName() + ",<br><br>" +
        	    "We received a request to reset your password. Click the link below to reset it:<br><br>" +
        	    "<a href='https://mockxpert.com/reset-password?token=" + resetToken + "'>Reset Your Password</a><br><br>" +
        	    "If you did not request this, please ignore this email.<br><br>" +
        	    "Best Regards,<br>MockXpert Team"
        	);

        return resetToken;
    }

    /**
     * Resets the user's password using a reset token.
     * Validates that the new password and confirm password match, then encrypts the new password.
     *
     * @param resetToken      The reset token.
     * @param newPassword     New password.
     * @param confirmPassword Confirmation of the new password.
     */
    @Transactional
    public void resetPassword(String resetToken, String newPassword, String confirmPassword) {
        final User user = userRepository.findByResetToken(resetToken)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired reset token."));
        if (newPassword == null || newPassword.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new ValidationException("Password and confirm password do not match.");
        }
        
        try {
            // Update password in Firebase
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(user.getFirebaseUid())
                    .setPassword(newPassword);
            
            firebaseAuth.updateUser(request);

            // Force logout by revoking all Firebase tokens
            firebaseAuth.revokeRefreshTokens(user.getFirebaseUid());

            // Clear reset token after successful update
            user.setResetToken(null);
            userRepository.save(user);

        } catch (FirebaseAuthException e) {
            throw new InternalServerErrorException("Failed to update password in Firebase: " + e.getMessage());
        }
    }

    /**
     * Deletes a user account.
     *
     * @param userId User ID.
     */
    @Transactional
    public void deleteUser(Long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        userRepository.delete(user);
    }

    /**
     * Deactivates a user account.
     *
     * @param userId User ID.
     * @return true if the user was successfully deactivated.
     */
    @Transactional
    public boolean deactivateUser(Long userId) {
    	System.out.println("Service API Request received to deactivate user: " + userId);
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
     * Reactivates a user account.
     *
     * @param userId User ID.
     * @return true if the user was successfully reactivated.
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
     * Helper method to create a user notification and trigger an email.
     *
     * @param userId  The user ID.
     * @param subject The email subject.
     * @param message The email content.
     */
    private void sendUserNotification(Long userId, String subject, String message) {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setUserId(userId);
        notificationDto.setSubject(subject);
        notificationDto.setMessage(message);
        notificationDto.setType("EMAIL");
        notificationDto.setStatus("SENT");

        notificationService.createNotification(notificationDto);
    }

}
