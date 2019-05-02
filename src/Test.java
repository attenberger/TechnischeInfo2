package src;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

public class Test {

    private static final int WIDTH = 384;
    private static final int HEIGHT = 640;

    public static void main(String[] args) {

        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), 4568);

            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            writer.write("R1.006\r\n");
            writer.flush();

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            byte[] bytes = dataInputStream.readAllBytes();

            byte[] bits = new byte[bytes.length * 8];

            for (int i = 0; i < bytes.length; i++) {
                System.out.println(Integer.toBinaryString(bytes[i]));
                for (int j = 0; j < 8; j++) {
                    /*if (Boolean.parseBoolean(String.valueOf(bitChars[j]))) {
                        bits[i*8+j] = (byte) 0xFF;
                    }*/
                }
            }

            byte[][] pixel = new byte[640][384];

            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

            /*for (int i = 0; i < HEIGHT; i++) {
                for (int j = 0; j < WIDTH; j++) {
                    pixel[i][j] = bits[i*WIDTH+j];
                }
            }*/

            for (int i = 0; i < HEIGHT; i++) {
                for (int j = 0; j < WIDTH; j++) {
                    image.setRGB(i, j, bits[i*WIDTH+j]);
                }
            }

            ImageIO.write(image, "png", new File("generated.png"));

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
