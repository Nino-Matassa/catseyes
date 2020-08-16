package ie.webware.catseyes;
import android.os.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import org.json.*;

class InternetAccess {
		public InternetAccess() {}

		public Void populateDatabase() {
				String url = "https://covid.ourworldindata.org/data/owid-covid-data.json";
				try {
						readWorldometerJson(url);	
					} catch (Exception e) {
						
					}

				return null;
			}

		public JSONObject readWorldometerJson(String url) throws Exception {
				InputStream is = new URL(url).openStream(); // not working!
				JSONObject json = null;

				try {
						BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
						String jsonText = readURL(reader);
						json = new JSONObject(jsonText);
						return json;
					} catch (Exception e) {
						
					} finally {
						is.close();
						return json;
					}		
			}

		private String readURL(BufferedReader reader) {
				StringBuilder sb = new StringBuilder();
				try {
						while (reader.readLine() != "") {
								sb.append(reader.toString());
							}
					} catch (Exception e) {}

				return sb.toString();
			}
	}
