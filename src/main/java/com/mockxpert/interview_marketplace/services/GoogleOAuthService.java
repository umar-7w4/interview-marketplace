package com.mockxpert.interview_marketplace.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


/**
 * Service class for generating google OAuth service.
 * 
 * @author Umar Mohammad
 */
@Service
public class GoogleOAuthService {

    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";

    // The dedicated meeting account's OAuth credentials from application.properties.
    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    /**
     * Exchanges a Google OAuth refresh token for a new access token.
     *
     * @param refreshToken The refresh token retrieved during initial authentication.
     * @return The new access token.
     */
    public String getAccessTokenFromRefreshToken(String refreshToken) {
        RestTemplate restTemplate = new RestTemplate();

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("Refresh token is missing or empty.");
        }

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        requestBody.add("refresh_token", refreshToken);
        requestBody.add("grant_type", "refresh_token");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(GOOGLE_TOKEN_URL, HttpMethod.POST, requestEntity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().get("access_token").toString();
            } else {
                throw new RuntimeException("Failed to retrieve access token: " + response.getBody());
            }
        } catch (HttpClientErrorException ex) {
            String errorResponse = ex.getResponseBodyAsString();
            System.err.println("Error response from Google: " + errorResponse);
            throw new RuntimeException("OAuth Token request failed: " + errorResponse, ex);
        } catch (Exception e) {
            throw new RuntimeException("OAuth Token request failed: " + e.getMessage(), e);
        }
    }
}
