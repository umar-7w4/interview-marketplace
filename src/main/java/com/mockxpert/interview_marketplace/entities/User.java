package com.mockxpert.interview_marketplace.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "preferred_language")
    private String preferredLanguage;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "firebase_uid", unique = true, nullable = false)
    private String firebaseUid;

    public String getFirebaseUid() {
		return firebaseUid;
	}

	public void setFirebaseUid(String firebaseUid) {
		this.firebaseUid = firebaseUid;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Transient
    private String fullName;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Interviewer interviewer;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Interviewee interviewee;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications;

    @OneToMany(mappedBy = "giver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Feedback> feedbackGiven;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Feedback> feedbackReceived;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Enums
    public enum Role {
        ADMIN,
        INTERVIEWER,
        INTERVIEWEE
    }

    public enum Status {
        ACTIVE,
        INACTIVE,
        PENDING
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Interviewer getInterviewer() {
        return interviewer;
    }

    public void setInterviewer(Interviewer interviewer) {
        this.interviewer = interviewer;
    }

    public Interviewee getInterviewee() {
        return interviewee;
    }

    public void setInterviewee(Interviewee interviewee) {
        this.interviewee = interviewee;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<Feedback> getFeedbackGiven() {
        return feedbackGiven;
    }

    public void setFeedbackGiven(List<Feedback> feedbackGiven) {
        this.feedbackGiven = feedbackGiven;
    }

    public List<Feedback> getFeedbackReceived() {
        return feedbackReceived;
    }

    public void setFeedbackReceived(List<Feedback> feedbackReceived) {
        this.feedbackReceived = feedbackReceived;
    }
}