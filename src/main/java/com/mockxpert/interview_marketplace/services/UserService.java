package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.UserDto;
import com.mockxpert.interview_marketplace.dto.LoginRequest;
import com.mockxpert.interview_marketplace.dto.LoginResponse;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class for managing user authentication and registration using Firebase Authentication.
 * This ensures full backend automation, eliminating manual user creation in Firebase.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FirebaseAuth firebaseAuth;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Inject the new FirebaseTokenService for refreshing tokens
    @Autowired
    private FirebaseTokenService firebaseTokenService;

    @Value("${firebase.api.key}")
    private String firebaseApiKey; // Firebase API Key from application.properties

    private static final String FIREBASE_LOGIN_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=";

    /**
     * Registers a new user, creates an account in Firebase, and stores user details in the database.
     *
     * @param userDto User registration details.
     * @return The registered user details.
     */
    @Transactional
    public UserDto registerUser(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists: " + userDto.getEmail());
        }

        // Create Firebase user
        String firebaseUid = createFirebaseUser(userDto.getEmail(), userDto.getPasswordHash());

        // Authenticate with Firebase to retrieve authentication tokens
        FirebaseLoginResponse firebaseResponse = authenticateWithFirebase(userDto.getEmail(), userDto.getPasswordHash());

        // Convert DTO to entity
        User user = UserMapper.toEntity(userDto);
        user.setFirebaseUid(firebaseUid);
        user.setRefreshToken(firebaseResponse.getRefreshToken());
        user.setCreatedAt(LocalDateTime.now());
        user.setStatus(user.getRole() == User.Role.INTERVIEWER ? User.Status.PENDING : User.Status.ACTIVE);

        // Save user in database
        User savedUser = userRepository.saveAndFlush(user);
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
            throw new InternalServerErrorException("Failed to create user in Firebase: " + e.getMessage());
        }
    }

    /**
     * Calls Firebase Authentication API to authenticate user credentials and retrieve authentication tokens.
     * This method ensures that Firebase handles authentication without storing passwords in the backend.
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

        // Call Firebase Authentication API
        RestTemplate restTemplate = new RestTemplate();
        FirebaseLoginResponse firebaseResponse;
        try {
            firebaseResponse = restTemplate.postForObject(
                    FIREBASE_LOGIN_URL + firebaseApiKey,
                    requestBody,
                    FirebaseLoginResponse.class
            );
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid email or password.");
        }

        return firebaseResponse;
    }

    /**
     * Logs in a user using Firebase authentication.
     * Retrieves Firebase UID, ID Token, and refresh token.
     *
     * @param loginRequest Login request containing email and password.
     * @return LoginResponse containing authentication tokens and user details.
     */
    @Transactional
    public LoginResponse loginUser(LoginRequest loginRequest) {
        FirebaseLoginResponse firebaseResponse = authenticateWithFirebase(loginRequest.getEmail(), loginRequest.getPassword());

        String firebaseUid = firebaseResponse.getLocalId();
        String idToken = firebaseResponse.getIdToken();
        String refreshToken = firebaseResponse.getRefreshToken();

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + loginRequest.getEmail()));

        user.setFirebaseUid(firebaseUid);
        user.setRefreshToken(refreshToken);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return new LoginResponse(idToken, refreshToken, user.getRole(), user.getEmail());
    }

    /**
     * Helper method to refresh a Firebase ID token using the stored Firebase refresh token.
     * This method uses the FirebaseTokenService which calls the correct secure token endpoint.
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
     * @return User details.
     */
    public UserDto findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Updates user profile.
     *
     * @param userId  User ID.
     * @param userDto Updated user details.
     * @return Updated user information.
     */
    @Transactional
    public UserDto updateUserProfile(Long userId, UserDto userDto) {
        User existingUser = userRepository.findById(userId)
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

        return UserMapper.toDto(userRepository.saveAndFlush(existingUser));
    }

    /**
     * Deletes a user by ID.
     *
     * @param userId User ID.
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        userRepository.delete(user);
    }

    /**
     * Changes user password.
     *
     * @param userId      User ID.
     * @param newPassword New password.
     */
    @Transactional
    public void changeUserPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (newPassword == null || newPassword.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long.");
        }

        user.setPasswordHash(newPassword);
        userRepository.saveAndFlush(user);
    }

    /**
     * Deactivates a user account.
     *
     * @param userId The user ID.
     * @return true if successfully deactivated.
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
     * Reactivates a user account.
     *
     * @param userId The user ID.
     * @return true if successfully reactivated.
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
}
