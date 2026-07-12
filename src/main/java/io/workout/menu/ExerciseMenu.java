package io.workout.menu;

import io.workout.enums.Equipment;
import io.workout.enums.ExerciseType;
import io.workout.enums.MuscleGroup;
import io.workout.interfaces.IMenu;
import io.workout.model.Exercise;
import io.workout.service.ExerciseService;
import io.workout.util.InputHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExerciseMenu implements IMenu {

    @Autowired
    private ExerciseService exerciseService;

    @Override
    public void show() {
        int choice;
        do {
            printOptions();
            choice = InputHandler.getIntegerInput();
            switch (choice) {
                case 1 -> listAllExercises();
                case 2 -> addExercise();
                case 3 -> viewExerciseDetail();
                case 4 -> deleteExercise();
            }
        }
        while (choice != 0);
    }

    public void viewExerciseDetail() {
        Exercise exercise = listExercisesAndSelect();
        if (exercise != null) {
            exerciseService.viewDetails(exercise);
        }
    }

    public void deleteExercise() {
        Exercise exercise = listExercisesAndSelect();
        if (exercise != null) {
            boolean success = exerciseService.deleteExercise(exercise);
            if (success) {
                System.out.println("Exercise - " + exercise.getName() + " has been deleted");
            }
            else
            {
                System.out.println("Exercise - " + exercise.getName() + " could not be deleted! It references other workout sessions.");
            }
        }
    }

    public void addExercise() {
        System.out.println("Please enter the exercise type (STRENGTH, CARDIO, BODYWEIGHT):");
        ExerciseType exerciseType = ExerciseType.valueOf(InputHandler.getStringInput().toUpperCase());
        System.out.println("Enter exercise name:");
        String exerciseName = InputHandler.getStringInput();
        System.out.println("Enter exercise muscle group (CHEST, BACK, SHOULDERS, BICEPS, TRICEPS, LEGS, CORE, FULL_BODY , CARDIO):");
        MuscleGroup muscleGroup = MuscleGroup.valueOf(InputHandler.getStringInput().toUpperCase());
        System.out.println("Enter exercise equipment (BARBELL, DUMBBELL, CABLE, MACHINE, BODYWEIGHT, TREADMILL, BIKE, ROWER, OTHER):");
        Equipment equipment = Equipment.valueOf(InputHandler.getStringInput().toUpperCase());
        System.out.println("Enter exercise notes:");
        String notes = InputHandler.getStringInput();

        exerciseService.createExercise(exerciseType, exerciseName, muscleGroup, equipment, notes);
        System.out.println("Exercise - " + exerciseName + " has been created!");
    }

    public void listAllExercises() {
        exerciseService.listExercises();
    }

    public void printOptions() {
        System.out.println();
        System.out.println("[1] List all exercises");
        System.out.println("[2] Add exercise");
        System.out.println("[3] View exercise detail");
        System.out.println("[4] Delete exercise");
        System.out.println("[0] Back");
        System.out.println("Please make a selection:");
    }

    public Exercise listExercisesAndSelect() {
        int number = 1;
        Exercise vehicle = null;
        int choice = 0;

        List<Exercise> exercises = exerciseService.findAllExercises();
        if (!exercises.isEmpty()) {
            for (Exercise e : exercises) {
                System.out.println("[" + number + "] " +  e.getExerciseDisplay());
                number++;
            }
            System.out.println("Select an exercise:");
            choice = InputHandler.getIntegerInput();
            vehicle = exercises.get(choice - 1);
        }
        else
        {
            System.out.println("There are no exercises available.");
        }

        return vehicle;
    }
}
