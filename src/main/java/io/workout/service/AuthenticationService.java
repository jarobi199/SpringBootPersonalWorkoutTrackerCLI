package io.workout.service;

import io.workout.authentication.PasswordEncryptor;
import io.workout.authentication.SessionContext;
import io.workout.enums.Role;
import io.workout.model.User;
import io.workout.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    public boolean authenticate(String username, String passwordCandidate) {
        boolean authenticated = false;
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if ((optionalUser.isPresent()) && PasswordEncryptor.authenticate(passwordCandidate, optionalUser.get().getPassword())) {
            User user = optionalUser.get();
            authenticated = true;
            SessionContext.login(user);
        }

        return authenticated;
    }

    public void initializeUser(String name, String username, String password, Role role, int bodyWeightKg) {
        User user = new User(name, username, PasswordEncryptor.encrypt(password), role, bodyWeightKg);
        userRepository.save(user);
    }

}
