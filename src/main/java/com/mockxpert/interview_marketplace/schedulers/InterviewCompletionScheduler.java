package com.mockxpert.interview_marketplace.schedulers;

import com.mockxpert.interview_marketplace.entities.Interview;
import com.mockxpert.interview_marketplace.repositories.InterviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Logger;

/**
 * Scheduler to mark interviews as completed after their end time.
 */
@Component
public class InterviewCompletionScheduler {

    private static final Logger logger = Logger.getLogger(InterviewCompletionScheduler.class.getName());

    @Autowired
    private InterviewRepository interviewRepository;

    /**
     * Scheduled method to update completed interviews.
     * Runs every minute to mark interviews as completed.
     */
    @Scheduled(cron = "0 * * * * *") // Runs every minute
    public void markCompletedInterviews() {
    	LocalTime currentTime = LocalDateTime.now().toLocalTime();
    	List<Interview> overdue = interviewRepository.findByStatusAndEndTimeBefore(Interview.InterviewStatus.BOOKED, currentTime);

        if (!overdue.isEmpty()) {
            for (Interview interview : overdue) {
                interview.setStatus(Interview.InterviewStatus.COMPLETED);
                interviewRepository.save(interview);
                logger.info("Marked interview ID " + interview.getInterviewId() + " as COMPLETED.");
            }
        } else {
            logger.info("No ongoing interviews found to mark as COMPLETED.");
        }
    }
}
