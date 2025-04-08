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
import java.util.Optional;
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
     * 
     * @param feedbackDto the feedback data transfer object containing registration information.
     * @return the saveAndFlushd FeedbackDto.
     */
    @Transactional
    public FeedbackDto registerFeedback(FeedbackDto feedbackDto) {
        try {
            Interview interview = interviewRepository.findById(feedbackDto.getInterviewId())
                    .orElseThrow(() -> new ResourceNotFoundException("Interview not found with ID: " + feedbackDto.getInterviewId()));

            if (feedbackDto.getGiverId() == null || feedbackDto.getReceiverId() == null) {
                throw new BadRequestException("Both giver and receiver must be provided.");
            }

            long giverId = feedbackDto.getGiverId();
            
            long receiverId = feedbackDto.getReceiverId();
            
            User giver = userRepository.findById(giverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Giver user not found with ID: " + giverId));

            User receiver = userRepository.findById(receiverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Receiver user not found with ID: " + receiverId));


            if (feedbackDto.getRating() < 1 || feedbackDto.getRating() > 10) {
                throw new BadRequestException("Rating must be between 1 and 10.");
            }

            Feedback feedback = FeedbackMapper.toEntity(feedbackDto, interview, giver, receiver);
            Feedback savedFeedback = feedbackRepository.saveAndFlush(feedback);

            try {
                sendFeedbackNotification(receiver, giver, feedbackDto);
            } catch (Exception e) {
                System.err.println("Failed to send feedback email: " + e.getMessage());
            }

            return FeedbackMapper.toDto(savedFeedback);

        } catch (ResourceNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerErrorException("An unexpected error occurred while registering feedback: " + e.getMessage());
        }
    }


    /**
     * Update feedback information.
     * 
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
     * 
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
     * 
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
     * 
     * @return a list of all Feedback entities as DTOs.
     */
    public List<FeedbackDto> findAllFeedbacks() {
        List<Feedback> feedbacks = feedbackRepository.findAll();
        return feedbacks.stream()
                .map(FeedbackMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Sends a feedback notification email using a rich HTML template.
     *
     * @param receiver  The user who receives the feedback.
     * @param giver     The user who gives the feedback.
     * @param feedbackDto The feedback details.
     */
    private void sendFeedbackNotification(User receiver, User giver, FeedbackDto feedbackDto) {
        String subject = "You've Received Interview Feedback!";

        String plainMessage = String.format(
            "Dear %s,<br/><br/>" +
            "You have received feedback from <strong>%s</strong> regarding your interview.<br/><br/>" +
            "<strong>Rating:</strong> %s/10<br/>" +
            "<strong>Comments:</strong> %s<br/>" +
            "<strong>Positives:</strong> %s<br/>" +
            "<strong>Negatives:</strong> %s<br/>" +
            "<strong>Improvements:</strong> %s<br/><br/>" +
            "Best regards,<br/>" +
            "<strong>MockXpert Team</strong>",
            receiver.getFullName(),
            giver.getFullName(),
            feedbackDto.getRating(),
            feedbackDto.getComments(),
            feedbackDto.getPositives(),
            feedbackDto.getNegatives(),
            feedbackDto.getImprovements()
        );
        
        // Wrap the plain message in a full HTML email template.
        String htmlMessage = buildHtmlEmail(subject, plainMessage);
        
        // Use your email service to send the final HTML notification.
        emailService.sendNotificationEmail(receiver.getEmail(), subject, htmlMessage);
    }

    /**
     * Helper method that wraps a plain HTML content string inside a full email template
     * following the MockXpert color palette and UI guidelines.
     *
     * @param headerTitle The title (subject) to display in the email header.
     * @param content     The HTML content to display in the body of the email.
     * @return A complete HTML string representing the email.
     */
    private String buildHtmlEmail(String headerTitle, String content) {
        return String.format(
            "<!DOCTYPE html>" +
            "<html>" +
              "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<title>%s</title>" +
              "</head>" +
              "<body style=\"margin: 0; padding: 0; background-color: #f4f4f4; font-family: Arial, sans-serif;\">" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%%\">" +
                  "<tr>" +
                    "<td align=\"center\" style=\"padding: 20px 10px;\">" +
                      "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" " +
                        "style=\"background-color: #ffffff; border-radius: 8px; overflow: hidden; " +
                        "box-shadow: 0 2px 8px rgba(0,0,0,0.1);\">" +
                        "<tr>" +
                          "<td align=\"center\" bgcolor=\"#6366f1\" " +
                            "style=\"padding: 30px 0; color: #ffffff; font-size: 28px; font-weight: bold;\">" +
                            "MockXpert" +
                          "</td>" +
                        "</tr>" +
                        "<tr>" +
                          "<td style=\"padding: 40px 30px; color: #333333;\">" +
                            "<p style=\"margin: 0; font-size: 16px; line-height: 1.5;\">Dear User,</p>" +
                            "<p style=\"margin: 20px 0 0 0; font-size: 16px; line-height: 1.5;\">%s</p>" +
                          "</td>" +
                        "</tr>" +
                        "<tr>" +
                          "<td align=\"center\" bgcolor=\"#f4f4f4\" " +
                            "style=\"padding: 20px; font-size: 12px; color: #777777;\">" +
                            "© 2025 MockXpert. All rights reserved." +
                          "</td>" +
                        "</tr>" +
                      "</table>" +
                    "</td>" +
                  "</tr>" +
                "</table>" +
              "</body>" +
            "</html>", headerTitle, content);
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
     *  
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<FeedbackDto> getFeedbackForUser(Long userId) {
        List<Feedback> feedbackList = feedbackRepository.findFeedbackByReceiver(userId);

        return feedbackList.stream()
            .map(FeedbackMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Finds the feedhack belonging to particular interview giveb by particular giver.
     * 
     * @param interviewId
     * @param giverId
     * @return
     */
    @Transactional(readOnly = true)
    public FeedbackDto findFeedbackByInterviewAndGiver(Long interviewId, Long giverId) {
        Optional<Feedback> existing = feedbackRepository.findByInterviewAndGiver(interviewId, giverId);
        return existing.map(FeedbackMapper::toDto).orElse(null);
    }


}
