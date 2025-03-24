package org.example;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Authentication {

    private IUserRepository userRepository;
    private static Path userFilePath = Paths.get("Accounts.csv");

    public Authentication(IUserRepository userRepository, Path userFilePath) {
        this.userRepository = userRepository;
        this.userFilePath = userFilePath;
    }
    public static User login(String login, String password) {
        User user = User.findUser(userFilePath,login);

        if (user != null && user.getPassword().equals(DigestUtils.sha256Hex(password))) {
            return user;
        }
        return null;
    }
}
