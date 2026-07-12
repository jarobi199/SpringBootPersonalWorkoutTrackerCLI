package io.workout.menu;

import io.workout.interfaces.IMenu;
import io.workout.model.Exercise;
import io.workout.model.SessionEntry;
import io.workout.model.WorkoutSession;
import io.workout.service.WorkoutSessionService;
import io.workout.util.InputHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SessionMenu implements IMenu {

    @Autowired
    private WorkoutSessionService workoutSessionService;
    @Autowired
    private ExerciseMenu exerciseMenu;

    @Override
    public void show() {
        int choice;
        do {
            printOptions();
            choice = InputHandler.getIntegerInput();
            switch (choice) {
                case 1 -> startNewSession();
                case 2 -> viewSessions();
                case 3 -> viewSessionDetails();
                case 4 -> deleteSession();
            }
        }
        while (choice != 0);
    }

    public void viewSessionDetails() {
        WorkoutSession workoutSession = listWorkoutSessionsAndSelect();
        workoutSessionService.viewSessionDetails(workoutSession);
    }

    public void deleteSession() {
        WorkoutSession workoutSession = listWorkoutSessionsAndSelect();
        System.out.println("Are you sure that you want to delete this session? (Y/N):");
        String confirmation = InputHandler.getStringInput();
        if (confirmation.equalsIgnoreCase("Y")) {
            workoutSessionService.deleteSession(workoutSession);
            System.out.println("The workout session has been deleted!");
        }
    }

    public void viewSessions() {
        workoutSessionService.viewSessions();
    }

    @Override
    public void printOptions() {
        System.out.println();
        System.out.println("[1] Start new session");
        System.out.println("[2] View all sessions");
        System.out.println("[3] View session detail");
        System.out.println("[4] Delete session");
        System.out.println("[0] Back");
        System.out.println("Please make a selection:");
    }

    public void startNewSession() {
        System.out.println("Select exercises for the session:");
        List<SessionEntry> sessionEntries = getExercisesAndSessionEntries();
        System.out.println("Enter the workout session duration:");
        int duration = InputHandler.getIntegerInput();
        System.out.println("Enter the workout session notes:");
        String workoutSessionNotes = InputHandler.getStringInput();

        workoutSessionService.createSession(sessionEntries, duration, workoutSessionNotes);
        System.out.println("Workout session has been created!");
    }

    private List<SessionEntry> getExercisesAndSessionEntries() {
        String keepGoing = "Y";

        List<SessionEntry> sessionEntries = new ArrayList<>();
        while (keepGoing.equalsIgnoreCase("Y")) {
            int sets = 0;
            int reps = 0;
            int weightKg = 0;
            int duration = 0;
            int distanceKm = 0;
            int addedWeightKg = 0;
            String sessionEntryNotes = "";

            Exercise exercise = exerciseMenu.listExercisesAndSelect();
            switch (exercise.getExerciseType()) {
                case STRENGTH -> {
                    System.out.println("Enter the sets:");
                    sets = InputHandler.getIntegerInput();
                    System.out.println("Enter the reps:");
                    reps = InputHandler.getIntegerInput();
                    System.out.println("Enter the weight (Kg):");
                    weightKg = InputHandler.getIntegerInput();
                }
                case CARDIO ->  {
                    System.out.println("Enter the distance:");
                    distanceKm = InputHandler.getIntegerInput();
                }
                case BODYWEIGHT ->  {
                    System.out.println("Enter the sets:");
                    sets = InputHandler.getIntegerInput();
                    System.out.println("Enter the reps:");
                    reps = InputHandler.getIntegerInput();
                    System.out.println("Enter the added weight (Kg):");
                    addedWeightKg = InputHandler.getIntegerInput();
                }
            }
            System.out.println("Enter the session entry notes:");
            sessionEntryNotes = InputHandler.getStringInput();
            System.out.println("Enter the session entry duration:");
            duration = InputHandler.getIntegerInput();

            SessionEntry sessionEntry = new SessionEntry(exercise.getId(), exercise.getName(), exercise.getExerciseType(), sets, reps, weightKg, duration, distanceKm, addedWeightKg, sessionEntryNotes);
            sessionEntries.add(sessionEntry);

            System.out.println("Continue? (Y/N):");
            keepGoing = InputHandler.getStringInput();
        }

        return sessionEntries;
    }

    public WorkoutSession listWorkoutSessionsAndSelect() {
        int number = 1;
        WorkoutSession workoutSession = null;
        int choice = 0;

        List<WorkoutSession> workoutSessions = workoutSessionService.getAllSessions();
        if (!workoutSessions.isEmpty()) {
            for (WorkoutSession w : workoutSessions) {
                System.out.println("[" + number + "] " +  w.getDisplayName());
                number++;
            }
            System.out.println("Select a workout session:");
            choice = InputHandler.getIntegerInput();
            workoutSession = workoutSessions.get(choice - 1);
        }
        else
        {
            System.out.println("There are no exercises available.");
        }

        return workoutSession;
    }
}

