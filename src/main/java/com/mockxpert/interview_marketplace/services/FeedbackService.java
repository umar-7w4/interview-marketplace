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

/**
 * 
 * Service class for managing all the feedback related services.
 * 
 * @author Umar Mohammad
 */
@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;

    /**
     * Register a new feedback.
     * @param feedbackDto the feedback data transfer object containing registration information.
     * @return the saveAndFlushd FeedbackDto.
     */
    @Transactional
    public FeedbackDto registerFeedback(FeedbackDto feedbackDto) {
        try {
            // Validate Interview Existence
            Interview interview = interviewRepository.findById(feedbackDto.getInterviewId())
                    .orElseThrow(() -> new ResourceNotFoundException("Interview not found with ID: " + feedbackDto.getInterviewId()));

            // Validate Giver and Receiver Users
            if (feedbackDto.getGiver() == null || feedbackDto.getReceiver() == null) {
                throw new BadRequestException("Both giver and receiver must be provided.");
            }

            long giverId = feedbackDto.getGiver().getUserId();
            
            long receiverId = feedbackDto.getReceiver().getUserId();
            
            User giver = userRepository.findById(giverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Giver user not found with ID: " + giverId));

            User receiver = userRepository.findById(receiverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Receiver user not found with ID: " + receiverId));


            // Validate Rating
            if (feedbackDto.getRating() < 1 || feedbackDto.getRating() > 10) {
                throw new BadRequestException("Rating must be between 1 and 10.");
            }

            // Convert DTO to Entity and Save Feedback
            Feedback feedback = FeedbackMapper.toEntity(feedbackDto, interview, giver, receiver);
            Feedback savedFeedback = feedbackRepository.saveAndFlush(feedback);

            // Send Email Notification (Handled Separately)
            try {
                sendFeedbackNotification(receiver, giver, feedbackDto);
            } catch (Exception e) {
                // Log email failure but don't block feedback creation
                System.err.println("Failed to send feedback email: " + e.getMessage());
            }

            return FeedbackMapper.toDto(savedFeedback);

        } catch (ResourceNotFoundException | BadRequestException e) {
            throw e; // Rethrow known exceptions for proper HTTP responses
        } catch (Exception e) {
            throw new InternalServerErrorException("An unexpected error occurred while registering feedback: " + e.getMessage());
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
            Feedback updatedFeedback = feedbackRepository.saveAndFlush(feedback);
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
    
    private void sendFeedbackNotification(User receiver, User giver, FeedbackDto feedbackDto) {
        String subject = "You've Received Interview Feedback!";
        String message = "<p>Dear " + receiver.getFullName() + ",</p>" +
                "<p>You have received feedback from <strong>" + giver.getFullName() + "</strong> regarding your interview.</p>" +
                "<p><strong>Rating:</strong> " + feedbackDto.getRating() + "/10</p>" +
                "<p><strong>Comments:</strong> " + feedbackDto.getComments() + "</p>" +
                "<p><strong>Positives:</strong> " + feedbackDto.getPositives() + "</p>" +
                "<p><strong>Negatives:</strong> " + feedbackDto.getNegatives() + "</p>" +
                "<p><strong>Improvements:</strong> " + feedbackDto.getImprovements() + "</p>" +
                "<br><p>Best regards,</p>" +
                "<p><strong>MockXpert Team</strong></p>";

        emailService.sendNotificationEmail(receiver.getEmail(), subject, message);
    }
    
    /**
     * Get the average rating for an interviewer based on received feedback.
     *
     * @param interviewerId the ID of the interviewer.
     * @return average rating (or 0 if no feedback).
     */
    public double getAverageRatingForInterviewer(Long interviewerId) {
        List<Feedback> feedbackList = feedbackRepository.findByReceiver_UserId(interviewerId);
        if (feedbackList.isEmpty()) return 0.0;

        return feedbackList.stream()
                .mapToDouble(Feedback::getRating)
                .average()
                .orElse(0.0);
    }
    
    /**
     * Get all feedback received by the user with the given userId.
     */
    @Transactional(readOnly = true)
    public List<FeedbackDto> getFeedbackForUser(Long userId) {
        List<Feedback> feedbackList = feedbackRepository.findFeedbackByReceiver(userId);

        return feedbackList.stream()
                .map(FeedbackMapper::toDto)
                .collect(Collectors.toList());
    }


}
