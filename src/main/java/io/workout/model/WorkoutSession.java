package io.workout.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "sessions")
public class WorkoutSession {
    @Id
    private String id;
    private String userId;
    private LocalDateTime sessionDate;
    private int duration;
    private String notes;
    private List<SessionEntry> sessionEntries;

    public WorkoutSession(String userId, LocalDateTime sessionDate, int duration, String notes) {
        this.userId = userId;
        this.sessionDate = sessionDate;
        this.duration = duration;
        this.notes = notes;
        this.sessionEntries = new ArrayList<>();
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

    public LocalDateTime getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDateTime sessionDate) {
        this.sessionDate = sessionDate;
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
}

