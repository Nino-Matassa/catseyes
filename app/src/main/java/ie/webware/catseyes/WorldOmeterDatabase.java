package ie.webware.catseyes;
import android.os.*;
import java.io.*;
import java.net.*;
import org.json.*;
import java.nio.charset.*;

class WorldOmeterDatabase {
		public WorldOmeterDatabase() {}

		public Void populateLocalDatabase() {
				BufferedReader bufferedReader = null;
				try {
						URL url = new URL(Constants.worldOmeterURL);
						HttpURLConnection httpUrlConnection = (HttpURLConnection)url.openConnection();
						InputStream inputStream =  httpUrlConnection.getInputStream();
						bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
						
						StringBuilder sb = new StringBuilder();
						String line = null;
						
						while((line = bufferedReader.readLine()) != null) {
							sb.append(line);
							// Read into database...
						}
						
						bufferedReader.close();
						
					} catch (Exception e) {
						String err = e.toString();
					}

				return null;
			}
	}

