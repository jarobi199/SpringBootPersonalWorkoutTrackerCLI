package io.workout.alert;

import io.workout.interfaces.AlertStrategy;
import io.workout.repository.WorkoutSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OvertrainingStrategy implements AlertStrategy {

    @Autowired
    private WorkoutSessionRepository workoutSessionRepository;

    @Override
    public boolean supports(AlertContext context) {
        return (context.workoutSession() != null);
    }

    @Override
    public void evaluate(AlertContext context) {
        //Warns the user that the muscle group may need rest
    }
}


//OvertrainingStrategy	supports() — true when context.getWorkoutSession() != null. evaluate() — fires when the same primary muscle group has been trained on consecutive days.
// Warns the user that the muscle group may need rest. Called from SessionService when a session is completed.