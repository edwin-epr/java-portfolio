import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Objects;

public class SocketClient {

    private static final int PORT = 12345;

    private static final String HOSTNAME = "localhost";

    public static void main(String[] args) {
        new SocketClient().watch();
    }

    public void watch() {
        try(Socket socket = new Socket(HOSTNAME, PORT)) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line;

            while (Objects.nonNull(line = reader.readLine())) {
                System.out.println(line);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
