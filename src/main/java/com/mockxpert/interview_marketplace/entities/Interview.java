 package com.mockxpert.interview_marketplace.entities;


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

/**
 *  
 * Entity class thats responsible for table creation for interviews and its fields
 * 
 * @author Umar Mohammad
 * 
 */

@Entity
@Table(name = "interviews")
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interview_id", nullable = false)
    private Long interviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewee_id", nullable = false)
    private Interviewee interviewee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewer_id", nullable = false)
    private Interviewer interviewer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "duration", nullable = false)
    private Duration duration;

    @Column(name = "interview_link", nullable = false)
    private String interviewLink;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewStatus status;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(nullable = false)
    private String timezone;
    
    @Column(nullable = true)
    private String title;

    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Feedback> feedbacks;

    @OneToOne(mappedBy = "interview", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications;
    
    @PrePersist
    @PreUpdate
    public void calculateEndTime() {
        if (this.startTime != null && this.duration != null) {
            this.endTime = this.startTime.plus(this.duration);
        }
    }
    
    public enum InterviewStatus {
        BOOKED,
        COMPLETED,
        CANCELLED
    }

	public Long getInterviewId() {
		return interviewId;
	}

	public void setInterviewId(Long interviewId) {
		this.interviewId = interviewId;
	}

	public Interviewee getInterviewee() {
		return interviewee;
	}

	public void setInterviewee(Interviewee interviewee) {
		this.interviewee = interviewee;
	}

	public Interviewer getInterviewer() {
		return interviewer;
	}

	public void setInterviewer(Interviewer interviewer) {
		this.interviewer = interviewer;
	}

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public String getInterviewLink() {
		return interviewLink;
	}

	public void setInterviewLink(String interviewLink) {
		this.interviewLink = interviewLink;
	}

	public InterviewStatus getStatus() {
		return status;
	}

	public void setStatus(InterviewStatus status) {
		this.status = status;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public LocalDateTime getActualStartTime() {
		return actualStartTime;
	}

	public void setActualStartTime(LocalDateTime actualStartTime) {
		this.actualStartTime = actualStartTime;
	}

	public LocalDateTime getActualEndTime() {
		return actualEndTime;
	}

	public void setActualEndTime(LocalDateTime actualEndTime) {
		this.actualEndTime = actualEndTime;
	}

	public List<Feedback> getFeedbacks() {
		return feedbacks;
	}

	public void setFeedbacks(List<Feedback> feedbacks) {
		this.feedbacks = feedbacks;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
    
    

}

