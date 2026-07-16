package io.workout.menu;

import io.workout.interfaces.IMenu;
import io.workout.service.ReportService;
import io.workout.util.InputHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReportsMenu implements IMenu {

    @Autowired
    private ReportService reportService;

    @Override
    public void show() {
        int choice;
        do {
            printOptions();
            choice = InputHandler.getIntegerInput();
            switch (choice) {
                case 1 -> reportService.weeklySummary();
                case 2 -> reportService.monthlySummary();
                case 3 -> reportService.muscleGroupFrequency();
            }
        }
        while (choice != 0);
    }

    @Override
    public void printOptions() {
        System.out.println();
        System.out.println("[1] Weekly summary");
        System.out.println("[2] Monthly summary");
        System.out.println("[3] Muscle group frequency");
        System.out.println("[0] Back");
        System.out.println("Please make a selection:");
    }
}

