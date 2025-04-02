package com.umcsuser.carrent.services;


import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> login(String login, String password) {
        Optional<User> userOpt = userRepository.findByLogin(login);
        if (userOpt.isPresent() && BCrypt.checkpw(password, userOpt.get().getPasswordHash())) {
            return userOpt;
        }
        return Optional.empty();
    }

    public User register(String login, String password, String role) {
        if (userRepository.findByLogin(login).isPresent()) {
            throw new IllegalArgumentException("User with this login already exists");
        }

        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = User.builder()
                .login(login)
                .passwordHash(passwordHash)
                .role(role)
                .build();
        return userRepository.save(user);
    }
}