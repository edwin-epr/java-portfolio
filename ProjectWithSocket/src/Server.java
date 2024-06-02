import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Server {

    public static final int PORT = 12345;

    public static final String SERVER_NAME = "mathEdwin-developer";

    public static final Logger LOG = Logger.getLogger(Server.class.getName());

    public static void main(String[] args) {
        new Server().startServer();
    }

    private ServerSocket serverSocket;

    public void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            LOG.info("Se ha inicializado el server en el puerto: " + PORT);

            while (!serverSocket.isClosed()) {
                try(Socket socketClient = serverSocket.accept();
                    BufferedReader input = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
                    PrintWriter output = new PrintWriter(new OutputStreamWriter(socketClient.getOutputStream()),true)
                ) {
                    LOG.info("Un cliente se ha conectado a: " + socketClient.getInetAddress());
                    clearConsole(output);
                    welcome(output, input);
                    countDown(output);
                    LOG.info("Se ha desconectado " + socketClient.getInetAddress());
                }
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void countDown(PrintWriter output) throws InterruptedException {
        for (int i = 10; i > 0; i--) {
            clearConsole(output);
            output.println("Cuenta regresiva: " + i + " segundos restantes...");
            TimeUnit.SECONDS.sleep(1);
        }
        output.println("¡Hasta luego!");
    }

    private void welcome(PrintWriter output, BufferedReader input) throws IOException {
        output.println("¡Hola!, bienvenido al servidor de: " + SERVER_NAME);
        output.println("Por razones de mantenimiento serás expulsado del servidor en diez segundos...");
        output.println("Presiona ENTER para continuar.");
    }

    private void clearConsole(PrintWriter output) {
        output.println("\033[H\033[2J");
    }
}
