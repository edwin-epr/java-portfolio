package org.mathedwin.softdev.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mathedwin.softdev.exceptions.DatabaseException;
import org.mathedwin.softdev.model.User;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
}
