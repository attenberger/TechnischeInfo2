package src;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class EchoSocket extends Thread {

    private Socket socket;
    private byte[] pixel;

    public EchoSocket(Socket socket, byte[] pixel) {
        this.socket = socket;
        this.pixel = pixel;
    }

    @Override
    public void run() {
        try {
            socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.write(pixel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
