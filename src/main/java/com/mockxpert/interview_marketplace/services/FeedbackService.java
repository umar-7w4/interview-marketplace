package com.mockxpert.interview_marketplace.services;

import com.mockxpert.interview_marketplace.dto.FeedbackDto;
import com.mockxpert.interview_marketplace.entities.Feedback;
import com.mockxpert.interview_marketplace.entities.Interview;
import com.mockxpert.interview_marketplace.entities.User;
import com.mockxpert.interview_marketplace.exceptions.ResourceNotFoundException;
import com.mockxpert.interview_marketplace.exceptions.BadRequestException;
import com.mockxpert.interview_marketplace.exceptions.InternalServerErrorException;
import com.mockxpert.interview_marketplace.mappers.FeedbackMapper;
import com.mockxpert.interview_marketplace.repositories.FeedbackRepository;
import com.mockxpert.interview_marketplace.repositories.InterviewRepository;
import com.mockxpert.interview_marketplace.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Register a new feedback.
     * @param feedbackDto the feedback data transfer object containing registration information.
     * @return the saved FeedbackDto.
     */
    @Transactional
    public FeedbackDto registerFeedback(FeedbackDto feedbackDto) {
        Interview interview = interviewRepository.findById(feedbackDto.getInterviewId())
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with ID: " + feedbackDto.getInterviewId()));

        User giver = userRepository.findById(feedbackDto.getGiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Giver user not found with ID: " + feedbackDto.getGiverId()));

        User receiver = userRepository.findById(feedbackDto.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver user not found with ID: " + feedbackDto.getReceiverId()));

        if (feedbackDto.getRating() < 1 || feedbackDto.getRating() > 10) {
            throw new BadRequestException("Rating must be between 1 and 10.");
        }

        Feedback feedback = FeedbackMapper.toEntity(feedbackDto, interview, giver, receiver);
        try {
            Feedback savedFeedback = feedbackRepository.save(feedback);
            return FeedbackMapper.toDto(savedFeedback);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to save Feedback due to server error.");
        }
    }

    /**
     * Update feedback information.
     * @param feedbackId the ID of the feedback to update.
     * @param feedbackDto the feedback data transfer object containing updated information.
     * @return the updated FeedbackDto.
     */
    @Transactional
    public FeedbackDto updateFeedback(Long feedbackId, FeedbackDto feedbackDto) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with ID: " + feedbackId));

        if (feedbackDto.getRating() < 1 || feedbackDto.getRating() > 10) {
            throw new BadRequestException("Rating must be between 1 and 10.");
        }

        feedback.setRating(feedbackDto.getRating());
        feedback.setComments(feedbackDto.getComments());
        feedback.setPositives(feedbackDto.getPositives());
        feedback.setNegatives(feedbackDto.getNegatives());
        feedback.setImprovements(feedbackDto.getImprovements());

        try {
            Feedback updatedFeedback = feedbackRepository.save(feedback);
            return FeedbackMapper.toDto(updatedFeedback);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to update Feedback due to server error.");
        }
    }

    /**
     * Find feedback by ID.
     * @param feedbackId the ID of the feedback to find.
     * @return the found FeedbackDto.
     */
    public FeedbackDto findFeedbackById(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with ID: " + feedbackId));
        return FeedbackMapper.toDto(feedback);
    }

    /**
     * Delete feedback by ID.
     * @param feedbackId the ID of the feedback to delete.
     * @return true if the feedback was deleted successfully, false otherwise.
     */
    @Transactional
    public boolean deleteFeedback(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with ID: " + feedbackId));

        try {
            feedbackRepository.delete(feedback);
            return true;
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to delete Feedback due to server error.");
        }
    }

    /**
     * Get a list of all feedback.
     * @return a list of all Feedback entities as DTOs.
     */
    public List<FeedbackDto> findAllFeedbacks() {
        List<Feedback> feedbacks = feedbackRepository.findAll();
        return feedbacks.stream()
                .map(FeedbackMapper::toDto)
                .collect(Collectors.toList());
    }
}