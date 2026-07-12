package io.workout.service;

import io.workout.authentication.SessionContext;
import io.workout.model.SessionEntry;
import io.workout.model.WorkoutSession;
import io.workout.repository.WorkoutSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WorkoutSessionService {

    @Autowired
    private WorkoutSessionRepository workoutSessionRepository;

    public List<WorkoutSession> findTop10WorkoutSessions() {
        return workoutSessionRepository.findTop10ByUserId(SessionContext.getUser().getId());
    }

    public void createSession(List<SessionEntry> sessionEntries, int duration, String sessionNotes) {
        WorkoutSession workoutSession = new WorkoutSession(SessionContext.getUser().getId(), LocalDateTime.now(), duration, sessionNotes, sessionEntries);
        workoutSessionRepository.save(workoutSession);
    }
}
