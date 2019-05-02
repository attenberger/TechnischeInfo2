package src;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageToBitConverter {


    public static void main(String... args) {

        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("src/test.png"));

            byte[][] pixels = new byte[image.getWidth()][];

            for (int x = 0; x < image.getWidth(); x++) {
                pixels[x] = new byte[image.getHeight()];

                for (int y = 0; y < image.getHeight(); y++) {
                    pixels[x][y] = (byte) (image.getRGB(x, y) == 0xFFFFFFFF ? 1 : 0);
                }
            }

            for(int i = 0; i < pixels.length; i++) {
                for(int j = 0; j < pixels[i].length; j++) {
                    System.out.println(pixels[i][j]);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}


