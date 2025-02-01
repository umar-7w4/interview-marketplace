package com.mockxpert.interview_marketplace.controllers;

import com.mockxpert.interview_marketplace.dto.AdminOverrideDto;
import com.mockxpert.interview_marketplace.dto.VerificationResponseDto;
import com.mockxpert.interview_marketplace.services.InterviewerVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling Interviewer Verification endpoints.
 */
@RestController
@RequestMapping("/api/verification")
public class InterviewerVerificationController {

    @Autowired
    private InterviewerVerificationService verificationService;
    
    @PostMapping("/send-verification-email")
    public ResponseEntity<String> sendVerificationEmail(@RequestParam Long interviewerId) {
        verificationService.initiateVerification(interviewerId);
        return ResponseEntity.ok("Verification email sent successfully.");
    }


    /**
     * Endpoint to handle email verification.
     * Example: GET /api/verification/verify-email?token=unique-token
     *
     * @param token the verification token.
     * @return a response indicating the verification result.
     */
    @GetMapping("/verify-email")
    public ResponseEntity<VerificationResponseDto> verifyEmail(@RequestParam("token") String token) {
        VerificationResponseDto response = verificationService.verifyEmail(token);
        return ResponseEntity.ok(response);
    }

    /**
     * Admin endpoint to override verification status.
     * Example: POST /api/verification/admin/override
     *
     * @param adminOverrideDto the DTO containing override information.
     * @return a response indicating the override result.
     */
    @PostMapping("/admin/override")
    public ResponseEntity<VerificationResponseDto> adminOverride(@RequestBody @Validated AdminOverrideDto adminOverrideDto) {
        VerificationResponseDto response = verificationService.adminOverride(adminOverrideDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to resend verification email.
     * Example: POST /api/verification/resend?interviewerId=1
     *
     * @param interviewerId the ID of the interviewer.
     * @return a response indicating that the email has been resent.
     */
    @PostMapping("/resend")
    public ResponseEntity<String> resendVerificationEmail(@RequestParam("interviewerId") Long interviewerId) {
        verificationService.resendVerificationEmail(interviewerId);
        return ResponseEntity.ok("Verification email resent successfully.");
    }
}
