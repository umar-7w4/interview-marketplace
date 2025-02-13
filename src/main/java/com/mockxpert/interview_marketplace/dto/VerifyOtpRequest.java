package com.mockxpert.interview_marketplace.dto;


/**
 * Data Transfer Object for the OTP verification JSON object.
 * 
 * @author Umar Mohammad
 */

public class VerifyOtpRequest {
    private String otp;

    public String getOtp() {
        return otp;
    }
    
    public void setOtp(String otp) {
        this.otp = otp;
    }
}