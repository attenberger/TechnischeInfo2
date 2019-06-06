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

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
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
            sendErrorData(timetableCreator, e.getMessage());
        } catch (Exception e) {
            sendErrorData(timetableCreator, e.getMessage());
        }
    }

    private void sendErrorData(TimetableCreator timetableCreator, String errorMsg) {
        byte[] bytes =  timetableCreator.generateErrorImageByteArray(errorMsg);
        try (DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
            dataOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
