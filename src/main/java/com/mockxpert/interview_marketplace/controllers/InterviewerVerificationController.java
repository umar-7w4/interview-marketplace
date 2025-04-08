package com.mockxpert.interview_marketplace.controllers;

import com.mockxpert.interview_marketplace.dto.AdminOverrideDto;
import com.mockxpert.interview_marketplace.dto.VerificationResponseDto;
import com.mockxpert.interview_marketplace.dto.VerifyOtpRequest;
import com.mockxpert.interview_marketplace.entities.User;
import com.mockxpert.interview_marketplace.exceptions.InvalidOtpException;
import com.mockxpert.interview_marketplace.exceptions.ResourceNotFoundException;
import com.mockxpert.interview_marketplace.repositories.UserRepository;
import com.mockxpert.interview_marketplace.services.InterviewerVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling Interviewer Verification endpoints.
 * 
 * @author Umar Mohammad
 */
@RestController
@RequestMapping("/api/verification")
public class InterviewerVerificationController {

    @Autowired
    private InterviewerVerificationService verificationService;

    /**
     * Endpoint to send a verification OTP to the authenticated user.
     * This method returns HTTP 200 OK with a success message if the OTP is sent.
     *
     * @return ResponseEntity with status and message.
     */
    @PostMapping("/sendOtp/{userId}")
    public ResponseEntity<String> sendVerificationOtp(@PathVariable Long userId) {
        
        verificationService.sendVerificationOtp(userId);
        
        return ResponseEntity.ok("OTP sent successfully");
    }

    /**
     * 
     * Endpoint to verify the OTP provided by the client.
     * Expects a JSON payload containing the OTP.
     *
     * On success, returns HTTP 200 OK; on failure (e.g. invalid/expired OTP),
     * returns HTTP 400 Bad Request with the error message.
     *
     * @param request the request payload containing the OTP.
     * @return ResponseEntity with status and message.
     */
    @PostMapping("/verifyOtp/{userId}")
    public ResponseEntity<String> verifyOtp(@PathVariable Long userId, @RequestBody VerifyOtpRequest request) {
        
        try {
            verificationService.verifyOtp(userId, request.getOtp());
            return ResponseEntity.ok("OTP verified successfully");
        } catch (InvalidOtpException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    /**
     * Endpoint to resend the OTP to the authenticated user.
     * Returns HTTP 200 OK with a success message.
     *
     * @return ResponseEntity with status and message.
     */
    @PostMapping("/resendOtp/{userId}")
    public ResponseEntity<String> resendOtp(@PathVariable Long userId) {
        
        verificationService.resendOtp(userId);
        return ResponseEntity.ok("OTP resent successfully");
    }

    
}
