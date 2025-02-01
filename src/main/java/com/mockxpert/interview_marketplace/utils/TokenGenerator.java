package com.mockxpert.interview_marketplace.utils;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for generating secure random tokens.
 */
public class TokenGenerator {

    private static final SecureRandom secureRandom = new SecureRandom(); // Thread-safe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding(); // Thread-safe

    /**
     * Generates a secure, random token encoded in Base64.
     *
     * @return a URL-safe Base64 encoded token string.
     */
    public static String generateToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
