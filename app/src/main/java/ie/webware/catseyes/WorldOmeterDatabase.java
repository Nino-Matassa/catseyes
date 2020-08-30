package ie.webware.catseyes;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import java.io.*;
import java.net.*;
import java.util.*;
import android.util.*;
import java.security.*;

public class WorldOmeterDatabase
 {
  private ArrayList<String> jsonFragment = new ArrayList<String>();
  private SQLiteDatabase db = null;
  private Context context = null;
  //private ArrayList<SerializeCountry> serialized = new ArrayList<SerializeCountry>();

  public WorldOmeterDatabase(Context _context) {
    context = _context;
    db = new SQL(context).getInstance();
    readJSONfromURL();
    db.close();
   }

  private boolean readJSONfromURL() {
    BufferedReader bufferedReader = null;
    try {
      URL url = new URL(Constants.worldOmeterURL);
      HttpURLConnection httpUrlConnection = (HttpURLConnection)url.openConnection();
      InputStream inputStream =  httpUrlConnection.getInputStream();
      bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      boolean bReadCountryCode = true;
      boolean bReadCountryInformation = true;

      String line = null;
      SerializeCountry serializeCountry = new SerializeCountry(db);
      while((line = bufferedReader.readLine().trim().replaceAll("\"", "")) != null) {
        if(bReadCountryCode) {
          line = line.replaceAll("[^a-zA-Z0-9._\\[\\]\\{\\}\\:]", "");
          if(line.matches("[A-Z][A-Z][A-Z]:\\{")) {
            serializeCountry.setCountryCode(line);
            bReadCountryCode = false;
           }
          continue;
         }
        if(bReadCountryInformation) {
          if(line.matches("data: \\[")) {
            bReadCountryInformation = false;
            continue;
           }
          serializeCountry.setCountryDetails(line);
          continue;
         }
        //if(bReadCountryData) {
         {
          if(line.matches("\\{"))
          // Start new row
           continue;
          if(line.matches("\\},")) {  // end of country data
            // row complete
            serializeCountry.setCountryData("*:*");
            serializeCountry.commitToDatabase();
            return true; // for debugging
            //continue;
           }
          if(line.matches("\\}")) {  // end of all countries data
            // row complete
            serializeCountry.setCountryData("*:*");
            serializeCountry.commitToDatabase();
            return true;
           }
          serializeCountry.setCountryData(line);
         }
       }
      bufferedReader.close();
     } catch(Exception e) {
      return false;
     }
    return true;
   }


 } // end class

class SerializeCountry
 {
  private SQLiteDatabase db = null;
  private long fkRegion;
  private long fkCountry;
  private String countryCode;
  private String continent;
  private String location;
  private boolean addColumns = true;
  ArrayList<String> colCountryKey = new ArrayList<String>();
  ArrayList<String> colCountryValue = new ArrayList<String>();
  ArrayList<String> colDataKey = new ArrayList<String>();
  ArrayList<String> colDataValue = new ArrayList<String>();

  public SerializeCountry(SQLiteDatabase _db) {
    db = _db; 
   }

  public void setCountryCode(String line) {
    String[] array = line.split("[:]");
    countryCode = array[0].trim();
   }
  public void setCountryDetails(String line) {
    String[] keyValue = line.split("[:]");
    colCountryKey.add(keyValue[0]);
    colCountryValue.add(keyValue[1]);
    if(keyValue[0].equals("continent")) {
      continent = keyValue[1].trim();
     }
    if(keyValue[0].equals("location")) {
      location = keyValue[1].trim();
     }
   }
  public void setCountryData(String line) {
    String[] keyValue = line.split("[:]");
    colDataKey.add(keyValue[0]);
    colDataValue.add(keyValue[1].trim().replace(",", ""));
   }
  public void commitToDatabase() {
    populateRegion();
    populateCountry();
   }

  private void populateRegion() {
    Cursor cId = db.rawQuery("select ID from region where continent = '" + continent + "'", null);
    if(cId.getCount() == 0) {
      ContentValues values = new ContentValues();
      values.put("continent", continent);
      fkRegion = db.insert(Constants.tblRegion, null, values);
     } else {
      cId.moveToFirst();
		  fkRegion = cId.getLong(cId.getColumnIndex("ID"));
     }
   }
  private void populateCountry() {
    Cursor cId = db.rawQuery("select ID from country where " + Constants.CountryCode + " = '" + countryCode + "'", null);
    if(cId.getCount() == 0) {
      ContentValues values = new ContentValues();
      values.put(Constants.fkRegion, fkRegion);
      values.put(Constants.CountryCode, countryCode);
      fkCountry = db.insert(Constants.tblCountry, null, values);
     } else {
      cId.moveToFirst();
      fkCountry = cId.getLong(cId.getColumnIndex("ID"));
     }
    // dynamically add in all columns if needs be???
    if(addColumns) {
      addColumns = false;
      for(int i = 0; i < colCountryKey.size(); i++) {
        boolean isDouble = false;
        boolean isDate = false;
        boolean isString = false;
        String type = "";
        try {
          Double d = Double.parseDouble(colCountryValue.get(i));
          isDouble = true;
         } catch(Exception e) {
          if(colCountryKey.get(i).equals("date")) {
            isDate = true;
           } else { isString = true;}
         } finally {
          type = "TEXT";
          if(isDouble) type = "INT";
          if(isDate) type = "DATE";
         }
        // ALTER TABLE {tableName} ADD COLUMN COLNew {type};
        String columnName = colCountryKey.get(i);
        db.execSQL("alter table country add column " + columnName + " " + type);
       }

     }

    // add in row data
    for(int i = 0; i < colCountryKey.size(); i++) {
      String key = colCountryKey.get(i);
      String value = colCountryValue.get(i);

     }
   }
 }
 

