import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Objects;

public class SocketClients {

    private static final int PORT = 23;

    private static final String HOSTNAME = "towel.blinkenlights.nl";

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
