package com.mockxpert.interview_marketplace.dto;

import com.mockxpert.interview_marketplace.entities.User;
import com.mockxpert.interview_marketplace.entities.User.Role;

/**
 * DTO for user login response, including Firebase authentication tokens and role.
 * 
 * @author Umar Mohammad
 */

public class LoginResponse {

	private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String workEmail;
    private String phoneNumber;
    private String profilePictureUrl;
    private String preferredLanguage;
    private String timezone;
    private String createdAt;
    private String lastLogin;
    private User.Role role;
    private User.Status status;
    private String idToken; 
    private String refreshToken;
    private boolean isEmailVerified;
    private boolean isWorkEmailVerified;

	public LoginResponse(String firstName, String lastName, String fullName, String email, String workEmail,
			String phoneNumber, String profilePictureUrl, String preferredLanguage, String timezone, String createdAt,
			String lastLogin, User.Role role, User.Status status, String idToken, String refreshToken, boolean isEmailVerified, boolean isWorkEmailVerified) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.fullName = fullName;
		this.email = email;
		this.workEmail = workEmail;
		this.phoneNumber = phoneNumber;
		this.profilePictureUrl = profilePictureUrl;
		this.preferredLanguage = preferredLanguage;
		this.timezone = timezone;
		this.createdAt = createdAt;
		this.lastLogin = lastLogin;
		this.role = role;
		this.status = status;
		this.idToken = idToken;
		this.refreshToken = refreshToken;
		this.isEmailVerified = isEmailVerified;
		this.isWorkEmailVerified = isWorkEmailVerified;
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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getWorkEmail() {
		return workEmail;
	}

	public void setWorkEmail(String workEmail) {
		this.workEmail = workEmail;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getProfilePictureUrl() {
		return profilePictureUrl;
	}

	public void setProfilePictureUrl(String profilePictureUrl) {
		this.profilePictureUrl = profilePictureUrl;
	}

	public String getPreferredLanguage() {
		return preferredLanguage;
	}

	public void setPreferredLanguage(String preferredLanguage) {
		this.preferredLanguage = preferredLanguage;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}

	public User.Status getStatus() {
		return status;
	}

	public void setStatus(User.Status status) {
		this.status = status;
	}

	public boolean isEmailVerified() {
		return isEmailVerified;
	}

	public void setEmailVerified(boolean isEmailVerified) {
		this.isEmailVerified = isEmailVerified;
	}

	public boolean isWorkEmailVerified() {
		return isWorkEmailVerified;
	}

	public void setWorkEmailVerified(boolean isWorkEmailVerified) {
		this.isWorkEmailVerified = isWorkEmailVerified;
	}
	
	
    
    
}
