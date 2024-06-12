package com.mathedwin.main.server;

import com.mathedwin.main.model.Message;
import com.mathedwin.main.model.User;
import com.mathedwin.main.service.LoginService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

    private static final Logger LOG = Logger.getLogger(ClientHandler.class.getName());
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private String password;
    private Boolean check;
    private static final String CLEAR_CHARACTER = "\033[H\033[2J";
    private final LoginService login;

    private static final String EXIT_WORD = "exit";

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.login = new LoginService();
    }

    @Override
    public void run() {
        try {

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            welcome();
            //TODO: tal vez aquí podríamos hacer el login/registro
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
                LOG.info("Usurario logeado");


                LOG.info("Ha ingresado " + username + " al servidor");
                //TODO: aquí deberíamos hacer un broadcast del historial de chat en caso de haberlo.
                //TODO: ese concern le corresponde al server, ergo, el server debería tener un método tipo "broadcastToUser"
                //TODO: dicho método recibe "this" y usa el método sendMessage para imprimir al usuario los últimos X mensajes.

                String connectionMessage = String.format("[SERVER]: %s ha ingresado al servidor", username);
                ChatServer.broadcastMessage(connectionMessage, this);

                String message;

                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase(EXIT_WORD)) {
                        break;
                    }
//                message = String.format("[%s]: " + message, username);
                    message = String.format("[%s]: %s", username, message);
                    ChatServer.broadcastMessage(message, this);
                    //TODO: tal vez es buen momento para almacenar en la base de datos el mensaje.
                    //TODO: esto es temporal (la creación de usuario), deberíamos obtener eso de BD al momento del login o registro
                    // TODO: También cuidado con el user_id, la función que he generado es dummy
                    Message messageToBeSaved = getMessage(message);
                    ChatServer.saveMessage(messageToBeSaved);
                }
            } else {
                LOG.info("Nombre de usuario o contraseña incorrectos");
                out.println("Nombre de usuario o contraseña incorrectos");
                out.print("Presione ENTER para continuar...");
                out.flush();
                in.readLine();
            }


        } catch (IOException e) {
            //TODO: mejorar manejo excepciones, custom + logs
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                //TODO: mejorar esto custom + logs
                e.printStackTrace();
            }

            ChatServer.removeClient(this);
            if (check) {
                LOG.info(username + " se ha desconectado ");
                ChatServer.broadcastMessage(username + " se ha desconectado del chat", this);
            } else {
                LOG.info("Conexión de cliente rechazada");
            }
        }
    }

    private Message getMessage(String message) {
        final int dummyUserId = 1; //TODO: temporal, remover y ajustar, de momento todos los mensajes serán del user con id 1 -> user1
        Message messageToBeSaved = new Message();
        messageToBeSaved.setContent(message);
        messageToBeSaved.setUserId(dummyUserId); //TODO: recordar que no hay correspondencia entre el userId y algún usuario real en la bd, esto sólo es para probar el guardado de mensajes.
        return messageToBeSaved;
    }


    private void welcome() throws IOException {
        clearScreen();
        out.println(ServerConstants.BANNER);
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
}
