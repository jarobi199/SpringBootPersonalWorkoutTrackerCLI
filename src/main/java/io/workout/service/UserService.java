package io.workout.service;

import io.workout.authentication.PasswordEncryptor;
import io.workout.authentication.SessionContext;
import io.workout.enums.Role;
import io.workout.exceptions.UnauthorizedException;
import io.workout.model.User;
import io.workout.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void changePassword(String newPassword) {
        String encodedPassword = PasswordEncryptor.encrypt(newPassword);
        SessionContext.getUser().setPassword(encodedPassword);
        userRepository.save(SessionContext.getUser());
        System.out.println("Your password has been changed!");
    }

    public void addUser(String fullName, String username, String password, Role role, int bodyWeightKg) {
        if(Role.ADMINISTRATOR.equals(SessionContext.getUser().getRole())) {
            User user = new User(fullName, username, PasswordEncryptor.encrypt(password), role, bodyWeightKg);
            userRepository.save(user);
        }
        else
        {
            throw new UnauthorizedException("Only administrators are allowed to add a new user!");
        }
    }

    public void deleteUser(String username) {
        if(Role.ADMINISTRATOR.equals(SessionContext.getUser().getRole())) {
            Optional<User> toDelete = userRepository.findByUsername(username);
            toDelete.ifPresent(user -> userRepository.delete(user));
        }
        else
        {
            throw new UnauthorizedException("Only administrators are allowed to delete a user!");
        }
    }

    public void updateBodyWeight(int bodyWeight) {
        SessionContext.getUser().setBodyWeightKg(bodyWeight);
        userRepository.save(SessionContext.getUser());
    }

    public void updateWeeklySessionGoal(int weeklySessionGoal) {
        SessionContext.getUser().setWeeklySessionGoal(weeklySessionGoal);
        userRepository.save(SessionContext.getUser());
    }
}
