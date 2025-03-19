package org.example;

public class Authentication {

    private IUserRepository userRepository;

    public Authentication(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean authenticate(String login, String password) {
        User user = userRepository.getUser(login);

        if (user != null && user.getPassword().equals(password)) {
            return true;
        }
        return false;
    }
}
