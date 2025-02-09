package com.mockxpert.interview_marketplace.entities;

import java.time.*;

import jakarta.persistence.*;

@Entity
@Table(name = "interviewer_verifications")
public class InterviewerVerification {

    public enum VerificationStatus {
        PENDING,
        EMAIL_SENT,
        VERIFIED,
        REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interviewer_verification_id", nullable = false)
    private Long interviewerVerificationId;

    @Column(name="verification_token", unique = true, nullable = false)
    private String verificationToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus status;

    @Column(name = "verification_notes")
    private String verificationNotes;
    
	@Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewer_id", nullable = false, unique = true)
    private Interviewer interviewer;

    @Column(name = "token_expiry")
    private LocalDateTime tokenExpiry;

    public Long getInterviewerVerificationId() {
		return interviewerVerificationId;
	}

	public void setInterviewerVerificationId(Long id) {
		this.interviewerVerificationId = id;
	}

	public String getVerificationToken() {
		return verificationToken;
	}

	public void setVerificationToken(String verificationToken) {
		this.verificationToken = verificationToken;
	}

	public VerificationStatus getStatus() {
		return status;
	}

	public void setStatus(VerificationStatus status) {
		this.status = status;
	}

	public String getVerificationNotes() {
		return verificationNotes;
	}

	public void setVerificationNotes(String verificationNotes) {
		this.verificationNotes = verificationNotes;
	}

	public LocalDateTime getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(LocalDateTime lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Interviewer getInterviewer() {
		return interviewer;
	}

	public void setInterviewer(Interviewer interviewer) {
		this.interviewer = interviewer;
	}

	public LocalDateTime getTokenExpiry() {
		return tokenExpiry;
	}

	public void setTokenExpiry(LocalDateTime tokenExpiry) {
		this.tokenExpiry = tokenExpiry;
	}


    @PrePersist
    protected void onCreate() {
        lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }

}
