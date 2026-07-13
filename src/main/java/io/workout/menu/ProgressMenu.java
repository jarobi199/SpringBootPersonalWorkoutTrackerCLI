package io.workout.menu;

import io.workout.interfaces.IMenu;
import io.workout.model.Exercise;
import io.workout.service.ReportService;
import io.workout.util.InputHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class ProgressMenu implements IMenu {

    @Autowired
    private ReportService reportService;
    @Autowired
    private ExerciseMenu exerciseMenu;

    @Override
    public void show() {
        int choice;
        do {
            printOptions();
            choice = InputHandler.getIntegerInput();
            switch (choice) {
                case 1 -> topExercises();
                case 2 -> exerciseProgression();
            }
        }
        while (choice != 0);
    }

    public void exerciseProgression() {
        Exercise exercise = exerciseMenu.listExercisesAndSelect();
        System.out.println("Enter the start date (yyyy-MM-dd): ");
        LocalDateTime startDate = LocalDate.parse(InputHandler.getStringInput()).atStartOfDay();
        System.out.println("Enter the end date (yyyy-MM-dd): ");
        LocalDateTime endDate = LocalDate.parse(InputHandler.getStringInput()).atStartOfDay();

        reportService.exerciseProgression(exercise, startDate, endDate);
    }

    public void topExercises() {
        reportService.topExercises();
    }

    @Override
    public void printOptions() {
        System.out.println();
        System.out.println("[1] Top exercises");
        System.out.println("[2] Exercise progression");
        System.out.println("[3] Volume over time");
        System.out.println("[0] Back");
        System.out.println("Please make a selection:");
    }
}
