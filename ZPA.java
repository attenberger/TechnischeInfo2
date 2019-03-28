import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


public class ZPA {

	public static void main(String[] args) throws IOException {
		String url = "https://w3-o.cs.hm.edu:8000/public/bookings/";
		
		URL obj = new URL(url);
		HttpURLConnection con;
		int responseCode;
		Map<String, List<String>> header;
		BufferedReader in;
		StringBuffer response;
		String inputLine;
		String csrfmiddlewaretoken = "";
		
		
		
		con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		responseCode = con.getResponseCode();
		System.out.println(responseCode);

		header = con.getHeaderFields();
		List<String> cookies = header.get("Set-Cookie");
		String csrftoken = cookies.get(0).split(";")[0];
		System.out.println(csrftoken);
		for (Map.Entry<String, List<String>> e : header.entrySet()) {
			//System.out.println(e.getKey() + "  " + e.getValue().toString() + "\n");
		}
		
		
		in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			if (inputLine.contains("csrfmiddlewaretoken")) {
				csrfmiddlewaretoken = inputLine.split("value='")[1].split("'")[0];
			}
			response.append(inputLine);
			response.append("\n");
		}
		in.close();
		
		System.out.println(csrfmiddlewaretoken);
		
		//print result
		//System.out.println(response.toString());
		
		
		
		//-----------------------------------------
		
		
		con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
		con.setRequestProperty("Cookie", csrftoken);
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.write(("csrfmiddlewaretoken=" + csrfmiddlewaretoken + "&room=2&date=28.03.2019").getBytes(StandardCharsets.UTF_8));
		
		
		responseCode = con.getResponseCode();
		System.out.println(responseCode);

		header = con.getHeaderFields();
		for (Map.Entry<String, List<String>> e : header.entrySet()) {
			System.out.println(e.getKey() + "  " + e.getValue().toString() + "\n");
		}
		
		
		in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
			response.append("\n");
		}
		in.close();
		System.out.println(response.toString());
	}

}
