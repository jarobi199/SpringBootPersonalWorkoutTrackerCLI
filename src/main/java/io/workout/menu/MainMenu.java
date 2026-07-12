package io.workout.menu;

import io.workout.interfaces.IMenu;
import io.workout.util.InputHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MainMenu implements IMenu {

    @Autowired
    private AuthenticateMenu authenticateMenu;
    @Autowired
    private ExerciseMenu exerciseMenu;
    @Autowired
    private SettingsMenu settingsMenu;
    @Autowired
    private SessionMenu sessionMenu;
    @Autowired
    private GoodbyeMenu goodbyeMenu;

    public void show() {
        int choice = 0;
        IMenu menu;

        System.out.println();
        displayTitle();
        authenticateMenu.automaticLogin();
        System.out.println();

        do {
            printOptions();
            choice = InputHandler.getIntegerInput();
            menu = switch (choice) {
                case 1 -> exerciseMenu;
                case 2 -> sessionMenu;
                case 5 -> settingsMenu;
                case 0 -> goodbyeMenu;
               default -> throw new IllegalStateException("Unexpected value: " + choice);
            };
            menu.show();
        }
        while (choice != 0);

        InputHandler.closeInput();
    }

    public void printOptions() {
        System.out.println("[1] Exercises");
        System.out.println("[2] Sessions");
        System.out.println("[3] Progress");
        System.out.println("[4] Reports");
        System.out.println("[5] Settings");
        System.out.println("[0] Exit");
        System.out.println("Please make a selection:");
    }

    public void displayTitle() {
        System.out.println("===========================================================");
        System.out.println("    Welcome to the Personal Workout Tracker Application!");
        System.out.println("============================================================");
    }

}
