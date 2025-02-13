package com.mockxpert.interview_marketplace.dto;


import com.mockxpert.interview_marketplace.entities.User;
import java.time.LocalDateTime;

import jakarta.validation.constraints.*;

/**
 * Data Transfer Object for the feedback JSON object.
 * 
 * @author Umar Mohammad
 */

public class FeedbackDto {

    private Long feedbackId;

    @NotNull(message = "Interview ID is required")
    private Long interviewId;

    @NotNull(message = "Feedback giver is required")
    private User giver; // Changed from giverId

    @NotNull(message = "Feedback receiver is required")
    private User receiver; // Changed from receiverId

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 10, message = "Rating must not exceed 10")
    private int rating;

    private String comments;
    private String positives;
    private String negatives;
    private String improvements;
    private LocalDateTime createdAt;


	public Long getFeedbackId() {
		return feedbackId;
	}

	public void setFeedbackId(Long feedbackId) {
		this.feedbackId = feedbackId;
	}

	public Long getInterviewId() {
		return interviewId;
	}

	public void setInterviewId(Long interviewId) {
		this.interviewId = interviewId;
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
