package com.mockxpert.interview_marketplace.mappers;

import com.mockxpert.interview_marketplace.dto.FeedbackDto;
import com.mockxpert.interview_marketplace.entities.Feedback;
import com.mockxpert.interview_marketplace.entities.Interview;
import com.mockxpert.interview_marketplace.entities.User;

public class FeedbackMapper {

    public static FeedbackDto toDto(Feedback feedback) {
        if (feedback == null) {
            return null;
        }

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

    public static Feedback toEntity(FeedbackDto dto, Interview interview, User giver, User receiver) {
        if (dto == null || interview == null || giver == null || receiver == null) {
            return null;
        }

        Feedback feedback = new Feedback();
        feedback.setInterview(interview);
        feedback.setGiver(giver);
        feedback.setReceiver(receiver);
        feedback.setRating(dto.getRating());
        feedback.setComments(dto.getComments());
        feedback.setPositives(dto.getPositives());
        feedback.setNegatives(dto.getNegatives());
        feedback.setImprovements(dto.getImprovements());

        return feedback;
    }
}
