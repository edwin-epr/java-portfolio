package org.mathedwin.softdev.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mathedwin.softdev.exceptions.DatabaseException;
import org.mathedwin.softdev.model.Message;
import org.mathedwin.softdev.service.MessageService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    public static final Logger LOGGER = LogManager.getLogger(ChatServer.class);

    private static final int PORT = 12345;
    private static final Set<ClientHandler> clients = Collections.synchronizedSet(new HashSet<>());
    private static final MessageService messageService = new MessageService();
    private static final int MAX_MESSAGE_POOL = 10;
    private static final ExecutorService messageExecutor = Executors.newFixedThreadPool(MAX_MESSAGE_POOL);

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            LOGGER.info("The server has been started on the port: " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                LOGGER.info("The connection to a client has been accepted.");
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                LOGGER.info("A customer has been added to the customer list: {}.", clients);
                messageExecutor.submit(clientHandler);
                LOGGER.info("A new thread has been started for a customer.");
            }

        } catch (IOException exception) {
            LOGGER.error("Error when starting the server.", exception);
            throw new RuntimeException(exception);
        }
    }

    public void stopServer() {
        try {
            messageExecutor.shutdown();
            LOGGER.info("The server has stopped...");
        } catch (Exception e) {
            LOGGER.error("Error when stopping the server.", e);
        }
    }

    public static void broadcastMessage(String message, ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        }
    }

    public static void broadcastToUser(ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client == sender) {
                    List<Message> lastMessages = messageService.getLastMessages();
                    lastMessages.forEach(message -> client.sendMessage(message.getContent()));
                }
            }
        }
    }

    public static void saveMessage(Message message) {
        messageExecutor.submit(() -> {
            try {
                messageService.saveMessage(message);
            } catch (DatabaseException exception) {
                String messageError = String.format("Error saving message: %s", message);
                LOGGER.error(messageError, exception);
            } catch (Exception e) {
                LOGGER.error("An error occurred saving the message.", e);
            }
        });
    }

    public static void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }
}
