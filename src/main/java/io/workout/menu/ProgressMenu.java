package io.workout.menu;

import io.workout.interfaces.IMenu;
import io.workout.service.ReportService;
import io.workout.util.InputHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProgressMenu implements IMenu {

    @Autowired
    private ReportService reportService;

    @Override
    public void show() {
        int choice;
        do {
            printOptions();
            choice = InputHandler.getIntegerInput();
            switch (choice) {
                case 1 -> topExercises();
            }
        }
        while (choice != 0);
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
