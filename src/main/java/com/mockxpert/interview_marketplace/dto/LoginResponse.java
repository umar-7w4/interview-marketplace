package com.mockxpert.interview_marketplace.dto;

import com.mockxpert.interview_marketplace.entities.User;
import com.mockxpert.interview_marketplace.entities.User.Role;

/**
 * DTO for user login response, including Firebase authentication tokens and role.
 */

public class LoginResponse {

	private String idToken;
    private String refreshToken;
    private User.Role role;  // Updated to use the Role enum
    private String email;
    
    public LoginResponse(String idToken, String refreshToken, Role role, String email) {
		this.idToken = idToken;
		this.refreshToken = refreshToken;
		this.role = role;
		this.email = email;
	}

	public String getIdToken() {
		return idToken;
	}

	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public User.Role getRole() {
		return role;
	}

	public void setRole(User.Role role) {
		this.role = role;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
    
    
}
