package org.mathedwin.softdev.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mathedwin.softdev.model.User;

import java.util.Optional;

public class LoginService {

    public static final Logger LOGGER = LogManager.getLogger(LoginService.class);
    private final UserService userService;

    public LoginService() {
        userService = new UserService();
    }

    public Boolean checkUser(String username, String password) {
        Optional<User> userToGetAccess = userService.getUserByUsername(username);
        if (userToGetAccess.isPresent()) {
            LOGGER.debug("checking if the user '{}' is registered", username);
            String registeredUser = userToGetAccess.get().getUsername();
            String registeredPassword = userToGetAccess.get().getPassword();
            return registeredUser.equals(username) && registeredPassword.equals(password);
        }
        return false;
    }
}
