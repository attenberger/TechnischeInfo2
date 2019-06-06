package src;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class TimetableCreator {
	
	private static final int WIDTH = 384;
	private static final int WIDTH_HOURSCALE = 80;
	private static final int HEIGHT = 640;
	private static final int HOUR_HEIGHT = 45;
	private static final int FIRST_HOUR = 8;
	private static final int PADDING = 5;
	private static final int FONTSIZE = 20;
	private static final int MAXLINESPERSLOT = 3;
	private static final int MAXLINELENGTH = 40;

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

		byte[] reversed = bits.toByteArray();
		for(int i = 0; i < reversed.length; i++) {
			reversed[i] = reverse(reversed[i]);
		}
		return reversed;

	}

	//reverses bits in a byte, eg. 10000000 --> 00000001
	public byte reverse(byte x) {
		int size = 8;
		byte y = 0;
		for(int position = size - 1; position >= 0; position--) {
			y += ((x&1)<<position);
			x >>= 1;
		}
		return y;
	}

	public void generateErrorImageByteArray(String msg) {

		BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_BINARY);
		Graphics2D graphics2d = image.createGraphics();

		// set background white
		graphics2d.setPaint(new Color(255,255,255));
		graphics2d.fillRect(0, 0, image.getWidth(), image.getHeight());

		Font font = new Font("Arial", Font.BOLD, FONTSIZE);
		graphics2d.setFont(font);
		FontMetrics fontmetrics = graphics2d.getFontMetrics();
		graphics2d.setColor(Color.BLACK);

		int y = 50;
		graphics2d.drawString("Ein Fehler ist aufgetreten.", 50, y);
		y += 30;
		graphics2d.drawString("Anzeige zur Zeit nicht möglich.", 50, y);

		y += 50;

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
	}
	
	public byte[] generateImageByteArray(String room, String date, List<Slot> slots){
		
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D graphics2d = image.createGraphics();
        
        // set background white
        graphics2d.setPaint(new Color(255,255,255));
        graphics2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        
        Font font = new Font("Arial", Font.BOLD, FONTSIZE);
        graphics2d.setFont(font);
        FontMetrics fontmetrics = graphics2d.getFontMetrics();
        graphics2d.setColor(Color.BLACK);
        
        printTextCentered(graphics2d, date, fontmetrics.getAscent());
        printTextCentered(graphics2d, room, (int)(fontmetrics.getAscent() * 2.5));
        graphics2d.drawLine(0, (int)(fontmetrics.getAscent() * 3), WIDTH, (int)(fontmetrics.getAscent() * 3));

        font = new Font("Arial", Font.PLAIN, 15);
        graphics2d.setFont(font);
        printHourScale(graphics2d, fontmetrics.getAscent() * 5, WIDTH_HOURSCALE);
        printSlots(graphics2d, slots, fontmetrics.getAscent() * 5, WIDTH_HOURSCALE, WIDTH - WIDTH_HOURSCALE);
        graphics2d.dispose();

        return picToBytes(image);
	}
	
	private void printSlots(Graphics2D graphics2d, List<Slot> slots, int pos_Y, int pos_X, int width) {
		for (Slot slot : slots) {
			int top = (int)(pos_X + ((slot.getStartTime().getHour() - FIRST_HOUR) + slot.getStartTime().getMinute() / 60.0) * HOUR_HEIGHT);
			int bottom = (int)(pos_X + ((slot.getEndTime().getHour() - FIRST_HOUR) + slot.getEndTime().getMinute() / 60.0) * HOUR_HEIGHT);
			int height = bottom - top;
			int left = pos_X;
			graphics2d.drawRect(left, top, width - 1, height);

	        FontMetrics fontmetrics = graphics2d.getFontMetrics();
			printText(graphics2d, slot.getBody(), top + fontmetrics.getAscent() + PADDING, left + PADDING, width - 2 * PADDING, false);
		}
	}
	
	
	private void printHourScale(Graphics2D graphics2d, int pos_Y, int width) {
		for (int hour = 8; hour < 21; hour++) {
			graphics2d.drawLine(0, pos_Y + (hour - FIRST_HOUR) * HOUR_HEIGHT - FONTSIZE / 2 - PADDING, width - 5, pos_Y + (hour - FIRST_HOUR) * HOUR_HEIGHT - FONTSIZE / 2 - PADDING);
			printText(graphics2d, hour + ":00", pos_Y + (hour - FIRST_HOUR) * HOUR_HEIGHT, 0, width, false);
		}
	}
	
	private void printTextLeftAligned(Graphics2D graphics2d, String text, int pos_Y) {
		printText(graphics2d, text, pos_Y, 0, WIDTH, false);
	}
	
	private void printTextCentered(Graphics2D graphics2d, String text, int pos_Y) {
		printText(graphics2d, text, pos_Y, 0, WIDTH, true);
	}
	
	private void printText(Graphics2D graphics2d, String text, int pos_Y, int offset_X, int width, boolean centered) {
        FontMetrics fontmetrics = graphics2d.getFontMetrics();
        
        if (centered) {
        	offset_X += (width - fontmetrics.stringWidth(text)) / 2;
        }
        String[] lines = text.split("\n");
        List<String> timetableLines = new ArrayList<>();
        for (int line = 0; line < lines.length; line++) {
        	timetableLines.addAll(splitLongLine(lines[line]));
        }
        for (int line = 0; line < MAXLINESPERSLOT && line < timetableLines.size(); line++) {
            graphics2d.drawString(timetableLines.get(line), offset_X, pos_Y + line * FONTSIZE);
        }
	}
	
	private List<String> splitLongLine(String text) {
		String[] words = text.split(" ");
		List<String> lines = new ArrayList<>();
		lines.add("");
		int currentLine = 0;
		int lengthCurrentLine = 0;
		int indexCurrentWord = 0;
		while (indexCurrentWord < words.length) {
			if (lengthCurrentLine + words[indexCurrentWord].length() > MAXLINELENGTH && lengthCurrentLine != 0) {
				currentLine++;
				if (currentLine >= MAXLINESPERSLOT) {
					break;
				}
				lengthCurrentLine = 0;
				lines.add("");
			}
			lines.set(currentLine, lines.get(currentLine) + words[indexCurrentWord] + " ");
			lengthCurrentLine += words[indexCurrentWord].length() + 1;
			indexCurrentWord++;
		}
		return lines;
	}

}
