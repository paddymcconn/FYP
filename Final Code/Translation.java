import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
// google api imports used.

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;
// java imports used.

import javax.net.ssl.HttpsURLConnection;
// connection import.

public class Translation {
	private String key;

	public Translation(String apiKey) {
		key = apiKey;
	}

	String translate(String text, String from, String to) {
		StringBuilder result = new StringBuilder();
		try {
			String encodedText = URLEncoder.encode(text, "UTF-8");

			String urlStr = "https://www.googleapis.com/language/translate/v2?key=" + key + "&q=" + encodedText + "&target=" + to + "&source=" + from;
			// api request with the appropriate info stored in strings for ease of repeated use.
			
			URL url = new URL(urlStr);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			// open connection
			
			InputStream stream;
			// taking in all the bits as a stream
			
			if (conn.getResponseCode() == 200) // success
			{
				stream = conn.getInputStream();
			} else
				stream = conn.getErrorStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}

			JsonParser parser = new JsonParser();
			// create a parser

			JsonElement element = parser.parse(result.toString());
			// back to string

			if (element.isJsonObject()) {
				JsonObject obj = element.getAsJsonObject();
				if (obj.get("error") == null) {
					String translatedText = obj.get("data").getAsJsonObject().get("translations").getAsJsonArray()
							.get(0).getAsJsonObject().get("translatedText").getAsString();
					return translatedText;
				}
			}

			if (conn.getResponseCode() != 200) {
				System.err.println(result);
			}

		} catch (IOException | JsonSyntaxException ex) {
			System.err.println(((Throwable) ex).getMessage());
		}
		return null;
	}

	public static String readFileAsString(String filePath) throws IOException {
		StringBuffer fileData = new StringBuffer();
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
		}
		reader.close();
		return fileData.toString();
	}

	public static void main(String[] args) throws IOException {
		String filepath = ("C://Users/u180384/Dropbox/Final Year/Final Year Project/test_xml/test.txt");
		String keeper = readFileAsString(filepath);
		Scanner Sc = new Scanner(System.in);
		String input = Sc.next();
		String output = Sc.next();
		Sc.close();
		Translation translator = new Translation("TheAPIKEY-DeletedForDepersonalisation");
		// passed in the api key for authentication
		String text = translator.translate(keeper, input, output);
	
		try {
		    BufferedWriter out = new BufferedWriter(new FileWriter("C://Users/u180384/Dropbox/Final Year/Final Year Project/test_xml/test_translated.txt"));
		    out.write(text);
			out.close();
		}
		catch (IOException e)
		{
		    System.out.println("Exception ");
		}
		System.out.println(text);
	}
}
