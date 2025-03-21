package com.mockxpert.interview_marketplace.mappers;

import com.mockxpert.interview_marketplace.dto.FeedbackDto;
import com.mockxpert.interview_marketplace.entities.Feedback;
import com.mockxpert.interview_marketplace.entities.Interview;
import com.mockxpert.interview_marketplace.entities.User;

/**
 * Mapper class that converts Data Transfer Object to feedback entity object.
 * 
 * @author Umar Mohammad
 */

public class FeedbackMapper {
	
	/**
	 * Feedback dto to entity. 
	 * 
	 * @param feedback
	 * @return
	 */

    public static FeedbackDto toDto(Feedback feedback) {
        FeedbackDto dto = new FeedbackDto();
        dto.setFeedbackId(feedback.getFeedbackId());
        dto.setInterviewId(feedback.getInterview().getInterviewId());
        dto.setGiverId(feedback.getGiver().getUserId());
        dto.setReceiverId(feedback.getReceiver().getUserId());
        dto.setRating(feedback.getRating());
        dto.setComments(feedback.getComments());
        dto.setPositives(feedback.getPositives());
        dto.setNegatives(feedback.getNegatives());
        dto.setImprovements(feedback.getImprovements());
        dto.setCreatedAt(feedback.getCreatedAt());
        return dto;
    }
    
    /**
     * Feedback entity to dto. 
     *
     * @param dto
     * @param interview
     * @param giver
     * @param receiver
     * @return
     */

    public static Feedback toEntity(FeedbackDto dto, Interview interview, User giver, User receiver) {
        Feedback feedback = new Feedback();
        feedback.setFeedbackId(dto.getFeedbackId());
        feedback.setInterview(interview);
        feedback.setGiver(giver);
        feedback.setReceiver(receiver);
        feedback.setRating(dto.getRating());
        feedback.setComments(dto.getComments());
        feedback.setPositives(dto.getPositives());
        feedback.setNegatives(dto.getNegatives());
        feedback.setImprovements(dto.getImprovements());
        // createdAt is set automatically in @PrePersist
        return feedback;
    }
}
