package io.workout.authentication;

import io.workout.model.User;
import org.springframework.stereotype.Component;

@Component
public class SessionContext {

    private static User currentUser;

    public static void login(User user) {
        currentUser = user;
    }

    public static User getUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }
}
