package org.example;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.file.Path;
import java.util.List;

public class Authentication {

    private IUserRepository userRepository;

    public Authentication(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Metoda logowania
    public static boolean login(String login, String password, Path userFilePath) {
        User user = User.findUser(userFilePath, login);

        if (user != null && user.getPassword().equals(DigestUtils.sha256Hex(password))) {
            return true;
        }
        return false;
    }
}
