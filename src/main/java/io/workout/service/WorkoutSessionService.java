package io.workout.service;

import io.github.kusoroadeolu.clique.Clique;
import io.github.kusoroadeolu.clique.components.Table;
import io.github.kusoroadeolu.clique.configuration.TableType;
import io.workout.alert.AlertContext;
import io.workout.alert.AlertManager;
import io.workout.authentication.SessionContext;
import io.workout.model.Exercise;
import io.workout.model.SessionEntry;
import io.workout.model.WorkoutSession;
import io.workout.repository.ExerciseRepository;
import io.workout.repository.WorkoutSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WorkoutSessionService {

    @Autowired
    private WorkoutSessionRepository workoutSessionRepository;
    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private AlertManager alertManager;

    public void createSession(List<SessionEntry> sessionEntries, int duration, String sessionNotes) {
        WorkoutSession workoutSession = new WorkoutSession(SessionContext.getUser().getId(), LocalDateTime.now(), duration, sessionNotes, sessionEntries);
        workoutSessionRepository.save(workoutSession);
        alertManager.evaluate(new AlertContext(workoutSession, null));
    }

    public void viewSessions() {
        List<WorkoutSession> workoutSessions = workoutSessionRepository.findByUserIdOrderBySessionDateTimeDesc(SessionContext.getUser().getId());
        System.out.println("| WORKOUT SESSIONS |");
        Table workoutSessionTable = Clique.table(TableType.BOX_DRAW)
                .headers(
                        "[*blue, bold]DATE[/]",
                        "[*blue, bold]DURATION[/]",
                        "[*blue, bold]NOTES[/]",
                        "[*blue, bold]NUMBER OF EXERCISES[/]",
                        "[*blue, bold]TOTAL SESSION VOLUME[/]"
                );
        for(WorkoutSession workoutSession : workoutSessions) {
            workoutSessionTable.row(workoutSession.getSessionDateTime().toString(), String.valueOf(workoutSession.getDuration()),workoutSession.getNotes(), String.valueOf(workoutSession.getSessionEntries().size()), getTotalWorkoutSessionVolumeAsString(workoutSession));
        }
        workoutSessionTable.render();
    }

    private String getTotalWorkoutSessionVolumeAsString(WorkoutSession workoutSession) {
        int totalVolume = 0;
        for(SessionEntry sessionEntry : workoutSession.getSessionEntries()) {
            Optional<Exercise> exerciseOptional = exerciseRepository.findById(sessionEntry.exerciseId());
            if (exerciseOptional.isPresent()) {
                Exercise exercise = exerciseOptional.get();
                totalVolume = totalVolume + exercise.calculateVolume(sessionEntry);
            }
        }
        return String.valueOf(totalVolume);
    }
}
