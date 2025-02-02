package com.mockxpert.interview_marketplace.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

/**
 * Service to interact with Google Calendar API and schedule Google Meet events.
 * Handles authentication, event creation, and configuration.
 */
@Service
public class GoogleCalendarService {
    private static final String APPLICATION_NAME = "MockXpert";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.refresh.token}")
    private String refreshToken;

    @Value("${google.calendar.id}")
    private String calendarId;

    /**
     * Obtains OAuth 2.0 credentials for Google API authentication.
     * @return Credential object for API authentication.
     * @throws IOException if credentials retrieval fails.
     * @throws GeneralSecurityException if security exception occurs.
     */
    private Credential getCredentials() throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setClientSecrets(clientId, clientSecret)
                .build()
                .setRefreshToken(refreshToken);
    }

    /**
     * Creates a Google Meet event using Google Calendar API.
     * @param title Event title.
     * @param description Event description.
     * @param interviewerEmail Email of the interviewer.
     * @param intervieweeEmail Email of the interviewee.
     * @param startTime Event start time as LocalDateTime.
     * @param endTime Event end time as LocalDateTime.
     * @return Google Meet link for the scheduled event.
     * @throws IOException if API call fails.
     * @throws GeneralSecurityException if security exception occurs.
     */
    public String createGoogleMeetEvent(String title, String description, String interviewerEmail, 
                                        String intervieweeEmail, LocalDateTime startTime, LocalDateTime endTime) 
                                        throws IOException, GeneralSecurityException {
        Calendar service = new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), 
                                                JSON_FACTORY, getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();

        Event event = new Event().setSummary(title).setDescription(description);

        EventDateTime start = new EventDateTime()
                .setDateTime(new DateTime(startTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()))
                .setTimeZone("UTC");
        event.setStart(start);

        EventDateTime end = new EventDateTime()
                .setDateTime(new DateTime(endTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()))
                .setTimeZone("UTC");
        event.setEnd(end);
        
        // Add attendees
        event.setAttendees(Arrays.asList(
            new EventAttendee().setEmail(interviewerEmail),
            new EventAttendee().setEmail(intervieweeEmail)
        ));

        // Enable Google Meet
        ConferenceData conferenceData = new ConferenceData();
        ConferenceSolutionKey conferenceSolutionKey = new ConferenceSolutionKey().setType("hangoutsMeet");
        CreateConferenceRequest createConferenceRequest = new CreateConferenceRequest()
                .setRequestId("meet-" + System.currentTimeMillis())
                .setConferenceSolutionKey(conferenceSolutionKey);
        conferenceData.setCreateRequest(createConferenceRequest);
        event.setConferenceData(conferenceData);

        // Insert event into Google Calendar
        event = service.events().insert("primary", event)
                .setConferenceDataVersion(1)
                .execute();

        return event.getHangoutLink();
    }
}