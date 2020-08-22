package ie.webware.catseyes;
import android.os.*;
import java.io.*;
import java.net.*;
import org.json.*;
import java.nio.charset.*;
import android.widget.*;
import java.util.*;
import org.apache.commons.codec.binary.*;

class WorldOmeterDatabase {

		public WorldOmeterDatabase() {}
		ArrayList<String> jsonFragment = new ArrayList<String>();
		//ArrayList<TableKeyValue> tableKeyValue = new ArrayList<TableKeyValue>();
		public Void populateLocalDatabase() {
				BufferedReader bufferedReader = null;
				try {
						URL url = new URL(Constants.worldOmeterURL);
						HttpURLConnection httpUrlConnection = (HttpURLConnection)url.openConnection();
						InputStream inputStream =  httpUrlConnection.getInputStream();
						bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

						String line = null;
						int fragmentIndex = 0;
						while ((line = bufferedReader.readLine()) != null) {
								jsonFragment.add(line.replaceAll(Constants.jsonWood, " "));
								fragmentIndex++;
								if (fragmentIndex == 1000) break; // test line
							}
						bufferedReader.close();
					} catch (Exception e) {
						String err = e.toString();
					}
				// Speed read json
				int level = 0;// 1 = Region, 2 = Country, 3 = Data
				for (int i = 0; i < jsonFragment.size(); i++) {
						String line = jsonFragment.get(i);
						if (line.contains("{") || line.contains("}") || line.contains("},") || line.contains("[") || line.contains("]")) {
								if (line.contains("{")) {
										level++;	
										continue;
									} else if (line.contains("}")) {
										level--;
										continue;
									} else if (line.contains("[") || line.contains("]"))
									continue;
							}
						String table = null;
						String key = null;
						String value = null;
						if (level == 1 && line.contains("{")) {
								table = Constants.tblRegion;
								line = line.replaceAll(Constants.jsonTree, "");
								key = Constants.colCountry;
								value = line;
								level++;
							} else if (level == 2) {
								table = Constants.tblCountry;
								String[] keyValue = line.split(" ");
								String values = fixStringArray(keyValue);
								keyValue = values.split("[+]");
								key = keyValue[0].toString();
								value = keyValue[1].toString();
							} else if (level == 3) {
								table = Constants.tblData;
								String[] keyValue = line.split(" ");
								String values = fixStringArray(keyValue);
								keyValue = values.split("[+]");
								key = keyValue[0].toString();
								value = keyValue[1].toString();
							}
						TableKeyValue tkv = new TableKeyValue();
						tkv.table = table;
						tkv.key = key;
						tkv.value = value;
						if (key.equals("date")) {
								tkv.isDate = true;
								tkv.isNumeric = false;
							} else {
								try {
									Double d = Double.parseDouble(value);
									tkv.isNumeric = true;
									} catch (Exception e) {
										tkv.isNumeric = false;
									} finally {
										tkv.isDate = false;
									}
							}

						MainActivity.tableKeyValue.add(tkv);
					}
				jsonFragment.clear();
				return null;
			}

		private String fixStringArray(String[] keyValue) {
				String key = null;
				String value = "";
				boolean isKey = true;
				for (int i = 0; i < keyValue.length; i++) {
						if (keyValue[i].length() > 0 && isKey) {
								key = keyValue[i];
								isKey = false;
							} else {
								if (keyValue[i].length() > 0)
									value += keyValue[i] + " ";
							}
					}
				return key + "+" + value;
			}
	}

	
