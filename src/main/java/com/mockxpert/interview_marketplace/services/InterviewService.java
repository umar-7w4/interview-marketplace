package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.InterviewDto;
import com.mockxpert.interview_marketplace.entities.*;
import com.mockxpert.interview_marketplace.exceptions.ResourceNotFoundException;
import com.mockxpert.interview_marketplace.exceptions.InternalServerErrorException;
import com.mockxpert.interview_marketplace.mappers.InterviewMapper;
import com.mockxpert.interview_marketplace.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Service class for managing all the interview related services.
 * 
 * @author Umar Mohammad
 */
@Service
public class InterviewService {

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private IntervieweeRepository intervieweeRepository;

    @Autowired
    private InterviewerRepository interviewerRepository;

    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private UserService userService;

    /**
     * Register a new interview.
     * 
     * @param interviewDto the interview data transfer object containing registration information.
     * @return the saveAndFlushd InterviewDto.
     */
    @Transactional
    public InterviewDto registerInterview(InterviewDto interviewDto) {
        Interviewee interviewee = intervieweeRepository.findById(interviewDto.getIntervieweeId())
                .orElseThrow(() -> new ResourceNotFoundException("Interviewee not found with ID: " + interviewDto.getIntervieweeId()));

        Interviewer interviewer = interviewerRepository.findById(interviewDto.getInterviewerId())
                .orElseThrow(() -> new ResourceNotFoundException("Interviewer not found with ID: " + interviewDto.getInterviewerId()));

        Booking booking = bookingRepository.findById(interviewDto.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + interviewDto.getBookingId()));

