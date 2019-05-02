package src;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    private static final int PORT = 8090;

    public static void main(String[] args) {

        while (true) {
            try {
                ServerSocket serverSocket = new ServerSocket(PORT);

                Socket socket = serverSocket.accept();
                new EchoSocket(socket).start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
