package techInf2.server.test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Test {

    private static final int WIDTH = 384;
    private static final int HEIGHT = 640;

    public static void main(String[] args) {

        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), 5555);

            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            writer.write("R1.008\r\n");
            writer.flush();

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            byte[] bytes = new byte[HEIGHT*WIDTH / 8];
            dataInputStream.read(bytes);

            byte[] bits = new byte[bytes.length * 8];

            for (int i = 0; i < bytes.length; i++) {
                char[] bitChars = String.format("%8s", Integer.toBinaryString(bytes[i] & 0xFF)).replace(' ', '0').toCharArray();
                /*char[] bitCharsx = new char[bitChars.length];
                for(int p = 0; p < bitChars.length; p++) {
                    bitCharsx[p] = bitChars[bitChars.length - 1 - p];
                }*/
                for (int j = 0; j < 8; j++) {
                    if (bitChars[j] == '1') {
                        bits[i*8+j] = (byte) 0xFF;
                    }
                }
            }

            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

            for (int i = 0; i < WIDTH; i++) {
                for (int j = 0; j < HEIGHT; j++) {
                    image.setRGB(i, j, bits[i*HEIGHT+j]);
                }
            }

            ImageIO.write(image, "png", new File("testgenerated.png"));

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
