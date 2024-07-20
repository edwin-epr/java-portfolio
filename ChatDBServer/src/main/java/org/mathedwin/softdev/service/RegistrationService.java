package org.mathedwin.softdev.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mathedwin.softdev.exceptions.DatabaseException;
import org.mathedwin.softdev.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mathedwin.softdev.server.PromptServer.clearScreen;
import static org.mathedwin.softdev.utils.UserUtils.validateUsername;

public class RegistrationService {

    public static final Logger LOGGER = LogManager.getLogger(RegistrationService.class);
    private final UserService userService;

    public RegistrationService() {
        this.userService = new UserService();
    }

    public boolean register(User user) throws ExecutionException, InterruptedException {

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        try {
            userService.saveUser(user);
            LOGGER.info("A user has registered: {}.", user);
            future.complete(true);
        } catch (DatabaseException exception) {
            String messageError = String.format("Error registering user: %s.", user);
            LOGGER.error(messageError, exception);
            future.complete(false);
        }

        return future.get();
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
}
