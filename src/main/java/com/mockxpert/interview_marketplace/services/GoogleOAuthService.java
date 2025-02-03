package com.mockxpert.interview_marketplace.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Map;

@Service
public class GoogleOAuthService {

    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    public String getAccessTokenFromRefreshToken(String refreshToken) {
        RestTemplate restTemplate = new RestTemplate();
        
        // Request body
        Map<String, String> requestBody = Map.of(
            "client_id", clientId,
            "client_secret", clientSecret,
            "refresh_token", refreshToken,
            "grant_type", "refresh_token"
        );

        // Request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        // Make API call
        ResponseEntity<Map> response = restTemplate.postForEntity(GOOGLE_TOKEN_URL, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody().get("access_token").toString();
        } else {
            throw new RuntimeException("Failed to retrieve access token from refresh token.");
        }
    }
}