        Interview interview = InterviewMapper.toEntity(interviewDto, interviewee, interviewer, booking);
        interview.setTitle("Mock Interview between"+interviewee.getUser().getFirstName()+" and "+interviewer.getUser().getFirstName());
        try {
            Interview saveAndFlushdInterview = interviewRepository.saveAndFlush(interview);
            return InterviewMapper.toDto(saveAndFlushdInterview);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to saveAndFlush Interview due to server error.");
        }
    }

    /**
     * Update interview information.
     * 
     * @param interviewId the ID of the interview to update.
     * @param interviewDto the interview data transfer object containing updated information.
     * @return the updated InterviewDto.
     */
    @Transactional
    public InterviewDto updateInterview(Long interviewId, InterviewDto interviewDto) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with ID: " + interviewId));

        if (interviewDto.getDate() != null) {
            interview.setDate(interviewDto.getDate());
        }
        if (interviewDto.getStartTime() != null) {
            interview.setStartTime(interviewDto.getStartTime());
        }
        if (interviewDto.getDuration() != null) {
            interview.setDuration(interviewDto.getDuration());
        }
        if (interviewDto.getInterviewLink() != null) {
            interview.setInterviewLink(interviewDto.getInterviewLink());
        }
        if (interviewDto.getInterviewStatus() != null) {
            interview.setStatus(Interview.InterviewStatus.valueOf(interviewDto.getInterviewStatus()));
        }
        if (interviewDto.getTimezone() != null) {
            interview.setTimezone(interviewDto.getTimezone());
        }
        if (interviewDto.getActualStartTime() != null) {
            interview.setActualStartTime(interviewDto.getActualStartTime());
        }
        if (interviewDto.getActualEndTime() != null) {
            interview.setActualEndTime(interviewDto.getActualEndTime());
        }

        try {
            Interview updatedInterview = interviewRepository.saveAndFlush(interview);
            return InterviewMapper.toDto(updatedInterview);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to update Interview due to server error.");
        }
    }

    /**
     * Find an interview by ID.
     * 
     * @param interviewId the ID of the interview to find.
     * @return the found InterviewDto.
     */
    public InterviewDto findInterviewById(Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with ID: " + interviewId));
        return InterviewMapper.toDto(interview);
    }

    /**
     * Cancel an interview by ID.
     * 
     * @param interviewId the ID of the interview to cancel.
     * @param reason the reason for cancellation.
     * @return the updated InterviewDto with status set to CANCELLED.
     */
    @Transactional
    public InterviewDto cancelInterview(Long interviewId, String reason) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with ID: " + interviewId));
        
        interview.setStatus(Interview.InterviewStatus.CANCELLED);
        
        // Additional logic could be added here to handle the cancellation reason
        
        try {
            Interview updatedInterview = interviewRepository.saveAndFlush(interview);
            return InterviewMapper.toDto(updatedInterview);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to cancel Interview due to server error.");
        }
    }
    
    
    /**
     * Retrieve all interviews.
     * 
     * @return a list of InterviewDto objects.
     */
    public List<InterviewDto> getAllInterviews() {
        try {
            List<Interview> interviews = interviewRepository.findAll();
            return interviews.stream()
                    .map(InterviewMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to retrieve interviews due to server error.");
        }
    }
    
    /**
     * Get the count of scheduled interviews for an interviewer.
     *
     * @param interviewerId the ID of the interviewer.
     * @return number of scheduled interviews.
     */
    public long countScheduledInterviewsForInterviewer(Long userId) {
        return getUpcomingInterviewsForInterviewer(userId).size();
    }
    
    /**
     * Get all upcoming interviews for an interviewer.
     *
     * @param interviewerId the ID of the interviewer.
     * @return list of InterviewDto objects.
     */
    public List<InterviewDto> getUpcomingInterviewsForInterviewer(Long userId) {
    	long interviewerId = interviewerRepository.findByUser_UserId(userId).get().getInterviewerId();
        return interviewRepository.findByInterviewer_InterviewerIdAndStatusOrderByDateAsc(interviewerId, Interview.InterviewStatus.BOOKED)
                .stream()
                .map(InterviewMapper::toDto)
                .collect(Collectors.toList());
    }
   
    /**
     * Get all upcoming interviews for an interviewee.
     * 
     * @param userId
     * @return
     */
    public List<InterviewDto> getUpcomingInterviewsForInterviewee(Long userId) {
    	long intervieweeId = intervieweeRepository.findByUser_UserId(userId).get().getIntervieweeId();
        return interviewRepository.findByInterviewee_IntervieweeIdAndStatusOrderByDateAsc(intervieweeId, Interview.InterviewStatus.BOOKED)
                .stream()
                .map(InterviewMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get count of upcoming interviews
     * 
     * @param userId
     * @return
     */
    public Long getUpcomingInterviewCount(Long userId) {
        return (long) getUpcomingInterviewsForInterviewee(userId).size();
    }

    /**
     * Get count of completed interviews
     * 
     * @param userId
     * @return
     */
    public Long getCompletedInterviewCount(Long userId) {
    	long intervieweeId = intervieweeRepository.findIntervieweeIdByUserId(userId);
        return interviewRepository.countCompletedInterviews(intervieweeId);
    }

    /**
     * Get list of completed interviews
     * 
     * @param userId
     * @return
     */
    public List<InterviewDto> getCompletedInterviews(Long userId) {
    	long intervieweeId = intervieweeRepository.findIntervieweeIdByUserId(userId);
        List<Interview> interviews = interviewRepository.findCompletedInterviews(intervieweeId);
        return interviews.stream()
                .map(InterviewMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get interviews for a single date where the user is interviewer or interviewee.
     * 
     * @param date
     * @param userId
     * @return
     */
    public List<InterviewDto> getInterviewsByDateForUser(LocalDate date, Long userId) {
        List<Interview> interviews = interviewRepository.findByDateAndUser(date, userId);
        return interviews.stream()
                .map(InterviewMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get interviews in a date range (week or any range) for a given user.
     * 
     * @param start
     * @param end
     * @param userId
     * @return
     */
    public List<InterviewDto> getInterviewsInRangeForUser(LocalDate start, LocalDate end, Long userId) {
        List<Interview> interviews = interviewRepository.findByDateRangeAndUser(start, end, userId);
        return interviews.stream()
                .map(InterviewMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get interviews for a specific month where the user is involved.
     * 
     * @param year
     * @param month
     * @param userId
     * @return
     */
    public List<InterviewDto> getInterviewsByMonthForUser(int year, int month, Long userId) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
        return getInterviewsInRangeForUser(startOfMonth, endOfMonth, userId);
    }

    /**
     * Reschedules the interview.
     * 
     * @param interviewId
     * @param interviewDto
     * @return
     */
    @Transactional
    public InterviewDto rescheduleInterview(Long interviewId, InterviewDto interviewDto) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with ID: " + interviewId));

        if (interviewDto.getDate() != null) {
            interview.setDate(interviewDto.getDate());
        }
        if (interviewDto.getStartTime() != null) {
            interview.setStartTime(interviewDto.getStartTime());
        }
        if (interviewDto.getDuration() != null) {
            interview.setDuration(interviewDto.getDuration());
        }
        if (interviewDto.getEndTime() != null) {
            interview.setEndTime(interviewDto.getEndTime());
        }
        if (interviewDto.getInterviewLink() != null) {
            interview.setInterviewLink(interviewDto.getInterviewLink());
        }
        
        try {
            Interview updated = interviewRepository.saveAndFlush(interview);
            return InterviewMapper.toDto(updated);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to reschedule interview: " + e.getMessage());
        }
    }
    
    /**
     * Fetch all 'past' interviews for an interviewee, then apply dynamic filters.
     * 
     * @param dbUserId
     * @param startDate
     * @param endDate
     * @param status
     * @param timezone
     * @param filterStartTime
     * @param filterEndTime
     * @return
     */
    public List<InterviewDto> getPastSessionsWithFiltersForCurrentUser(
            Long dbUserId,
            LocalDate startDate,
            LocalDate endDate,
            String status,
            String timezone,
            LocalTime filterStartTime,
            LocalTime filterEndTime
    ) {
        List<Interview> pastInterviews = interviewRepository.findPastInterviews(dbUserId);

        List<Interview> filtered = pastInterviews.stream()
            .filter(interview -> {
                if (startDate != null || endDate != null) {
                    LocalDate d = interview.getDate();
                    if (startDate != null && d.isBefore(startDate)) return false;
                    if (endDate != null && d.isAfter(endDate)) return false;
                }
                return true;
            })
            .filter(interview -> {
                if (status != null && !status.isBlank()) {
                    return interview.getStatus().name().equalsIgnoreCase(status);
                }
                return true;
            })
            .filter(interview -> {
                if (timezone != null && !timezone.isBlank()) {
                    return timezone.equals(interview.getTimezone());
                }
                return true;
            })
            .filter(interview -> {
                if (filterStartTime != null || filterEndTime != null) {
                    LocalTime st = interview.getStartTime();
                    LocalTime en = interview.getEndTime() != null
                            ? interview.getEndTime()
                            : LocalTime.MIDNIGHT;

                    if (filterStartTime != null && st.isBefore(filterStartTime)) {
                        return false;
                    }
                    if (filterEndTime != null && en.isAfter(filterEndTime)) {
                        return false;
                    }
                }
                return true;
            })
            .collect(Collectors.toList());
        
        filtered = filtered.stream().filter((interview) -> interview.getInterviewee().getUser().getUserId() == userService.getCurrentUser().getUserId()).collect(Collectors.toList());

        return filtered.stream()
                .map(InterviewMapper::toDto)
                .collect(Collectors.toList());
    }
    
}
