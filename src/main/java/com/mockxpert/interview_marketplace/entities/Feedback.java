package com.mockxpert.interview_marketplace.entities;


import java.time.LocalDateTime;

import jakarta.persistence.*;


@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giver_id", nullable = false)
    private User giver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(nullable = false)
    private int rating; 
    
    @Column(length = 1000)
    private String comments;

    @Column(length = 1000)
    private String positives;

    @Column(length = 1000)
    private String negatives;

    @Column(length = 1000)
    private String improvements;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

	public Long getFeedbackId() {
		return feedbackId;
	}

	public void setFeedbackId(Long feedbackId) {
		this.feedbackId = feedbackId;
	}

	public Interview getInterview() {
		return interview;
	}

	public void setInterview(Interview interview) {
		this.interview = interview;
	}

	public User getGiver() {
		return giver;
	}

	public void setGiver(User giver) {
		this.giver = giver;
	}

	public User getReceiver() {
		return receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getPositives() {
		return positives;
	}

	public void setPositives(String positives) {
		this.positives = positives;
	}

	public String getNegatives() {
		return negatives;
	}

	public void setNegatives(String negatives) {
		this.negatives = negatives;
	}

	public String getImprovements() {
		return improvements;
	}

	public void setImprovements(String improvements) {
		this.improvements = improvements;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
}

