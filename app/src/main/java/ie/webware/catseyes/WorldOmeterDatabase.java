package ie.webware.catseyes;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import java.io.*;
import java.net.*;
import java.util.*;

class WorldOmeterDatabase {
		
		private ArrayList<String> jsonFragment = new ArrayList<String>();
		private Context context = null;
		
		public WorldOmeterDatabase(Context _context) {
				context = _context;
				populateLocalDatabase();
			}
			
		public Void populateLocalDatabase() {
				pullWorldometerJSON();
				speedReadJSON();
				SQLiteDatabase db = new SQL(context).getInstance();
				addTableColumns(db);
				return null;
			}

		private boolean pullWorldometerJSON() {
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
						return false;
					}
				return true;
			}
		private boolean speedReadJSON() {
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
						boolean isData = false;
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
								isData = true;
							}
						TableKeyValue tkv = new TableKeyValue();
						tkv.isData = isData;
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
				return true;
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
		private boolean addTableColumns(SQLiteDatabase db) {
			// To Read, Cursor c = db.rawQuery("select * from Region", null);
			// To Write, db.execSQL("insert into .....");
			String colCountry = "";//"alter table Country add";
			String colData = "";//"alter table Data add";
			String sql = null;
			for(int i = 0; i < MainActivity.tableKeyValue.size(); i++) {
					boolean isData = MainActivity.tableKeyValue.get(i).isData;
					boolean isDate = MainActivity.tableKeyValue.get(i).isDate;
					boolean isNumeric = MainActivity.tableKeyValue.get(i).isNumeric;
					String table = MainActivity.tableKeyValue.get(i).table;
					String key = MainActivity.tableKeyValue.get(i).key;
					
					if(table.equals(Constants.tblCountry)) {
						if(colCountry.contains(key))
							continue;
							colCountry += " " + key;
							sql = "alter table Country add " + defineColumn(isDate, isNumeric, key);
					} else if(table.equals(Constants.tblData)) {
						if(colData.contains(key))
							continue;
							colData += " " + key;
							sql = "alter table Data add " + defineColumn(isDate, isNumeric, key);
					}
					db.execSQL(sql);
			}
			
			Cursor c = db.rawQuery("select * from Country", null);
			Cursor d = db.rawQuery("select * from Data", null);
			
			return true;
		}
		
		private String defineColumn(boolean isDate, boolean isNumeric, String key) {
			String coldef = " ";
				
			if(isDate) {
				coldef = key + " date";
			} else if(isNumeric) {
				coldef = key + " decimal (10, 3)";
			} else { // is string
				coldef = key + " text";
			}
			return coldef;
		}
	}

	
