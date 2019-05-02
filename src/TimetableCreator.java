package src;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

public class TimetableCreator {
	
	private static final int WIDTH = 384;
	private static final int WIDTH_HOURSCALE = 80;
	private static final int HEIGHT = 640;
	private static final int HOUR_HEIGHT = 45;
	private static final int FIRST_HOUR = 8;
	private static final int PADDING = 5;
	private static final int FONTSIZE = 20;
	private static final int MAXLINESPERSLOT = 3;

	public static void main(String[] args) {
		
		List<Slot> slots = new ArrayList<>();
		slots.add(new Slot(LocalTime.of(8, 15), LocalTime.of(9, 45), "Vorlesung 1\nsdfsfdsfsfsdf\ndsfdsfsdf"));
		slots.add(new Slot(LocalTime.of(10, 00), LocalTime.of(11, 30), "Vorlesung 1"));
		slots.add(new Slot(LocalTime.of(11, 45), LocalTime.of(13, 15), "Vorlesung 1"));
		slots.add(new Slot(LocalTime.of(13, 30), LocalTime.of(15, 00), "Vorlesung 1"));
		slots.add(new Slot(LocalTime.of(15, 15), LocalTime.of(16, 45), "Vorlesung 1"));
		slots.add(new Slot(LocalTime.of(17, 00), LocalTime.of(18, 30), "Vorlesung 1"));
		slots.add(new Slot(LocalTime.of(18, 45), LocalTime.of(20, 15), "Vorlesung 1"));

		TimetableCreator textToImage = new TimetableCreator();
		
		BufferedImage image = textToImage.generateImage("Raum: 2.007", new Date(), slots);
		try {
            ImageIO.write(image, "png", new File("H:/Sample4.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

	}
	
	public BufferedImage generateImage(String room, Date date, List<Slot> slots) {
		
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D graphics2d = image.createGraphics();
        
        // set background white
        graphics2d.setPaint(new Color(255,255,255));
        graphics2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        
        Font font = new Font("Arial", Font.BOLD, FONTSIZE);
        graphics2d.setFont(font);
        FontMetrics fontmetrics = graphics2d.getFontMetrics();
        graphics2d.setColor(Color.BLACK);
        
        DateFormat dateformat = new SimpleDateFormat("dd.MM.yyyy");
        printTextCentered(graphics2d, dateformat.format(date), fontmetrics.getAscent() * 1);
        printTextCentered(graphics2d, room, (int)(fontmetrics.getAscent() * 2.5));
        graphics2d.drawLine(0, (int)(fontmetrics.getAscent() * 3), WIDTH, (int)(fontmetrics.getAscent() * 3));
        

        font = new Font("Arial", Font.PLAIN, 15);
        graphics2d.setFont(font);
        printHourScale(graphics2d, fontmetrics.getAscent() * 5, WIDTH_HOURSCALE);
        printSlots(graphics2d, slots, fontmetrics.getAscent() * 5, WIDTH_HOURSCALE, WIDTH - WIDTH_HOURSCALE);        
        graphics2d.dispose();
        return image;		
	}
	
	private void printSlots(Graphics2D graphics2d, List<Slot> slots, int pos_Y, int pos_X, int width) {
		for (Slot slot : slots) {
			System.out.println();
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
        String[] lines = text.split("\n", MAXLINESPERSLOT);
        for (int line = 0; line < lines.length; line++) {
            graphics2d.drawString(lines[line], offset_X, pos_Y + line * FONTSIZE);
        }
	}

}
