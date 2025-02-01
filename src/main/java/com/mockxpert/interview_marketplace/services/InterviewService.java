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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    /**
     * Register a new interview.
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
        try {
            Interview saveAndFlushdInterview = interviewRepository.saveAndFlush(interview);
            return InterviewMapper.toDto(saveAndFlushdInterview);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to saveAndFlush Interview due to server error.");
        }
    }

    /**
     * Update interview information.
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
    
    
}
