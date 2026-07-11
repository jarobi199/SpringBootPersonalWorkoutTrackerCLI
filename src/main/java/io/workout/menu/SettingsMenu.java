package io.workout.menu;

import io.workout.authentication.SessionContext;
import io.workout.enums.Role;
import io.workout.interfaces.IMenu;
import io.workout.service.UserService;
import io.workout.util.InputHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SettingsMenu implements IMenu {

    @Autowired
    private UserService userService;

    @Override
    public void show() {
        int choice;
        do {
            printOptions();
            choice = InputHandler.getIntegerInput();
            switch (choice) {
                case 1 -> updateBodyWeight();
                case 2 -> updateWeeklySessionGoal();
                case 3 -> changePassword();
                case 4 -> addUser();
                case 5 -> deleteUser();
            }
        }
        while (choice != 0);
    }

    public void deleteUser() {
        if(verifyAdmin()) {
            System.out.println("Enter the username of the user that you want to delete:");
            String username = InputHandler.getStringInput();
            System.out.println("Are you sure you want to delete this user? (Y/N):");
            String answer = InputHandler.getStringInput();
            if("Y".equalsIgnoreCase(answer)) {
                try {
                    userService.deleteUser(username);
                    System.out.println("User " + username + " has been deleted!");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addUser() {
        if(verifyAdmin()) {
            System.out.println("Enter your full name:");
            String fullName = InputHandler.getStringInput();
            System.out.println("Enter your username:");
            String username = InputHandler.getStringInput();
            System.out.println("Enter your password:");
            String password = InputHandler.getStringInput();
            System.out.println("Enter your role (ADMINISTRATOR, USER):");
            Role role = Role.valueOf(InputHandler.getStringInput().toUpperCase());
            System.out .println("Enter the value of your body weight in kilograms:");
            int bodyWeightKg  = InputHandler.getIntegerInput();

            try {
                userService.addUser(fullName, username, password, role, bodyWeightKg);
                System.out.println("User " +  username + " has been successfully created!");
            }
            catch (RuntimeException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void updateWeeklySessionGoal() {
        System.out .println("Enter the value of the new weekly session goal:");
        int sessionGoal = InputHandler.getIntegerInput();
        userService.updateWeeklySessionGoal(sessionGoal);
        System.out.println("The weekly session goal has been set to " + sessionGoal + ".");
    }

    public void updateBodyWeight() {
        System.out .println("Enter the new body weight value (Kg):");
        int bodyWeight = InputHandler.getIntegerInput();
        userService.updateBodyWeight(bodyWeight);
        System.out.println("Body weight (Kg) has been set to " + bodyWeight + ".");
    }

    public void changePassword() {
        System.out .println("Enter the new password:");
        String newPassword = InputHandler.getStringInput();
        userService.changePassword(newPassword);
    }

    public void printOptions() {
        System.out.println();
        System.out.println("[1] Update body weight");
        System.out.println("[2] Set weekly session goal");
        System.out.println("[3] Change password");
        if(verifyAdmin()) {
            System.out.println("[4] Add user");
            System.out.println("[5] Delete user");
        }
        System.out.println("[0] Exit");
        System.out.println("Please make a selection:");
    }

    private boolean verifyAdmin() {
        return Role.ADMINISTRATOR.equals(SessionContext.getUser().getRole());
    }
}
