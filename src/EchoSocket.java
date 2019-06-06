package src;

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EchoSocket extends Thread {

    private Socket socket;

    public EchoSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        TimetableCreator timetableCreator = new TimetableCreator();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String room = reader.readLine();
            System.out.println(room + "\n");
            DateFormat dateformat = new SimpleDateFormat("dd.MM.yyyy");
            String date = dateformat.format(new Date());

            ZPAParser parser = new ZPAParser(date, room);
            List<Slot> slots = parser.getSlots();

            byte[] bytes = timetableCreator.generateImageByteArray(room, date, slots);

            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.write(bytes);

            socket.close();
        } catch (IOException e) {
            timetableCreator.generateErrorImageByteArray("Creating connection to ZPA failed!");
        } catch (Exception e) {
            timetableCreator.generateErrorImageByteArray(e.getMessage());
        }
    }
}
