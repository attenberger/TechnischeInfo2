package src;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.*;

public class ZPAParser {

    private final String ZPA_URL = "https://w3-o.cs.hm.edu:8000/public/bookings/";

    private Map<String, String> rooms;

    private String date;
    private String room;

    private String csrfToken;
    private String csrfMiddleWareToken;

    public ZPAParser(String date, String room) {
        this.room = room;
        this.date = date;
    }

    public List<Slot> getSlots() {

        String html;

        try {
            html = getTokens();

            initializeRooms(html);

            html = getTimeTable();

            return parseTimeTable(html);

        } catch (Exception e) {
            System.out.println("Failed reading html!");
            return null;
        }
    }

    private String getTokens() throws Exception {
        URL url = new URL(ZPA_URL);
        //sending get request to zpa server
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();

        if (responseCode != 200) {
            throw new IOException("Bad response code!");
        }

        //get csrf token from response header
        Map<String, List<String>> header = con.getHeaderFields();
        List<String> cookies = header.get("Set-Cookie");
        csrfToken = cookies.get(0).split(";")[0];

        String html = readHTML(con);
        Document document = Jsoup.parse(html);
        csrfMiddleWareToken = document.select("input[name=csrfmiddlewaretoken]").get(0).attr("value");

        return html;
    }

    private String getTimeTable() throws Exception {
        URL url = new URL(ZPA_URL);
        //send post request with received cookies, token and room and date to check room occupancy
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("Cookie", csrfToken);
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.write(("csrfmiddlewaretoken=" + csrfMiddleWareToken + "&room=" + rooms.get(room) + "&date=" + date).getBytes(StandardCharsets.UTF_8));

        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Bad response code");
        }
        return readHTML(con);
    }

    private List<Slot> parseTimeTable(String html) {
        List<Slot> slots = new ArrayList<>();

        //replace <br> for getting a string with new lines after each body value
        final String newLine = "§§NEWLINE§§";
        html = html.replaceAll("<br>", newLine);
        Document doc = Jsoup.parse(html);
        Elements day = doc.select("td.day[id=" + date + "]");
        Elements slotElements = day.select("div.slot");

        Slot slot;
        for (Element slotElement : slotElements) {
            String startTime = slotElement.select("div.slot_header").text().split(" ")[0];
            String endTime = slotElement.select("div.slot_header").text().split(" ")[2];
            slot = new Slot(LocalTime.parse(startTime), LocalTime.parse(endTime), slotElement.select("div.slot_inner")
                    .text().replaceAll(newLine, "\n"));
            slots.add(slot);
        }
        slots.sort((a, b) -> a.getStartTime().compareTo(b.getEndTime()));

        return slots;
    }

    private void initializeRooms(String html) {
        Document d = Jsoup.parse(html);

        Elements roomElements = d.select("select[name=room] option");

        rooms = new HashMap<>();
        for (Element roomElement : roomElements) {
            rooms.put(roomElement.text(), roomElement.attr("value"));
        }
    }

    private String readHTML(HttpURLConnection con) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            response.append("\n");
        }
        in.close();
        return response.toString();
    }

    /*@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Slot slot : slots) {
            builder.append(slot);
        }
        return builder.toString();
    }*/
}
