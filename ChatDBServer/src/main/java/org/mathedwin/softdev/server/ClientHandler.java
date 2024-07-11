package org.mathedwin.softdev.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mathedwin.softdev.model.Message;
import org.mathedwin.softdev.model.User;
import org.mathedwin.softdev.service.LoginService;
import org.mathedwin.softdev.service.RegistrationService;
import org.mathedwin.softdev.service.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class ClientHandler implements Runnable {

    public static final Logger LOGGER = LogManager.getLogger(ClientHandler.class);
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private String password;
    private Boolean check = false;
    private Integer optionScreen;
    private static final String CLEAR_CHARACTER = "\033[H\033[2J";
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

            welcome();
            //TODO: tal vez aquí podríamos hacer el login/registro
            while (true) {
                optionScreen = startScreen();
                if (optionScreen == 1) {
                    out.print("Escribe tu nombre de usuario: ");
                    out.flush();
                    username = in.readLine();
                    clearScreen();
                    out.print("Escribe tu password: ");
                    out.flush();
                    password = in.readLine();
                    clearScreen();
                    check = login.checkUser(username, password);

                    if (check) {
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
//                            message = String.format("[%s]: " + message, username);
                            message = String.format("[%s]: %s", username, message);
                            ChatServer.broadcastMessage(message, this);
                            Message messageToBeSaved = getMessage(message, user.orElseThrow());
                            ChatServer.saveMessage(messageToBeSaved);
                        }
                    } else {
                        LOGGER.info("Incorrect username or password.");
                        out.println("Incorrect username or password.");
                        out.print("Presione ENTER para continuar...");
                        out.flush();
                        in.readLine();
                    }
                }
                if (optionScreen == 2) {
                    User user = new User();
                    boolean flag;
                    out.print("Escribe tu nombre de usuario: ");
                    out.flush();
                    user.setUsername(in.readLine());
                    clearScreen();
                    out.print("Escribe tu password: ");
                    out.flush();
                    user.setPassword(in.readLine());
                    clearScreen();
                    out.print("Escribe tu email: ");
                    out.flush();
                    user.setEmail(in.readLine());
                    clearScreen();
                    flag = registrationService.register(user);
                    // TODO: Cachar validateUserException

                    if (flag) {
                        LOGGER.info("User successfully registered!");
                    } else {
                        LOGGER.info("User name or email already registered.");
                        out.println("User name or email already registered.");
                        out.print("Presione ENTER para continuar...");
                        out.flush();
                        in.readLine();
                    }
                }
                if (optionScreen == 3) {
                    break;
                }
            }


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
            if (check) {
                LOGGER.info("{} has been disconnected.", username);
                ChatServer.broadcastMessage(username + " has been disconnected from the chat.", this);
            } else {
                LOGGER.info("Client connection refused.");
            }
        }
    }

    private Message getMessage(String message, User user) {
        final int userId = user.getId(); //TODO: temporal, remover y ajustar, de momento todos los mensajes serán del user con id 1 -> user1
        Message messageToBeSaved = new Message();
        messageToBeSaved.setContent(message);
        messageToBeSaved.setUserId(userId); //TODO: recordar que no hay correspondencia entre el userId y algún usuario real en la bd, esto sólo es para probar el guardado de mensajes.
        return messageToBeSaved;
    }

    private void welcome() throws IOException {
        clearScreen();
        out.println(ServerConstants.MYBANNER);
        out.print("¡Bienvnid@!, recuerda ser amigable y respetuoso!, presiona ENTER para continuar...");
        out.flush();
        in.readLine();
        clearScreen();
    }

    private void clearScreen() {
        out.print(CLEAR_CHARACTER);
        out.flush();
    }

    public void sendMessage(String message) {
        out.println(message);
        out.flush();
    }

    private int startScreen () throws IOException {
        int option;
        while (true) {
            clearScreen();
            out.println("1) Iniciar Sesión");
            out.println("2) Registrar Usuario");
            out.println("3) Salir del servidor");
            out.print("Seleccione una opción: ");
            out.flush();
            try {
                option = Integer.parseInt(in.readLine());
            } catch (RuntimeException e) {
                option = 0;
            }
            clearScreen();
            if (option == 1 || option == 2 || option == 3) {
                return option;
            } else {
                out.println("Opción incorrecta, vuelva a escoger");
                out.print("Presione ENTER para continuar...");
                out.flush();
                in.readLine();
                clearScreen();
            }
        }
    }
}
