package src;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class ZPAParser {

    private final String ZPA_URL = "https://w3-o.cs.hm.edu:8000/public/bookings/";

    private Map<String, String> rooms;

    private String date;
    private String room;

    private String csrfToken;
    private String csrfMiddleWareToken;

    private List<Slot> slots = new ArrayList<>();

    private ZPAParser(String date, String room) {
        this.room = room;
        this.date = date;
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

    private void parseTimeTable(String html) {
        //replace <br> for getting a string with new lines after each body value
        final String newLine = "§§NEWLINE§§";
        html = html.replaceAll("<br>", newLine);
        Document doc = Jsoup.parse(html);
        Elements day = doc.select("td.day[id=" + date + "]");
        Elements slotElements = day.select("div.slot");

        Slot slot;
        for (Element slotElement : slotElements) {
            slot = new Slot(slotElement.select("div.slot_header").text(), slotElement.select("div.slot_inner")
                    .text().replaceAll(newLine, "\n"));
            slots.add(slot);
        }
        slots.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getTime().substring(0, 2))));
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Slot slot : slots) {
            builder.append(slot);
        }
        return builder.toString();
    }

    private class Slot {

        private String time;
        private String body;

        Slot(String time, String body) {
            this.time = time;

            String[] lines = body.split("\n");
            StringBuilder builder = new StringBuilder();
            for (String line : lines) {
                builder.append(line.trim()).append("\n");
            }
            this.body = builder.toString();
        }

        @Override
        public String toString() {
            return time + "\n" + body + "\n\n";
        }

        String getTime() {
            return time;
        }
    }


    public static void main(String[] args) {
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        String date = format.format(new Date());

        ZPAParser parser = new ZPAParser(date, "R1.006");

        String html;

        try {
            html = parser.getTokens();

            parser.initializeRooms(html);

            html = parser.getTimeTable();

            parser.parseTimeTable(html);

            System.out.println(parser);
        } catch (Exception e) {
            System.out.println("Failed reading html!");
            e.printStackTrace();
        }
    }
}