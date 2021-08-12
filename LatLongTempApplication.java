package net.newjazz.LatLongTemp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.*;
import java.util.*;
import java.net.*;

@RestController
@SpringBootApplication
public class LatLongTempApplication {

	@RequestMapping(value = "/latLongTemp")

	public String latLongTemp(@RequestParam(defaultValue="60.0") float lat,
	@RequestParam(defaultValue="0.0") float lon) {
		Float newLat, newLon;

		if (lat > 90 || lat < -90) {
			return "Latitude error (" + lat + ")";
		}

		if (lon > 180 || lon < -180) {
			return "Longitude error (" + lon + ")";
		}

		URLConnection myConnection = null;
		try {
			String urlString = "https://api.openweathermap.org/data/2.5/onecall?lat=" + lat +"&lon=" + lon +"&exclude=hourly&appid=F8bbd2c4154b67e214403406996bbf44";
			//          System.out.println("[  " + urlString);
			URL url = new URL(urlString);
			myConnection = url.openConnection();
		} catch
		(MalformedURLException exc) {
			return "" + exc;
		}
		catch
		(IOException exc) {
			return "" + exc;
		}
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(myConnection.getInputStream()));
		} catch (IOException exc) {
			return "" + exc;
		}
		String line = "";
		String token = "";
		List<Entry> entryList = new ArrayList<Entry>();
		Entry entry;
		int x=0;

		try {
			line = in.readLine();
		}
		catch (Exception exc) {
			return "" + exc;
		}
		//       System.out.println("printLine[" + line + "]");

		for (int count = 0; count < 8; count++) {

			entry = new Entry();
			entryList.add(entry);
			token = "";
			entry.day = count;

			int offset = 0;
			try {

				offset = line.indexOf("\"max\":" , x);
				x = offset + 6;

				while (line.charAt(x) != ',') {
					token += line.charAt(x);
					x++;
				}

				entry.max = Float.parseFloat(token);



				token = "";

				int offsetHumid = line.indexOf("\"humidity\":", x);
				x = offsetHumid + 11;

				while (line.charAt(x) == ' ') {
					x++;
				}

				while (line.charAt(x) != ',') {
					token += line.charAt(x);
					x++;
				}

				entry.humid = Float.parseFloat(token);



			}catch (Exception exc) {

				return "" + exc;
			}

		} // end for (int count; count < 8; count++)

		Entry winningEntry = null;
		Float highTemp = 0.0f;
		Float lowHumid = 0.0f;

		// check for high temparature
		ListIterator<Entry> li = entryList.listIterator();
		while (li.hasNext()) {
			entry = (Entry)li.next();
			if (highTemp < entry.max) {
				winningEntry = entry;
				highTemp = entry.max;
			}
		}

		// check for low humidity with high temp
		li = entryList.listIterator();
		while (li.hasNext()) {
			entry = (Entry)li.next();
			if (highTemp == entry.max) {
				if (lowHumid < entry.humid) {
					lowHumid = entry.humid;
				}
			}
		}

		// check for winning entry based on highTemp and low humidity
		li = entryList.listIterator();
		while (li.hasNext()) {
			entry = (Entry)li.next();
			if ((highTemp == entry.max) && (lowHumid == entry.humid)) {
				winningEntry = entry;
				break;
			}
		}

		String statement = "Day " + winningEntry.day + "  Temperature " + winningEntry.max + " Humidity " + winningEntry.humid;

		return statement;
	}

	public static void main(String[] args) {
		SpringApplication.run(LatLongTempApplication.class, args);
	}

}
