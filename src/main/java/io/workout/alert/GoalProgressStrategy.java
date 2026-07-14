package io.workout.alert;

import io.workout.authentication.SessionContext;
import io.workout.interfaces.AlertStrategy;
import io.workout.repository.WorkoutSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class GoalProgressStrategy implements AlertStrategy {

    @Autowired
    private WorkoutSessionRepository workoutSessionRepository;

    @Override
    public boolean supports(AlertContext context) {
        return (context.workoutSession() != null);
    }

    @Override
    public void evaluate(AlertContext context) {
        int workoutSessionsInWeek = workoutSessionRepository.findByUserIdAndSessionDateTimeBetween(SessionContext.getUser().getId(), LocalDateTime.now().minusDays(7), LocalDateTime.now()).size();
        int sessionGoal =  SessionContext.getUser().getWeeklySessionGoal();
        if(workoutSessionsInWeek < sessionGoal) {
            System.out.println(ANSI_YELLOW + "[⚠ ALERT]: You are below your weekly workout session goal! You have logged " + workoutSessionsInWeek + " workout sessions but your goal is " + sessionGoal + "." + ANSI_RESET );
        }
    }
}
