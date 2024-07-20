package org.mathedwin.softdev.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mathedwin.softdev.model.Message;
import org.mathedwin.softdev.model.User;
import org.mathedwin.softdev.service.LoginService;
import org.mathedwin.softdev.service.RegistrationService;
import org.mathedwin.softdev.service.UserService;

import java.io.*;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.mathedwin.softdev.server.PromptServer.*;

public class ClientHandler implements Runnable {

    public static final Logger LOGGER = LogManager.getLogger(ClientHandler.class);
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private Boolean isUserRegistered = false;
    //    private static final String CLEAR_CHARACTER = "\033[H\033[2J";
    private final LoginService login;
    private final RegistrationService registrationService;

    private static final String EXIT_WORD = "exit";

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.login = new LoginService();
        this.registrationService = new RegistrationService();
    }

    @Override
    public void run() {
        try {

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // TODO: Mover metodos de instancia publicos de LoginService y RegistrationService a LoginUtils y RegistrationUtils y hacerlos estaticos

            welcome(out, in);
            int optionScreen;
            do {
                optionScreen = startScreen(out, in);
                // Login
                if (optionScreen == 1) {
                    username = login.enterUsername(out, in);
                    String password = login.enterPassword(out, in);
                    isUserRegistered = login.checkUser(username, password);

                    if (isUserRegistered) {
                        LOGGER.info("Has entered {} to the server.", username);
                        Optional<User> user = new UserService().getUserByUsername(username);

                        String connectionMessage = String.format("[SERVER]: %s has logged on to the server.", username);
                        ChatServer.broadcastMessage(connectionMessage, this);
                        ChatServer.broadcastToUser(this);

                        String message;

                        while ((message = in.readLine()) != null) {
                            if (message.equalsIgnoreCase(EXIT_WORD)) {
                                break;
                            }
                            if (!message.isEmpty()) {
                                LOGGER.info("Entrando si no es vacio");
                                message = String.format("[%s]: %s", username, message);
                                ChatServer.broadcastMessage(message, this);
                                Message messageToBeSaved = getMessage(message, user.orElseThrow());
                                ChatServer.saveMessage(messageToBeSaved);
                            }
                        }
                    } else {
                        LOGGER.info("Incorrect username or password.");
                        out.println("Incorrect username or password.");
                        out.print("Press ENTER to continue...");
                        out.flush();
                        in.readLine();
                    }
                }
                // Registration
                if (optionScreen == 2) {
                    User user = new User();
                    boolean isSuccessfulRegistration;
                    user.setUsername(registrationService.enterUsername(out, in));
                    out.print("Enter your password: ");
                    out.flush();
                    user.setPassword(in.readLine());
                    clearScreen(out);
                    out.print("Enter your email: ");
                    out.flush();
                    user.setEmail(in.readLine());
                    clearScreen(out);
                    isSuccessfulRegistration = registrationService.register(user);

                    if (isSuccessfulRegistration) {
                        LOGGER.info("User successfully registered!");
                    } else {
                        LOGGER.info("User name or email already registered.");
                        out.println("User name or email already registered.");
                        out.print("Press ENTER to continue...");
                        out.flush();
                        in.readLine();
                    }
                }
            } while (optionScreen != 3);


        } catch (IOException | InterruptedException | ExecutionException exception) {
            LOGGER.error("Error: ", exception);
            throw new RuntimeException("Error: ", exception);
        } finally {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                LOGGER.error("Error closing an IO component: ", e);
            }

            ChatServer.removeClient(this);
            if (isUserRegistered) {
                LOGGER.info("{} has been disconnected.", username);
                ChatServer.broadcastMessage(username + " has been disconnected from the chat.", this);
            } else {
                LOGGER.info("Client connection refused.");
            }
        }
    }

    private Message getMessage(String message, User user) {
        final int userId = user.getId();
        Message messageToBeSaved = new Message();
        messageToBeSaved.setContent(message);
        messageToBeSaved.setUserId(userId);
        return messageToBeSaved;
    }

    public void sendMessage(String message) {
        out.println(message);
        out.flush();
    }

}
