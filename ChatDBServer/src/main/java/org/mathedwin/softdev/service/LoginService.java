package org.mathedwin.softdev.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mathedwin.softdev.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import static org.mathedwin.softdev.server.PromptServer.clearScreen;
import static org.mathedwin.softdev.utils.UserUtils.validatePasswordForLogin;
import static org.mathedwin.softdev.utils.UserUtils.validateUsername;

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

    public String enterUsername(PrintWriter out, BufferedReader in) throws IOException {
        String username;
        while (true) {
            out.print("Enter your username: ");
            out.flush();
            username = in.readLine();
            try {
                validateUsername(username);
            } catch (IllegalArgumentException exception) {
                LOGGER.info(exception.getMessage());
                out.println(exception.getMessage());
                out.print("Press ENTER to continue...");
                out.flush();
                in.readLine();
                clearScreen(out);
                continue;
            }
            clearScreen(out);
            break;
        }
        return username;
    }

    public String enterPassword(PrintWriter out, BufferedReader in) throws IOException {
        String password;
        while (true) {
            out.print("Enter your password: ");
            out.flush();
            password = in.readLine();
            try {
                validatePasswordForLogin(password);
            } catch (IllegalArgumentException exception) {
                LOGGER.info(exception.getMessage());
                out.println(exception.getMessage());
                out.print("Press ENTER to continue...");
                out.flush();
                in.readLine();
                clearScreen(out);
                continue;
            }
            clearScreen(out);
            break;
        }
        return password;
    }
}