/*
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

  private void populateDatabase(SQLiteDatabase db) {
	populateRegionTable(db);
	populateCountryTable(db);
   }
  
  private void populateRegionTable(SQLiteDatabase db) {
	ContentValues values = null;
	int index = 0;
	for(index = 0; index < MainActivity.tableKeyValue.size(); index++) { // populate table region
	  if(MainActivity.tableKeyValue.get(index).key.equals(Constants.isCountryLead)) {
		TableKeyValue tkv = MainActivity.tableKeyValue.get(index);
		values = new ContentValues();
		values.put(tkv.key, tkv.value);
		Cursor cId = db.rawQuery("select ID from region where " + tkv.key + " = '" + tkv.value + "'", null);
		if(cId.getCount() == 0)
		 db.insert(Constants.tblRegion, null, values);
	   }
	 }
   }

  private void populateCountryTable(SQLiteDatabase db) {
	int index = 0;
	ContentValues values = null;
	for(; index + 1 < MainActivity.tableKeyValue.size(); index++) { // populate table country
	  TableKeyValue tkv = MainActivity.tableKeyValue.get(index);
	  int foreignKey = 0;
	  if(tkv.table.equals(Constants.tblCountry)) {
		if(tkv.key.equals(Constants.isCountryLead)) { // set foreign key
		  values = new ContentValues();
		  String sql = "select ID from region where " + tkv.key + " = '" + tkv.value + "'";
		  Cursor cId = db.rawQuery(sql, null);
		  cId.moveToFirst();
		  foreignKey = cId.getInt(cId.getColumnIndex("ID"));
		  values.put(Constants.fkRegion, foreignKey);
		 }
		values.put(tkv.key, tkv.value);
		if(MainActivity.tableKeyValue.get(index + 1).table.equals(Constants.tblData)) {
		  db.insert(Constants.tblCountry, null, values);
		  index = populateDataTable(db, foreignKey, index + 1);
		 }
	   }
	 }	
   }

  private int populateDataTable(SQLiteDatabase db, int fkCountry, int index) {
	ContentValues values = null;
	int i = index;
	for(; i + 1 < MainActivity.tableKeyValue.size(); i++) { // populate table data
	  TableKeyValue tkv = MainActivity.tableKeyValue.get(i);
	  values = new ContentValues();
	  if(tkv.table.equals(Constants.tblData)) {
		if(tkv.key.equals(Constants.isDataLead)) { // set foreign key
		  values.put(Constants.fkRegion, fkCountry);
		 }
		values.put(tkv.key, tkv.value);
		if(MainActivity.tableKeyValue.get(i + 1).table.equals(Constants.tblData)) {
		  if(MainActivity.tableKeyValue.get(i + 1).key.equals(Constants.isDataLead)) {
			db.insert(Constants.tblData, null, values);
		   }
		 }
		if(MainActivity.tableKeyValue.get(i + 1).table.equals(Constants.tblCountry))
		 return i;
	   }
	 }
	return i;
   }
 }
*/

	
