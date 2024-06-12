package com.mathedwin.main.service;

import com.mathedwin.main.model.User;

import java.util.Optional;
import java.util.logging.Logger;

public class LoginService {
    private static final Logger LOG = Logger.getLogger(MessageService.class.getName());
    private final UserService userService;

    public LoginService() {
        userService = new UserService();
    }

    public Boolean checkUser(String username, String password) {
        Optional<User> userToGetAccess = userService.getUserByUsername(username);
        if (userToGetAccess.isPresent()) {
            String registeredUser = userToGetAccess.get().getUsername();
            String registeredPassword = userToGetAccess.get().getPassword();
            return registeredUser.equals(username) && registeredPassword.equals(password);
        }
        return false;
    }
}
