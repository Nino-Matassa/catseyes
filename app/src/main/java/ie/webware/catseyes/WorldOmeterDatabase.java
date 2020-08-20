package ie.webware.catseyes;
import android.os.*;
import java.io.*;
import java.net.*;
import org.json.*;
import java.nio.charset.*;
import android.widget.*;
import java.util.*;

class WorldOmeterDatabase {
	
		public WorldOmeterDatabase() {}

			public Void populateLocalDatabase() {
				BufferedReader bufferedReader = null;
				try {
						URL url = new URL(Constants.worldOmeterURL);
						HttpURLConnection httpUrlConnection = (HttpURLConnection)url.openConnection();
						InputStream inputStream =  httpUrlConnection.getInputStream();
						bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
						
						ArrayList<String> jsonFragment = new ArrayList<String>();
						String line = null;
						String value = null;
						
						while((line = bufferedReader.readLine()) != null) {
							jsonFragment.add(line);
							// Populate database...
							value = line.replaceAll(Constants.jsonExpression," ");
						}
						bufferedReader.close();
						
					} catch (Exception e) {
						String err = e.toString();
					}
				return null;
			}
			
			private class TableKeyValue {
				public String table;
				public String key;
				public String value;
			}
		
			private TableKeyValue getTableKeyValue(String jsonFragment){
				TableKeyValue keyValue = new TableKeyValue();
				String value = jsonFragment.replaceAll("[^a-zA-Z0-9.]"," ");
					String[] lines =value.split(" ");
					
//					String str = "Hello";
//					switch(str) {
//							case "Hello":
//								// do something
//								break;
//					}
				
				keyValue.value = value;
				return keyValue;
			}
	}

	
