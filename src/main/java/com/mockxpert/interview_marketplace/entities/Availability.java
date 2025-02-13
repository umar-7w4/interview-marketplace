package com.mockxpert.interview_marketplace.entities;


import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.*;

/**
 *  
 * Entity class thats responsible for table creation for availabilities and its fields
 * 
 * @author Umar Mohammad
 * 
 */

@Entity
@Table(name = "availabilities")
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "availability_id", nullable = false)
    private Long availabilityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewer_id", nullable = false)
    private Interviewer interviewer;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AvailabilityStatus status; 

    @Column(nullable = false)
    private String timezone;

    @OneToOne(mappedBy = "availability", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Booking booking;
    
    @Version
    @Column(name = "version", nullable = false)
    private int version = 0; 

    
    public enum AvailabilityStatus {
        AVAILABLE,
        BOOKED,
        EXPIRED
    }

	public Long getAvailabilityId() {
		return availabilityId;
	}

	public void setAvailabilityId(Long availabilityId) {
		this.availabilityId = availabilityId;
	}

	public Interviewer getInterviewer() {
		return interviewer;
	}

	public void setInterviewer(Interviewer interviewer) {
		this.interviewer = interviewer;
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

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public AvailabilityStatus getStatus() {
		return status;
	}

	public void setStatus(AvailabilityStatus status) {
		this.status = status;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

	public int getVersion() {
		return version;
	}
    
    
}
