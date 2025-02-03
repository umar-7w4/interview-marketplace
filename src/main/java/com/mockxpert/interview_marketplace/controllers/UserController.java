package com.mockxpert.interview_marketplace.controllers;

import com.google.firebase.auth.FirebaseAuthException;
import com.mockxpert.interview_marketplace.dto.UserDto;
import com.mockxpert.interview_marketplace.dto.LoginRequest;
import com.mockxpert.interview_marketplace.dto.LoginResponse;
import com.mockxpert.interview_marketplace.exceptions.*;
import com.mockxpert.interview_marketplace.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;

/**
 * REST Controller for user authentication and management.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    public UserController() {
        System.out.println("UserController Initialized");
    }

    /**
     * Registers a new user.
     * Backend automatically handles Firebase token validation and refresh token extraction.
     * 
     * @param userDto User registration details.
     * @return The registered user details.
     * @throws FirebaseAuthException If Firebase authentication fails.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserDto userDto) throws FirebaseAuthException {
        try {
            System.out.println("Received User Data for Registration: " + userDto);
            
            UserDto savedUser = userService.registerUser(userDto);
            
            System.out.println("User Registered Successfully: " + savedUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * Logs in a user using email and password.
     * Backend retrieves Firebase authentication tokens and refresh token.
     * 
     * @param loginRequest Contains email and password.
     * @return LoginResponse with authentication tokens and user details.
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
     * Fetch user details by email.
     * 
     * @param email User's email.
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
     * @param userId User ID.
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
     * Deactivates a user.
     * 
     * @param userId User ID.
     * @return Deactivation status.
     */
    @PutMapping("/{userId}/deactivate")
    public ResponseEntity<String> deactivateUser(@PathVariable Long userId) {
        try {
            userService.deactivateUser(userId);
            return ResponseEntity.ok("User deactivated successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Reactivates a user.
     * 
     * @param userId User ID.
     * @return Reactivation status.
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
     * Changes the user's password.
     * 
     * @param userId User ID.
     * @param newPassword New password.
     * @return Password change status.
     */
    @PutMapping("/{userId}/changePassword")
    public ResponseEntity<String> changeUserPassword(@PathVariable Long userId, @RequestParam String newPassword) {
        try {
            userService.changeUserPassword(userId, newPassword);
            return ResponseEntity.ok("Password changed successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Deletes a user account.
     * 
     * @param userId User ID.
     * @return Deletion status.
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
}
