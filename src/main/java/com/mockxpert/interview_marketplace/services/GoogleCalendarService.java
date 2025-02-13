package com.mockxpert.interview_marketplace.services;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.UUID;

/**
 * Service class for managing all google calendar related services.
 * 
 * @author Umar Mohammad
 */
@Service
public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "MockXpert";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    // The dedicated meeting account's email (configured in application.properties)
    @Value("${meeting.google.account.email}")
    private String meetingAccountEmail;

    /**
     * Creates a Google Meet event using an OAuth access token.
     * The event is created on the dedicated meeting account's calendar so that email invites are sent.
     *
     * @param accessToken      OAuth access token of the dedicated meeting account.
     * @param title            Event title.
     * @param description      Event description.
     * @param interviewerEmail Interviewer's email.
     * @param intervieweeEmail Interviewee's email.
     * @param startTime        Event start time.
     * @param endTime          Event end time.
     * @return Google Meet link.
     * @throws IOException              if the API call fails.
     * @throws GeneralSecurityException if a security exception occurs.
     */
    public String createGoogleMeetEvent(String accessToken, String title, String description,
                                        String interviewerEmail, String intervieweeEmail,
                                        LocalDateTime startTime, LocalDateTime endTime)
                                        throws IOException, GeneralSecurityException {

        // Initialize the Google Calendar API service using the dedicated account's access token.
        Calendar service = new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY, request -> request.getHeaders().setAuthorization("Bearer " + accessToken))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Create the event with the provided details.
        Event event = new Event()
                .setSummary(title)
                .setDescription(description);

        // Set start and end times.
        event.setStart(new EventDateTime()
                .setDateTime(new DateTime(startTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()))
                .setTimeZone("UTC"));
        event.setEnd(new EventDateTime()
                .setDateTime(new DateTime(endTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()))
                .setTimeZone("UTC"));

        // Set the dedicated meeting account as the organizer.
        event.setOrganizer(new Event.Organizer().setEmail(meetingAccountEmail));

        // Add attendees: the interviewee and the interviewer.
        event.setAttendees(Arrays.asList(
                new EventAttendee().setEmail(interviewerEmail),
                new EventAttendee().setEmail(intervieweeEmail)
        ));

        // Enable Google Meet conferencing.
        ConferenceData conferenceData = new ConferenceData();
        ConferenceSolutionKey conferenceSolutionKey = new ConferenceSolutionKey().setType("hangoutsMeet");
        CreateConferenceRequest createConferenceRequest = new CreateConferenceRequest()
                .setRequestId(UUID.randomUUID().toString())
                .setConferenceSolutionKey(conferenceSolutionKey);
        conferenceData.setCreateRequest(createConferenceRequest);
        event.setConferenceData(conferenceData);

        // Enable email notifications.
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(
                        new EventReminder().setMethod("email").setMinutes(1440), // 1 day before
                        new EventReminder().setMethod("email").setMinutes(60),   // 1 hour before
                        new EventReminder().setMethod("popup").setMinutes(10)    // 10 minutes before
                ));
        event.setReminders(reminders);

        // Insert the event into the dedicated account's primary calendar and send updates.
        event = service.events().insert("primary", event)
                .setConferenceDataVersion(1)
                .setSendUpdates("all")
                .execute();

        return event.getHangoutLink();
    }
}
