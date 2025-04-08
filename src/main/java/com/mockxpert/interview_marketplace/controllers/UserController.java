package com.mockxpert.interview_marketplace.controllers;

import com.google.firebase.auth.FirebaseAuthException;
import com.mockxpert.interview_marketplace.dto.UserDto;
import com.mockxpert.interview_marketplace.dto.LoginRequest;
import com.mockxpert.interview_marketplace.dto.LoginResponse;
import com.mockxpert.interview_marketplace.exceptions.ConflictException;
import com.mockxpert.interview_marketplace.exceptions.ResourceNotFoundException;
import com.mockxpert.interview_marketplace.exceptions.UnauthorizedException;
import com.mockxpert.interview_marketplace.exceptions.ValidationException;
import com.mockxpert.interview_marketplace.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.mockxpert.interview_marketplace.entities.User;


/**
 * Rest controller responsible for handling all the HTTP API requests related to user operations.
 * 
 * @author Umar Mohammad
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    /**
     * Registers a new user.
     * Validates that the password and confirm password match, encrypts the password,
     * creates a Firebase user, and stores user details in the database.
     *
     * @param userDto User registration details.
     * @return Registered user details.
     * @throws FirebaseAuthException If Firebase authentication fails.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserDto userDto) throws FirebaseAuthException {
        try {
            UserDto savedUser = userService.registerUser(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Logs in a user using email and password.
     * Retrieves authentication tokens and user details.
     *
     * @param loginRequest Contains user login credentials.
     * @return LoginResponse with tokens and user information.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = userService.loginUser(loginRequest);
            return ResponseEntity.ok(loginResponse);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * Retrieves user details by email.
     *
     * @param email User email.
     * @return User details.
     */
    @GetMapping("/findByEmail")
    public ResponseEntity<?> findUserByEmail(@RequestParam String email) {
        try {
            UserDto user = userService.findUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Updates a user's profile.
     *
     * @param userId  User ID.
     * @param userDto Updated user details.
     * @return Updated user information.
     */
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUserProfile(@PathVariable Long userId, @RequestBody @Valid UserDto userDto) {
        try {
            UserDto updatedUser = userService.updateUserProfile(userId, userDto);
            return ResponseEntity.ok(updatedUser);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /**
     * Generates a reset token for forgot password functionality.
     *
     * @param email User email.
     * @return The generated reset token.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            String resetToken = userService.generateResetToken(email);
            return ResponseEntity.ok("Reset token generated: " + resetToken);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Resets the user's password using a reset token.
     * Validates that the new password and confirm password match, then updates the password.
     *
     * @param token           Reset token.
     * @param newPassword     New password.
     * @param confirmPassword Confirmation of the new password.
     * @return Password reset status message.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token,
                                           @RequestParam String newPassword,
                                           @RequestParam String confirmPassword) {
        try {
            userService.resetPassword(token, newPassword, confirmPassword);
            return ResponseEntity.ok("Password has been reset successfully.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Changes the user's password.
     * Validates that the new password and confirm password match, then updates the password.
     *
     * @param userId          User ID.
     * @param newPassword     New password.
     * @param confirmPassword Confirmation of the new password.
     * @return Password change status message.
     */
    @PutMapping("/{userId}/changePassword")
    public ResponseEntity<String> changeUserPassword(@PathVariable Long userId,
                                                     @RequestParam String newPassword,
                                                     @RequestParam String confirmPassword) {
        try {
            userService.changeUserPassword(userId, newPassword, confirmPassword);
            return ResponseEntity.ok("Password changed successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Deactivates a user account.
     *
     * @param userId User ID.
     * @return User deactivation status message.
     */
    @PutMapping("/{userId}/deactivate")
    public ResponseEntity<String> deactivateUser(@PathVariable Long userId) {
    	System.out.println("Contoller API Request received to deactivate user: " + userId);
    	try {
            userService.deactivateUser(userId);
            return ResponseEntity.ok("User deactivated successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Reactivates a user account.
     *
     * @param userId User ID.
     * @return User reactivation status message.
     */
    @PutMapping("/{userId}/reactivate")
    public ResponseEntity<String> reactivateUser(@PathVariable Long userId) {
        try {
            userService.reactivateUser(userId);
            return ResponseEntity.ok("User reactivated successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Deletes a user account.
     *
     * @param userId User ID.
     * @return User deletion status message.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("User deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    /**
     * Deletes a user account.
     *
     * @param userId User ID.
     * @return User deletion status message.
     */
    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser() {
        try {
        	UserDto user = userService.getCurrentUser();
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    /**
     * Gets user record by its id.
     * 
     * @param userId
     * @return
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
      UserDto user = userService.getUserById(userId);
      return ResponseEntity.ok(user);
    }
}
