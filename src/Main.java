package src;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    //TODO Exception Handling in allen Klassen

    private static final int PORT = 5555;

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("server accepted new connection");
                    new EchoSocket(socket).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
