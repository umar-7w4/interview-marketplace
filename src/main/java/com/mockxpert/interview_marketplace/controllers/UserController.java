package com.mockxpert.interview_marketplace.controllers;

import com.mockxpert.interview_marketplace.dto.UserDto;
import com.mockxpert.interview_marketplace.exceptions.*;
import com.mockxpert.interview_marketplace.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Register a new user using Firebase authentication.
     * @param firebaseToken the Firebase authentication token.
     * @param userDto the user data transfer object containing registration information.
     * @return the created UserDto.
     */
    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestHeader("Authorization") String firebaseToken, @RequestBody @Valid UserDto userDto) {
        try {
            UserDto savedUser = userService.registerUserUsingFirebaseToken(firebaseToken, userDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    /**
     * Login user using Firebase token.
     * @param firebaseToken the Firebase authentication token.
     * @return the UserDto of the logged-in user.
     */
    @PostMapping("/login")
    public ResponseEntity<UserDto> loginUser(@RequestHeader("Authorization") String firebaseToken) {
        try {
            UserDto loggedInUser = userService.loginUserUsingFirebaseToken(firebaseToken);
            return ResponseEntity.ok(loggedInUser);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    /**
     * Update user profile.
     * @param userId the ID of the user to update.
     * @param userDto the user data transfer object containing updated information.
     * @return the updated UserDto.
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUserProfile(@PathVariable Long userId, @RequestBody @Valid UserDto userDto) {
        try {
            UserDto updatedUser = userService.updateUserProfile(userId, userDto);
            return ResponseEntity.ok(updatedUser);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    /**
     * Deactivate user.
     * @param userId the ID of the user to deactivate.
     * @return a response indicating whether the user was successfully deactivated.
     */
    @PutMapping("/{userId}/deactivate")
    public ResponseEntity<String> deactivateUser(@PathVariable Long userId) {
        try {
            boolean success = userService.deactivateUser(userId);
            return ResponseEntity.ok("User deactivated successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Reactivate user.
     * @param userId the ID of the user to reactivate.
     * @return a response indicating whether the user was successfully reactivated.
     */
    @PutMapping("/{userId}/reactivate")
    public ResponseEntity<String> reactivateUser(@PathVariable Long userId) {
        try {
            boolean success = userService.reactivateUser(userId);
            return ResponseEntity.ok("User reactivated successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Find user by email.
     * @param email the email of the user to find.
     * @return the UserDto if found.
     */
    @GetMapping("/findByEmail")
    public ResponseEntity<UserDto> findUserByEmail(@RequestParam String email) {
        try {
            UserDto user = userService.findUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Change user password.
     * @param userId the ID of the user whose password is to be changed.
     * @param newPassword the new password.
     * @return a response indicating success.
     */
    @PutMapping("/{userId}/changePassword")
    public ResponseEntity<String> changeUserPassword(@PathVariable Long userId, @RequestParam String newPassword) {
        try {
            userService.changeUserPassword(userId, newPassword);
            return ResponseEntity.ok("Password changed successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Delete a user by ID.
     * @param userId the ID of the user to delete.
     * @return a response indicating success.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("User deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
