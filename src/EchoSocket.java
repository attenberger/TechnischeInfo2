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
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //String room = reader.readLine();
            String room = "R2.007";
            System.out.println(room + "\n");
            DateFormat dateformat = new SimpleDateFormat("dd.MM.yyyy");
            String date = dateformat.format(new Date());

            ZPAParser parser = new ZPAParser(date, room);
            List<Slot> slots = parser.getSlots();

            TimetableCreator timetableCreator = new TimetableCreator();
            byte[] bytes = timetableCreator.generateImageByteArray(room, date, slots);

            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.write(bytes);

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
