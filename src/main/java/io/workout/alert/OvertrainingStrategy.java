package io.workout.alert;

import io.workout.authentication.SessionContext;
import io.workout.enums.MuscleGroup;
import io.workout.interfaces.AlertStrategy;
import io.workout.model.WorkoutSession;
import io.workout.repository.ExerciseRepository;
import io.workout.repository.WorkoutSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class OvertrainingStrategy implements AlertStrategy {

    @Autowired
    private WorkoutSessionRepository workoutSessionRepository;
    @Autowired
    private ExerciseRepository exerciseRepository;

    @Override
    public boolean supports(AlertContext context) {
        return (context.workoutSession() != null);
    }

    @Override
    public void evaluate(AlertContext context) {
        Set<MuscleGroup> currentMuscleGroups = getAllMuscleGroups(context.workoutSession());
        Set<MuscleGroup> previousDayMuscleGroups = new HashSet<>();

        List<WorkoutSession> workoutSessions = workoutSessionRepository.findByUserIdAndSessionDateTimeBetween(SessionContext.getUser().getId(), context.workoutSession().getSessionDateTime().minusDays(1), context.workoutSession().getSessionDateTime());
        workoutSessions.forEach(workoutSession -> previousDayMuscleGroups.addAll(getAllMuscleGroups(workoutSession)));

        Set<MuscleGroup> combinedSet = new HashSet<>(currentMuscleGroups);
        combinedSet.addAll(previousDayMuscleGroups);

        if(combinedSet.size() <  (currentMuscleGroups.size() + previousDayMuscleGroups.size())) {
            System.out.println(ANSI_YELLOW + "[⚠ ALERT]: You have already worked on the same muscle group for consecutive days! You need to take a rest. " + ANSI_RESET );
        }
    }

    private Set<MuscleGroup> getAllMuscleGroups(WorkoutSession workoutSession) {
        Set<MuscleGroup> muscleGroups = new HashSet<>();
        workoutSession.getSessionEntries().forEach(entry -> {
            exerciseRepository.findById(entry.exerciseId()).ifPresent(exercise -> muscleGroups.add(exercise.getMuscleGroup()));
        });

        return muscleGroups;
    }

}
