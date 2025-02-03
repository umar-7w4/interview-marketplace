package com.mockxpert.interview_marketplace.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.mockxpert.interview_marketplace.dto.FirebaseTokenResponse;

@Service
public class FirebaseTokenService {

    // Your Firebase API key (make sure it's set correctly in your application.properties)
    @Value("${firebase.api.key}")
    private String firebaseApiKey;

    // Firebase secure token endpoint URL
    private static final String FIREBASE_TOKEN_URL = "https://securetoken.googleapis.com/v1/token?key=";

    /**
     * Exchanges a Firebase refresh token for a new ID token.
     *
     * @param refreshToken The refresh token from Firebase.
     * @return A FirebaseTokenResponse containing the new tokens.
     */
    public FirebaseTokenResponse refreshIdToken(String refreshToken) {
        RestTemplate restTemplate = new RestTemplate();

        // Build the request body for Firebase refresh
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "refresh_token");
        requestBody.add("refresh_token", refreshToken);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Create the HTTP entity
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Call the Firebase secure token endpoint
        ResponseEntity<FirebaseTokenResponse> responseEntity = restTemplate.exchange(
                FIREBASE_TOKEN_URL + firebaseApiKey,
                HttpMethod.POST,
                requestEntity,
                FirebaseTokenResponse.class
        );

        if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
            return responseEntity.getBody();
        } else {
            throw new RuntimeException("Failed to refresh Firebase token: " + responseEntity.getBody());
        }
    }
}
