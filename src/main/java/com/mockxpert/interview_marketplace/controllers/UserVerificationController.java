package com.mockxpert.interview_marketplace.controllers;

import com.mockxpert.interview_marketplace.dto.VerifyOtpRequest;
import com.mockxpert.interview_marketplace.exceptions.InvalidOtpException;
import com.mockxpert.interview_marketplace.services.UserVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling User Email Verification endpoints.
 * 
 * <p>
 * This controller provides endpoints for sending, verifying, and resending OTPs
 * to the user's primary email. When a user triggers the verification process,
 * an OTP is generated and sent via email. The user can then verify their email
 * by providing the OTP.
 * </p>
 * 
 * <pre>
 * Endpoints:
 *  POST /api/verification/user/sendOtp/{userId}   - Sends an OTP to the user's email.
 *  POST /api/verification/user/verifyOtp/{userId} - Verifies the provided OTP.
 *  POST /api/verification/user/resendOtp/{userId} - Resends the OTP.
 * </pre>
 */

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/verification/user")
public class UserVerificationController {

    @Autowired
    private UserVerificationService userVerificationService;

    /**
     * Sends a verification OTP to the primary email of the user identified by the given ID.
     *
     * @param userId the ID of the user whose email should be verified.
     * @return a ResponseEntity containing a success message.
     */
    @PostMapping("/sendOtp/{userId}")
    public ResponseEntity<String> sendVerificationOtp(@PathVariable Long userId) {
        userVerificationService.sendVerificationOtp(userId);
        return ResponseEntity.ok("OTP sent successfully to your email.");
    }

    /**
     * Verifies the OTP provided by the user for email verification.
     *
     * <p>
     * Expects a JSON payload with the OTP.
     * </p>
     *
     * @param userId  the ID of the user.
     * @param request the request payload containing the OTP.
     * @return a ResponseEntity with a success message if the OTP is valid,
     *         or an error message if the OTP is invalid or expired.
     */
    @PostMapping("/verifyOtp/{userId}")
    public ResponseEntity<String> verifyOtp(@PathVariable Long userId, @RequestBody VerifyOtpRequest request) {
        try {
            userVerificationService.verifyOtp(userId, request.getOtp());
            return ResponseEntity.ok("OTP verified successfully.");
        } catch (InvalidOtpException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Resends a verification OTP to the user's primary email.
     *
     * @param userId the ID of the user.
     * @return a ResponseEntity containing a success message.
     */
    @PostMapping("/resendOtp/{userId}")
    public ResponseEntity<String> resendOtp(@PathVariable Long userId) {
        userVerificationService.resendOtp(userId);
        return ResponseEntity.ok("OTP resent successfully to your email.");
    }
}
