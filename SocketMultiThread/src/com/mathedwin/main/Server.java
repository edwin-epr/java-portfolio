package com.mathedwin.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final int PORT = 12345;

    private static final String SERVER_NAME = "Math-Edwin-Developer";

    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    private ServerSocket serverSocket;

    private ExecutorService threadPool;

    public void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            threadPool = Executors.newCachedThreadPool();
            LOGGER.info("Se ha inicializado el servidor en el puerto: " + PORT);

            while (!serverSocket.isClosed()) {
                Socket socketClient = serverSocket.accept();
                LOGGER.info("Se ha conectado un cliente a: " + socketClient.getInetAddress());
                threadPool.submit(new ClientHandler(socketClient, SERVER_NAME));
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al iniciar el servidor: ", e);
        }
    }
}
