package techInf2.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    private static final int PORT = 5555;

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {
                try {
                    System.out.println("starting server...");
                    Socket socket = serverSocket.accept();
                    System.out.println("server accepted new connection");
                    new EchoSocket(socket).start();
                } catch (Exception e) {
                    e.printStackTrace();
                    serverSocket.close();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
