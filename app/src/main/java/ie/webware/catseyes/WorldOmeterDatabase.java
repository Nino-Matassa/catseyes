package ie.webware.catseyes;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class WorldOmeterDatabase
 {
  private SQLiteDatabase db = null;
  private Context context = null;

  public WorldOmeterDatabase(Context _context) throws IOException {
    context = _context;
    db = Database.getInstance(context);
    readJSONfromURL();
    speedReadJSON();
   }

  private void readJSONfromURL() throws IOException {
    String filePath = context.getFilesDir().getPath().toString() + Constants.dbPath;
    File file = new File(filePath);
    if(file.exists()) file.delete();
    FileOutputStream oStream = new FileOutputStream(new File(filePath), true);

    URL url = new URL(Constants.worldOmeterURL);
    HttpURLConnection httpUrlConnection = (HttpURLConnection)url.openConnection();
    InputStream istream =  httpUrlConnection.getInputStream();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(istream));
    String line = null;
    while((line = bufferedReader.readLine()) != null) {
      oStream.write(line.getBytes());
      oStream.write('\n');
     }
    oStream.close();
    bufferedReader.close();
   }

  private void speedReadJSON() throws IOException {
    String filePath = context.getFilesDir().getPath().toString() + Constants.dbPath;

    boolean bReadCountryCode = true;
    boolean bReadCountryInformation = true;
    String previousLine = "";
    SerializeCountry serializeCountry = new SerializeCountry(db);

    BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
    String line = null;
    long l = 0;
    while((line = bufferedReader.readLine()) != null) {
      if(l++ == 0) continue; // if this is the first line ignore it

      line = line.trim().replaceAll("\"", ""); 
      
      if(bReadCountryCode && !inCountryCodeList(line)) // ignore most countries
       continue;

      if(bReadCountryCode) {
        bReadCountryCode = ifCountryCodeRead(line);
        serializeCountry.setCountryCode(line);
        continue;
       }
      if(bReadCountryInformation) {
        bReadCountryInformation = ifCountryInformationRead(line);
        if(!bReadCountryInformation) {
          continue;
         }
        serializeCountry.setCountryDetails(line);
        continue;
       }
       { // bReadCountryData
        if(ifStartOfDataRowMarker(line)) continue; // rows are seperated by SerializeCountry class
        if(ifEndOfDataRowMarker(line)) continue;
        if(ifEndOfCountryDataMarker(line)) {  // end of country rows
          serializeCountry.commitToDatabase();
          bReadCountryCode = true;
          bReadCountryInformation = true;
          continue;
         }
        if(ifEndOfDataMarker(line, previousLine)) {  // and this was the last of all countries data
          serializeCountry.commitToDatabase();
          return;
         } else {
          previousLine = line;
          if(line.matches("\\}")) continue; 
          serializeCountry.setCountryData(line);
         }
       }
     }
    bufferedReader.close();
    File file = new File(filePath); // file currently 30 mb in size
    if(file.exists()) file.delete();
   }

  private boolean ifCountryCodeRead(String line) {
    if(line.matches("[A-Z][A-Z][A-Z]: \\{")) {  //line = "AFG: {"
      return false;
     } else {
      return true;
     }
   }
  private boolean ifCountryInformationRead(String line) {
    if(line.matches("data: \\[")) {
      return false;
     } else {
      return true;
     }
   }
  private boolean ifStartOfDataRowMarker(String line) {
    if(line.matches("\\{"))
     return true;
    else
     return false;
   }
  private boolean ifEndOfDataRowMarker(String line) {
    if(line.matches("\\},"))
     return true;
    else
     return false;
   }
  private boolean ifEndOfCountryDataMarker(String line) {
    if(line.matches("\\]"))
     return true;
    else
     return false;
   }
  private boolean ifEndOfDataMarker(String line, String previousLine) {
    if(line.matches("\\}") && previousLine.matches("\\]"))
     return true;
    else
     return false;
   }
  public boolean inCountryCodeList(String line) {
    if(line.matches("[A-Z][A-Z][A-Z]: \\{")) {
      String[] array = line.split("[:]");
      String countryCode = array[0].trim();
      List<String> lstCC = Arrays.asList(Constants.lstCountry);
      if(lstCC.contains(countryCode))
       return true;
      else
       return false;
    }
    return false;
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
  //private boolean addColumns = true;
  ArrayList<String> lstCountryName = new ArrayList<String>(); // complete list of column names
  ArrayList<String> lstDataName = new ArrayList<String>();  // complete list of column names
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
    colCountryValue.add(keyValue[1].trim().replaceAll(",", ""));
    if(keyValue[0].equals("continent")) {
      continent = keyValue[1].trim().replaceAll(",", "");
     }
    if(keyValue[0].equals("location")) {
      location = keyValue[1].trim().replaceAll(",", "");
     }
   }
  public void setCountryData(String line) {
    String[] keyValue = line.split("[:]");
    colDataKey.add(keyValue[0]);
    colDataValue.add(keyValue[1].trim().replaceAll(",", ""));
   }
  public void commitToDatabase() {
   if(!Database.isExistingDatabase) {
     populateRegion();
     populateCountry();
   }
    populateData();
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
  private boolean populateCountry() {
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
    /*addColumns = */addColumnIfNotExists(Constants.tblCountry, colCountryKey, colCountryValue);
    // add in row data
    ContentValues values = new ContentValues();
    for(int i = 0; i < colCountryKey.size(); i++) {
      String key = colCountryKey.get(i);
      String value = colCountryValue.get(i);
      values.put(key, value);
     }
    long nCol = db.update(Constants.tblCountry, values, "ID = " + fkCountry, null);
    colCountryKey.clear();
    colCountryValue.clear();
    if(nCol == -1)
     return false;
    else
     return true;
   }

  private boolean populateData() {
    long nCol = 0;
    addColumnIfNotExists(Constants.tblData, colDataKey, colDataValue);

    ContentValues values = new ContentValues();
    values.put(Constants.fkCountry, fkCountry);

    // ToDo: if existing database, check the last date entry and ignore all entries till after that
    for(int i = 0; i < colDataKey.size(); i++) {
      String key = colDataKey.get(i);
      String value = colDataValue.get(i);
      if(colDataKey.get(i).equals("date") && i > 0) {
        nCol = db.insert(Constants.tblData, null, values);
        if(nCol == -1) {
          return false;
         } else {
          values = new ContentValues();
          values.put(Constants.fkCountry, fkCountry);
         }
       }
      values.put(key, value);
     }
    colDataKey.clear();
    colDataValue.clear();
    return true;
   }

  private boolean addColumnIfNotExists(String table, ArrayList<String> colList, ArrayList<String> colData) {
    for(int i = 0; i < colList.size(); i++) {
      boolean isDouble = false;
      boolean isDate = false;
      boolean isString = false;
      String type = "";
      try {
        Double d = Double.parseDouble(colData.get(i));
        isDouble = true;
       } catch(Exception e) {
        if(colList.get(i).equals("date")) {
          isDate = true;
         } else { isString = true;}
       } finally {
        if(isString) type = "TEXT";
        if(isDouble) type = "decimal (10, 3)";
        if(isDate) type = "DATE";
       }
      String columnName = colList.get(i);
      if(table.equals(Constants.tblCountry)) {
        if(!lstCountryName.contains(columnName)) {
          lstCountryName.add(columnName);
          db.execSQL("alter table " + table + " add column " + columnName + " " + type);
         } 
       } else if(table.equals(Constants.tblData)) {
        if(!lstDataName.contains(columnName)) {
          lstDataName.add(columnName);
          db.execSQL("alter table " + table + " add column " + columnName + " " + type);
         }
       }
     }
    return false;
   }
 }
 
 
