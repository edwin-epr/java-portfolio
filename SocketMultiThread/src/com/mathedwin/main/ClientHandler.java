package com.mathedwin.main;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());

    private final Socket socketClient;

    private final String serverName;

    public ClientHandler(Socket socketClient, String serverName) {
        this.socketClient = socketClient;
        this.serverName = serverName;
    }

    @Override
    public void run() {
        try(PrintWriter output = new PrintWriter(new OutputStreamWriter(socketClient.getOutputStream()), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(socketClient.getInputStream()))
        ) {
            ServerUtils.clearConsole(output);
            welcome(output, input);
            ServerUtils.countDown(output);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error manejando el cliente: ", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            LOGGER.info("Se ha desconectado el cliente desde: " + socketClient.getInetAddress());
        }
    }

    private void welcome(PrintWriter output, BufferedReader input) throws IOException {
        output.printf("Hola, bienvenido al servidor de: %s%n", serverName);
        output.println("Por razones de mantenimiento serás desconectado en diez segundos...");
        output.println("Presiona ENTER para continuar...");
        input.readLine();
    }
}
