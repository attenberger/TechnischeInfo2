package techInf2.server;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Creates a byte-array for the image of an timetable.
 * @author Robert Attenberger
 */
public class TimetableCreator {

    private static final int WIDTH = 384; // Number of pixel horizontal of the e-paper display
    private static final int WIDTH_HOURSCALE = 80; // width of the hourscale in pixels
    private static final int HEIGHT = 640; // Number of pixel vertiacal of the e-paper display
    private static final int HOUR_HEIGHT = 45; // hight of one hour in pixel on the display
    private static final int FIRST_HOUR = 8; // number of the first hour of the timetable displayed on the e-paper
    private static final int LAST_HOUR = 21; // number of the last hour (exklusive) of the timetable displayed on the e-paper
    private static final int PADDING = 5; // offset of the text in the slots in pixels
    private static final int FONTSIZE = 20; // fontsize of the text on the e-paper display
    private static final int MAXLINESPERSLOT = 3; // max lines of text in one slot
    private static final int MAXLINELENGTH = 40; // max number of characters in one line of a slotbox

    /**
     * Creates a byte array out of an black and white image.
     * Each pixel of the image is converted into one bit in the array (while = 1; black = 0)
     * The pixels are read column per column.
     * @param image to create the byte array from
     * @return byte array which encode the black and white image
     */
    private byte[] picToBytes(BufferedImage image) {

        byte[][] pixels = new byte[image.getWidth()][];

        //read black / white values from image and save into byte array
        for (int x = 0; x < image.getWidth(); x++) {
            pixels[WIDTH-x-1] = new byte[image.getHeight()];

            for (int y = 0; y < image.getHeight(); y++) {
                pixels[WIDTH-x-1][y] = (byte) (image.getRGB(x, y) == 0xFFFFFFFF ? 1 : 0);
            }
        }

        //byte array to bit set
        BitSet bits = new BitSet( image.getWidth() * image.getHeight());

        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                if (pixels[i][j] == 1) {
                    bits.set(i * pixels[i].length + j);
                }
            }
        }

        // inverse the bit-order in each byte
        byte[] reversed = bits.toByteArray();
        for(int i = 0; i < reversed.length; i++) {
            reversed[i] = reverse(reversed[i]);
        }
        return reversed;

    }

    /**
     * Reverses the bits in one byte.
     * Eg. 10000000 --> 00000001
     * @param x byte to reverse
     * @return reversed byte
     */
    public byte reverse(byte x) {
        int size = 8;
        byte y = 0;
        for(int position = size - 1; position >= 0; position--) {
            y += ((x&1)<<position);
            x >>= 1;
        }
        return y;
    }

    /**
     * Generates a byte-Array which represents a an error-image.
     * @param msg of the error image
     * @return generate byte-array
     */
    public byte[] generateErrorImageByteArray(String msg) {

        // Create the image
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D graphics2d = image.createGraphics();

        // set background white
        graphics2d.setPaint(new Color(255,255,255));
        graphics2d.fillRect(0, 0, image.getWidth(), image.getHeight());

        Font font = new Font("Arial", Font.BOLD, FONTSIZE);
        graphics2d.setFont(font);
        FontMetrics fontmetrics = graphics2d.getFontMetrics();
        graphics2d.setColor(Color.BLACK);

        // print common error text to image
        int y = 50;
        graphics2d.drawString("Ein Fehler ist aufgetreten.", 50, y);
        y += 30;
        graphics2d.drawString("Anzeige zur Zeit nicht möglich.", 50, y);

        y += 50;

        // print msg of the parameter
        font = new Font("Arial", Font.PLAIN, FONTSIZE);
        graphics2d.setFont(font);
        List<String> strings = new ArrayList<String>();
        int index = 0;
        while (index < msg.length()) {
            strings.add(msg.substring(index, Math.min(index + 30,msg.length())));
            index += 30;
        }
        for(String s : strings) {
            y += 30;
            graphics2d.drawString(s, 50, y);
        }

        graphics2d.dispose();

        // convert image to byte-array and return
        return picToBytes(image);
    }

    /**
     * Generates a byte-array which represents a timetable according to the given parameters.
     * @param room of the timetable (only printed on the top of the image, not further relevant)
     * @param date of the timetable (only printed on the top of the image, not further relevant)
     * @param slots to be printed on the timetable
     * @return byte-array which represents the timetable
     */
    public byte[] generateImageByteArray(String room, String date, List<Slot> slots){

        // Create the image
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D graphics2d = image.createGraphics();

        // set background white
        graphics2d.setPaint(new Color(255,255,255));
        graphics2d.fillRect(0, 0, image.getWidth(), image.getHeight());

        Font font = new Font("Arial", Font.BOLD, FONTSIZE);
        graphics2d.setFont(font);
        FontMetrics fontmetrics = graphics2d.getFontMetrics();
        graphics2d.setColor(Color.BLACK);

        // print room and date on top of the timetable
        printTextCentered(graphics2d, date, fontmetrics.getAscent());
        printTextCentered(graphics2d, room, (int)(fontmetrics.getAscent() * 2.5));
        graphics2d.drawLine(0, (int)(fontmetrics.getAscent() * 3), WIDTH, (int)(fontmetrics.getAscent() * 3));

        font = new Font("Arial", Font.PLAIN, 15);
        graphics2d.setFont(font);
        printHourScale(graphics2d, fontmetrics.getAscent() * 5, WIDTH_HOURSCALE); // print hourscale on the left side of the timetable
        printSlots(graphics2d, slots, fontmetrics.getAscent() * 5, WIDTH_HOURSCALE, WIDTH - WIDTH_HOURSCALE); // print the hourscale itself
        graphics2d.dispose();

        // convert image to byte-array and return
        return picToBytes(image);
    }

    /**
     * Prints all the slots to the image which is passed as parameter.
     * @param image to print the slots on
     * @param slots to print
     * @param pos_Y distance of the timetable to the left edge of the image (e-paper display) in pixels
     * @param pos_X distance of the timetable to the top edge of the image (e-paper display) in pixels
     * @param width in pixels of the slots on the image
     */
    private void printSlots(Graphics2D image, List<Slot> slots, int pos_Y, int pos_X, int width) {
        for (Slot slot : slots) {
            int top = (int)(pos_X + ((slot.getStartTime().getHour() - FIRST_HOUR) + slot.getStartTime().getMinute() / 60.0) * HOUR_HEIGHT);
            int bottom = (int)(pos_X + ((slot.getEndTime().getHour() - FIRST_HOUR) + slot.getEndTime().getMinute() / 60.0) * HOUR_HEIGHT);
            int height = bottom - top;
            int left = pos_X;
            image.drawRect(left, top, width - 1, height); // print a box for the slot

            // print the text into the box for the slot
            FontMetrics fontmetrics = image.getFontMetrics();
            printText(image, slot.getBody(), top + fontmetrics.getAscent() + PADDING, left + PADDING, width - 2 * PADDING, false);
        }
    }

    /**
     * Prints the hourscale on the image passed in the parameters.
     * @param image to print the hour scale on
     * @param pos_Y vertical offset of the hourscale from the top edge of the image (e-paper display) in pixels
     * @param width of the hourscale in pixels
     */
    private void printHourScale(Graphics2D image, int pos_Y, int width) {
        for (int hour = FIRST_HOUR; hour < LAST_HOUR; hour++) {
            image.drawLine(0, pos_Y + (hour - FIRST_HOUR) * HOUR_HEIGHT - FONTSIZE / 2 - PADDING, width - 5, pos_Y + (hour - FIRST_HOUR) * HOUR_HEIGHT - FONTSIZE / 2 - PADDING);
            printText(image, hour + ":00", pos_Y + (hour - FIRST_HOUR) * HOUR_HEIGHT, 0, width, false);
        }
    }

    /**
     * Prints a text centered to full width of the image (e-paper display) to the image
     * @param image to print the text on
     * @param text to print
     * @param pos_Y offset of the text to the top edge of the image
     */
    private void printTextCentered(Graphics2D image, String text, int pos_Y) {
        printText(image, text, pos_Y, 0, WIDTH, true);
    }

    /**
     * Prints a text at the given position to the image.
     * @param image to print the text on
     * @param text to print
     * @param pos_Y vertical offset of the text from the top edge of the image (e-paper display)
     * @param offset_X horizontal offset of the text from the left edge of the image (e-paper display)
     * @param width of the area to print the text on. Only relevant if the text is printed centered.
     * @param centered is set to true if the text should printed centered. Otherwise it is printed left aligned.
     */
    private void printText(Graphics2D image, String text, int pos_Y, int offset_X, int width, boolean centered) {
        FontMetrics fontmetrics = image.getFontMetrics();

        // modify print position if the text should be printed centered
        if (centered) {
            offset_X += (width - fontmetrics.stringWidth(text)) / 2;
        }
        // split the text on "\n" because it is ignored by the print methods for the image,
        // also long lines are wrapped
        String[] lines = text.split("\n");
        List<String> timetableLines = new ArrayList<>();
        for (int line = 0; line < lines.length; line++) {
            timetableLines.addAll(splitLongLine(lines[line]));
        }
        // print the first three lines of the text in the slot box
        for (int line = 0; line < MAXLINESPERSLOT && line < timetableLines.size(); line++) {
            image.drawString(timetableLines.get(line), offset_X, pos_Y + line * FONTSIZE);
        }
    }

    /**
     * Wraps a long line into more lines.
     * @param text to wrap
     * @return List of the wraped lines.
     */
    private List<String> splitLongLine(String text) {
        String[] words = text.split(" ");
        List<String> lines = new ArrayList<>();
        lines.add("");
        int currentLine = 0;
        int lengthCurrentLine = 0;
        int indexCurrentWord = 0;
        while (indexCurrentWord < words.length) { // until all lines are proceeded
            if (lengthCurrentLine + words[indexCurrentWord].length() > MAXLINELENGTH && lengthCurrentLine != 0) { // wrap if the next word would exceed the limit of characters per line
                currentLine++;
                if (currentLine >= MAXLINESPERSLOT) { // cancel if the max number of lines per slot is reached
                    break;
                }
                lengthCurrentLine = 0;
                lines.add("");
            }
            lines.set(currentLine, lines.get(currentLine) + words[indexCurrentWord] + " "); // append the next word to the current line
            lengthCurrentLine += words[indexCurrentWord].length() + 1; // update the line-length-counter for the current line
            indexCurrentWord++; // update the number of already proceeded lines
        }
        return lines;
    }

}
