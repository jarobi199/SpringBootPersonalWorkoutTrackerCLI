package io.workout.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "sessions")
public class WorkoutSession {
    @Id
    private String id;
    private String userId;
    private LocalDateTime sessionDateTime;
    private int duration;
    private String notes;
    private List<SessionEntry> sessionEntries;

    public WorkoutSession() {
        //No argument constructor
    }

    public WorkoutSession(String userId, LocalDateTime sessionDateTime, int duration, String notes, List<SessionEntry> sessionEntries) {
        this.userId = userId;
        this.sessionDateTime = sessionDateTime;
        this.duration = duration;
        this.notes = notes;
        this.sessionEntries = sessionEntries;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getSessionDateTime() {
        return sessionDateTime;
    }

    public void setSessionDateTime(LocalDateTime sessionDateTime) {
        this.sessionDateTime = sessionDateTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<SessionEntry> getSessionEntries() {
        return sessionEntries;
    }

    public void setSessionEntries(List<SessionEntry> sessionEntries) {
        this.sessionEntries = sessionEntries;
    }

    public String getDisplayName() {
        return sessionDateTime.toString() + " - Notes: " + notes;
    }
}

