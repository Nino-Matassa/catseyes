package ie.webware.catseyes;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import java.io.*;
import java.net.*;
import java.util.*;
import android.util.*;
import java.security.*;

class WorldOmeterDatabase
 {

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
	populateDatabase(db);
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
	  while((line = bufferedReader.readLine()) != null) {
		jsonFragment.add(line.replaceAll(Constants.jsonWood, " "));
		fragmentIndex++;
		if(fragmentIndex == 10000) break; // test line
	   }
	  bufferedReader.close();
	 } catch(Exception e) {
	  return false;
	 }
	return true;
   }
  private boolean speedReadJSON() {
	int level = 0;// 1 = Region, 2 = Country, 3 = Data
	for(int i = 0; i < jsonFragment.size(); i++) {
	  String line = jsonFragment.get(i);
	  if(line.contains("{") || line.contains("}") || line.contains("},") || line.contains("[") || line.contains("]")) {
		if(line.contains("{")) {
		  level++;	
		  continue;
		 } else if(line.contains("}")) {
		  level--;
		  continue;
		 } else if(line.contains("[") || line.contains("]"))
		 continue;
	   }
	  String table = null;
	  String key = null;
	  String value = null;
	  boolean isData = false;
	  if(level == 1 && line.contains("{")) {
		table = Constants.tblRegion;
		line = line.replaceAll(Constants.jsonTree, "");
		key = Constants.colContinent;
		value = line;
		level++;
	   } else if(level == 2) {
		table = Constants.tblCountry;
		String[] keyValue = line.split(" ");
		String values = fixStringArray(keyValue);
		keyValue = values.split("[+]");
		key = keyValue[0].toString();
		value = keyValue[1].toString();
	   } else if(level == 3) {
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
	  if(key.equals("date")) {
		tkv.isDate = true;
		tkv.isNumeric = false;
	   } else {
		try {
		  Double d = Double.parseDouble(value);
		  tkv.isNumeric = true;
		 } catch(Exception e) {
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
	for(int i = 0; i < keyValue.length; i++) {
	  if(keyValue[i].length() > 0 && isKey) {
		key = keyValue[i];
		isKey = false;
	   } else {
		if(keyValue[i].length() > 0)
		 value += keyValue[i] + " ";
	   }
	 }
	return key + "+" + value;
   }
  private boolean addTableColumns(SQLiteDatabase db) {
	String colCountry = "";
	String colData = "";
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

  private boolean populateDatabase(SQLiteDatabase db) {
	int foreignKey = 0;
	ContentValues values = null;
	int index = 0;
	int size = MainActivity.tableKeyValue.size();
	for(index = 0; index < size; index++) {
	  if(MainActivity.tableKeyValue.get(index).key.equals(Constants.isCountryLead)) {
		TableKeyValue tkv = MainActivity.tableKeyValue.get(index);
		values = new ContentValues();
		values.put(tkv.key, tkv.value);
		Cursor cId = db.rawQuery("select * from region where " + tkv.key + " = '" + tkv.value + "'", null);
		if(cId.getCount() == 0)
		 db.insert(Constants.tblRegion, null, values);
	  }
	}
	for(index = 0; index < MainActivity.tableKeyValue.size(); index++) {
	  TableKeyValue tkv = MainActivity.tableKeyValue.get(index);
	  if(tkv.table.equals(Constants.tblCountry)) {
		if(tkv.key.equals(Constants.isCountryLead)) {
		  values = new ContentValues();
		  String sql = "select id from region where continent = '" + tkv.value + "'";
		  Cursor rs = db.rawQuery(sql, null);
		  if(rs.getCount() == 0) {
			Cursor cId = db.rawQuery("select * from region where " + tkv.key + " = '" + tkv.value + "'", null);
			cId.moveToFirst();
			foreignKey = cId.getInt(cId.getColumnIndex("ID"));
			values.put(Constants.fkRegion, foreignKey); // never hits here
		   }		   
		 }
		values.put(tkv.key, tkv.value);
		if(MainActivity.tableKeyValue.get(index + 1).table.equals(Constants.tblData)) {
		  db.insert(Constants.tblCountry, null, values);
		 }
	   } else if(tkv.table.equals(Constants.tblData)) {
		values = new ContentValues();
		values.put(Constants.fkCountry, foreignKey);
		 while(index < MainActivity.tableKeyValue.size() && tkv.table.equals(Constants.tblData)) {
		  tkv = MainActivity.tableKeyValue.get(index);
		  values.put(tkv.key, tkv.value);
		  index++;
		  if(index == MainActivity.tableKeyValue.size()) continue;
		  if(MainActivity.tableKeyValue.get(index).table.equals(Constants.tblCountry)) {
			db.insert(Constants.tblCountry, null, values);
			continue;
		   } else if(MainActivity.tableKeyValue.get(index).key.equals(Constants.isDataLead)) {
			db.insert(Constants.tblData, null, values);
			values = new ContentValues();
			values.put(Constants.fkCountry, foreignKey);
		   }
		 }
	   }
	 }
	return true;
   }
 }

	
