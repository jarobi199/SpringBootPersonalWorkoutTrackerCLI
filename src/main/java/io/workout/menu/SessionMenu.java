package io.workout.menu;

import io.workout.interfaces.IMenu;
import io.workout.util.InputHandler;
import org.springframework.stereotype.Component;

@Component
public class SessionMenu implements IMenu {

    @Override
    public void show() {
        int choice;
        do {
            printOptions();
            choice = InputHandler.getIntegerInput();
            switch (choice) {

            }
        }
        while (choice != 0);
    }

    @Override
    public void printOptions() {
        System.out.println();
        System.out.println("[1] Start new session");
        System.out.println("[2] View recent sessions");
        System.out.println("[3] View session detail");
        System.out.println("[4] Delete session");
        System.out.println("[0] Back");
        System.out.println("Please make a selection:");
    }
}

