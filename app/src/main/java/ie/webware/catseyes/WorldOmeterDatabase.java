package ie.webware.catseyes;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.text.*;
import java.util.*;

public class WorldOmeterDatabase 
{
 private SQLiteDatabase db = null;
 private Context context = null;
 ArrayList<String> listOfCountryColumns = new ArrayList<String>(); // complete list of column names
 ArrayList<String> listOfDataColumns = new ArrayList<String>();  // complete list of column names

 public WorldOmeterDatabase(Context _context) throws IOException
 {
  context = _context;
  db = Database.getInstance(context);
  readJSONfromURL();
  try
  {
   speedReadJSON();
  }
  catch (Exception e)
  {
   String s = e.toString();
  }
 }

 private void readJSONfromURL() throws IOException
 {
  // Need to find last modified timestamp nio acting up.
  String filePath = context.getFilesDir().getPath().toString() + Constants.dbPath;
  File file = new File(filePath);
  if (file.exists()) file.delete();

  ReadableByteChannel readChannel = Channels.newChannel(new URL(Constants.worldOmeterURL).openStream());
  FileOutputStream fileOS = new FileOutputStream(filePath);
  FileChannel writeChannel = fileOS.getChannel();
  writeChannel.transferFrom(readChannel, 0, Long.MAX_VALUE);
  writeChannel.close();
  readChannel.close();
 }

 private void speedReadJSON() throws Exception
 {
  String filePath = context.getFilesDir().getPath().toString() + Constants.dbPath;
  BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
  ArrayList<String> rows = new ArrayList<String>();
  String line = null;
  String countryCode = null;
  Date lastDate = new SimpleDateFormat("yyyy-mm-dd").parse("2000-01-01");
  String table = null;
  String row = "";
  boolean isFirstLine = true;

  while ((line = bufferedReader.readLine()) != null)
  {
   if (isFirstLine)
   {isFirstLine = false; continue;}
   if (line == null || line.isEmpty()) continue;
   line = line.replaceAll("\"", "").trim();

   if (isCountryCode(line))
   {
	if (countryCode == null)
	{
	 countryCode = getCountryCode(line);
	 table = Constants.tblCountry;
	}
	else
	{
	 serializeCountry(rows, countryCode);
	 rows = new ArrayList<String>();
	 row = "";
	 countryCode = getCountryCode(line);
	 table = Constants.tblCountry;
	}
	continue;
   }
   if (isTableData(line))
   {
	if (!rowAlreadyExists(row, lastDate))
	 rows.add(table + ": " + countryCode + ", " + row);
	row = "";
	table = Constants.tblData;
	continue;
   }
   if (newDataRowMarker(line) && !row.isEmpty())
   {
	if (!rowAlreadyExists(row, lastDate))
	 rows.add(table + ": " + countryCode + ", " + row);
	row = "";
	continue;
   }
   row += line;
  }
  bufferedReader.close();
 }
 private boolean serializeCountry(ArrayList<String> rows, String countryCode)
 {
  String continent = null;
  String country = null;
  long fkRegion = 0;
  long fkCountry = 0;
  long idData = 0;
  // populate table region and country if not already populated. Row Zero is the region/country row
  String colCountry = rows.get(0);//rows.toArray(new String[0]);
  String[] columns = colCountry.split(",");
  String[] kvRegion = columns[1].split(":");
  String[] kvCountry = columns[2].split(":");
  continent = kvRegion[1].trim();
  country = kvCountry[1].trim();
  Cursor cId = db.rawQuery("select ID from region where continent = '" + continent + "'", null);
  if (cId.getCount() == 0)
  {
   ContentValues values = new ContentValues();
   values.put("continent", continent);
   fkRegion = db.insert(Constants.tblRegion, null, values);
  }
  else
  {
   cId.moveToFirst();
   fkRegion = cId.getLong(cId.getColumnIndex("ID"));
  }
  // populate country table if not already populated
  cId = db.rawQuery("select ID from country where country_code = '" + countryCode + "'", null);
  if (cId.getCount() == 0)
  {
   ContentValues values = new ContentValues();
   values.put(Constants.fkRegion, fkRegion);
   values.put(Constants.colCountryCode, countryCode);
   fkCountry = db.insert(Constants.tblCountry, null, values);
   ArrayList<String> colName = new ArrayList<String>();
   ArrayList<String> colData = new ArrayList<String>();
   values = new ContentValues();
   for (int i = 1; i < columns.length; i++)
   { // start at 1 because the first element is meta info
	String[] kv = columns[i].split(":");
	String key = kv[0].trim();
	String value = kv[1].replace(",", "").trim();
	colName.add(key);
	colData.add(value);
	values.put(key, value);
   }
   addColumnIfNotExists(Constants.tblCountry, colName, colData);
   idData = db.update(Constants.tblCountry, values, "ID = " + fkCountry, null);
  }
  else
  {
   cId.moveToFirst();
   fkCountry = cId.getLong(cId.getColumnIndex("ID"));
  }
  // populate data if not already populated check for date
  cId = db.rawQuery("select id from data limit 1", null);
  if (cId.getCount() == 0)
  { // The table is empty
   //idData = db.insert(Constants.tblData, null, values);
   for (int o = 1; o < rows.size(); o++)
   { // the first row is for country table, ignore it
	String row = rows.get(o);
	columns = row.split(",");
	ArrayList<String> colName = new ArrayList<String>();
	ArrayList<String> colData = new ArrayList<String>();
	ContentValues values = new ContentValues();
	values.put(Constants.fkCountry, fkCountry);
	for (int i = 1; i < columns.length; i++)
	{ // start at 1 because the first element is meta info
	 String[] kv = columns[i].split(":");
	 String key = kv[0].trim();
	 if (i == 1) key = key.replace("{", ""); // rogue character..
	 String value = kv[1].replace(",", "").trim();
	 colName.add(key);
	 colData.add(value);
	 values.put(key, value);
	}
	addColumnIfNotExists(Constants.tblData, colName, colData);
	idData = db.insert(Constants.tblData, null, values);
   }
  }
  else
  { // Check against the time stamp, if already there ignore the entry otherwise insert it
   cId.moveToFirst();
//      fkRegion = cId.getLong(cId.getColumnIndex("ID"));
  }

  return true;
 }
 private boolean rowAlreadyExists(String row, Date lastDate) throws Exception
 {
  if (!row.contains("date:")) return false;
  if (!Database.isExistingDatabase) return false; // if so save some cycles
  String[] keyValueA = row.split(",");
  String[] keyValueB = keyValueA[0].split(":");
  Date date = new SimpleDateFormat("yyyy-mm-dd").parse(keyValueB[1].toString().trim());
  if (date.after(lastDate)) return false;
  return true;
 }
 private boolean newDataRowMarker(String line)
 {
  if (line.matches("\\{"))
   return true;
  else
   return false;
 }
 private boolean isCountryCode(String line)
 {
  if (line.matches("[A-Z][A-Z][A-Z]: \\{"))
  {
   return true;
  }
  else
  {
   return false;
  }
 }
 public String getCountryCode(String line)
 {
  String[] array = line.split("[:]");
  return array[0].trim();
 }
 private boolean isTableData(String line)
 {
  if (line.matches("data: \\["))
  {
   return true;
  }
  else
  {
   return false;
  }
 }
 public boolean inCountryCodeList(String line)
 {
  if (line.matches("[A-Z][A-Z][A-Z]: \\{"))
  {
   String[] array = line.split("[:]");
   String countryCode = array[0].trim();
   List<String> lstCC = Arrays.asList(Constants.lstCountry);
   if (lstCC.contains(countryCode))
	return true;
   else
	return false;
  }
  return false;
 }


//  private void readJSONfromURL() throws IOException {
//    String filePath = context.getFilesDir().getPath().toString() + Constants.dbPath;
//    File file = new File(filePath);
//    if(file.exists()) file.delete();
//    FileOutputStream oStream = new FileOutputStream(new File(filePath), true);
//
//    URL url = new URL(Constants.worldOmeterURL);
//    HttpURLConnection httpUrlConnection = (HttpURLConnection)url.openConnection();
//    InputStream istream =  httpUrlConnection.getInputStream();
//    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(istream));
//    String line = null;
//    while((line = bufferedReader.readLine()) != null) {
//      oStream.write(line.getBytes());
//      oStream.write('\n');
//     }
//    oStream.close();
//    bufferedReader.close();
//   }
//
//  private void speedReadJSON() throws IOException {
//    String filePath = context.getFilesDir().getPath().toString() + Constants.dbPath;
//
//    boolean bReadCountryCode = true;
//    boolean bReadCountryInformation = true;
//    String previousLine = "";
//    SerializeCountry serializeCountry = new SerializeCountry(db);
//
//    BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
//    String line = null;
//    long l = 0;
//    while((line = bufferedReader.readLine()) != null) {
//      if(l++ == 0) continue; // if this is the first line ignore it
//
//      line = line.trim().replaceAll("\"", ""); 
//      
//      if(bReadCountryCode && !inCountryCodeList(line)) // ignore most countries
//       continue;
//
//      if(bReadCountryCode) {
//        bReadCountryCode = ifCountryCodeRead(line);
//        serializeCountry.setCountryCode(line);
//        continue;
//       }
//      if(bReadCountryInformation) {
//        bReadCountryInformation = ifCountryInformationRead(line);
//        if(!bReadCountryInformation) {
//          continue;
//         }
//        serializeCountry.setCountryDetails(line);
//        continue;
//       }
//       { // bReadCountryData
//        if(ifStartOfDataRowMarker(line)) continue; // rows are seperated by SerializeCountry class
//        if(ifEndOfDataRowMarker(line)) continue;
//        if(ifEndOfCountryDataMarker(line)) {  // end of country rows
//          serializeCountry.commitToDatabase();
//          bReadCountryCode = true;
//          bReadCountryInformation = true;
//          continue;
//         }
//        if(ifEndOfDataMarker(line, previousLine)) {  // and this was the last of all countries data
//          serializeCountry.commitToDatabase();
//          return;
//         } else {
//          previousLine = line;
//          if(line.matches("\\}")) continue; 
//          serializeCountry.setCountryData(line);
//         }
//       }
//     }
//    bufferedReader.close();
//    File file = new File(filePath); // file currently 30 mb in size
//    if(file.exists()) file.delete();
//   }
//
//  private boolean ifCountryCodeRead(String line) {
//    if(line.matches("[A-Z][A-Z][A-Z]: \\{")) {  //line = "AFG: {"
//      return false;
//     } else {
//      return true;
//     }
//   }
//  private boolean ifCountryInformationRead(String line) {
//    if(line.matches("data: \\[")) {
//      return false;
//     } else {
//      return true;
//     }
//   }
//  private boolean ifStartOfDataRowMarker(String line) {
//    if(line.matches("\\{"))
//     return true;
//    else
//     return false;
//   }
//  private boolean ifEndOfDataRowMarker(String line) {
//    if(line.matches("\\},"))
//     return true;
//    else
//     return false;
//   }
//  private boolean ifEndOfCountryDataMarker(String line) {
//    if(line.matches("\\]"))
//     return true;
//    else
//     return false;
//   }
//  private boolean ifEndOfDataMarker(String line, String previousLine) {
//    if(line.matches("\\}") && previousLine.matches("\\]"))
//     return true;
//    else
//     return false;
//   }
//  public boolean inCountryCodeList(String line) {
//    if(line.matches("[A-Z][A-Z][A-Z]: \\{")) {
//      String[] array = line.split("[:]");
//      String countryCode = array[0].trim();
//      List<String> lstCC = Arrays.asList(Constants.lstCountry);
//      if(lstCC.contains(countryCode))
//       return true;
//      else
//       return false;
//    }
//    return false;
//   }
// } // end class
//
//class SerializeCountry
// {
//  private SQLiteDatabase db = null;
//  private long fkRegion;
//  private long fkCountry;
//  private String countryCode;
//  private String continent;
//  private String location;
//  //private boolean addColumns = true;
//  ArrayList<String> lstCountryName = new ArrayList<String>(); // complete list of column names
//  ArrayList<String> lstDataName = new ArrayList<String>();  // complete list of column names
//  ArrayList<String> colCountryKey = new ArrayList<String>();
//  ArrayList<String> colCountryValue = new ArrayList<String>();
//  ArrayList<String> colDataKey = new ArrayList<String>();
//  ArrayList<String> colDataValue = new ArrayList<String>();
//  public SerializeCountry(SQLiteDatabase _db) {
//    db = _db; 
//   }
//
//  public void setCountryCode(String line) {
//    String[] array = line.split("[:]");
//    countryCode = array[0].trim();
//   }
//  public void setCountryDetails(String line) {
//    String[] keyValue = line.split("[:]");
//    colCountryKey.add(keyValue[0]);
//    colCountryValue.add(keyValue[1].trim().replaceAll(",", ""));
//    if(keyValue[0].equals("continent")) {
//      continent = keyValue[1].trim().replaceAll(",", "");
//     }
//    if(keyValue[0].equals("location")) {
//      location = keyValue[1].trim().replaceAll(",", "");
//     }
//   }
//  public void setCountryData(String line) {
//    String[] keyValue = line.split("[:]");
//    colDataKey.add(keyValue[0]);
//    colDataValue.add(keyValue[1].trim().replaceAll(",", ""));
//   }
//  public void commitToDatabase() {
//   if(!Database.isExistingDatabase) {
//     populateRegion();
//     populateCountry();
//   }
//    populateData();
//   }
//
//  private void populateRegion() {
//    Cursor cId = db.rawQuery("select ID from region where continent = '" + continent + "'", null);
//    if(cId.getCount() == 0) {
//      ContentValues values = new ContentValues();
//      values.put("continent", continent);
//      fkRegion = db.insert(Constants.tblRegion, null, values);
//     } else {
//      cId.moveToFirst();
//		  fkRegion = cId.getLong(cId.getColumnIndex("ID"));
//     }
//   }
//  private boolean populateCountry() {
//    Cursor cId = db.rawQuery("select ID from country where " + Constants.CountryCode + " = '" + countryCode + "'", null);
//    if(cId.getCount() == 0) {
//      ContentValues values = new ContentValues();
//      values.put(Constants.fkRegion, fkRegion);
//      values.put(Constants.CountryCode, countryCode);
//      fkCountry = db.insert(Constants.tblCountry, null, values);
//     } else {
//      cId.moveToFirst();
//      fkCountry = cId.getLong(cId.getColumnIndex("ID"));
//     }
//    /*addColumns = */addColumnIfNotExists(Constants.tblCountry, colCountryKey, colCountryValue);
//    // add in row data
//    ContentValues values = new ContentValues();
//    for(int i = 0; i < colCountryKey.size(); i++) {
//      String key = colCountryKey.get(i);
//      String value = colCountryValue.get(i);
//      values.put(key, value);
//     }
//    long nCol = db.update(Constants.tblCountry, values, "ID = " + fkCountry, null);
//    colCountryKey.clear();
//    colCountryValue.clear();
//    if(nCol == -1)
//     return false;
//    else
//     return true;
//   }
//
//  private boolean populateData() {
//    long nCol = 0;
//    addColumnIfNotExists(Constants.tblData, colDataKey, colDataValue);
//
//    ContentValues values = new ContentValues();
//    values.put(Constants.fkCountry, fkCountry);
//
//    // ToDo: if existing database, check the last date entry and ignore all entries till after that
//    for(int i = 0; i < colDataKey.size(); i++) {
//      String key = colDataKey.get(i);
//      String value = colDataValue.get(i);
//      if(colDataKey.get(i).equals("date") && i > 0) {
//        nCol = db.insert(Constants.tblData, null, values);
//        if(nCol == -1) {
//          return false;
//         } else {
//          values = new ContentValues();
//          values.put(Constants.fkCountry, fkCountry);
//         }
//       }
//      values.put(key, value);
//     }
//    colDataKey.clear();
//    colDataValue.clear();
//    return true;
//   }
//
 private boolean addColumnIfNotExists(String table, ArrayList<String> colList, ArrayList<String> colData)
 {
  for (int i = 0; i < colList.size(); i++)
  {
   boolean isDouble = false;
   boolean isDate = false;
   boolean isString = false;
   String type = "";
   try
   {
	Double d = Double.parseDouble(colData.get(i));
	isDouble = true;
   }
   catch (Exception e)
   {
	if (colList.get(i).equals("date"))
	{
	 isDate = true;
	}
	else
	{ isString = true;}
   }
   finally
   {
	if (isString) type = "TEXT";
	if (isDouble) type = "decimal (10, 3)";
	if (isDate) type = "DATE";
   }
   String columnName = colList.get(i);
   if (table.equals(Constants.tblCountry))
   {
	if (!listOfCountryColumns.contains(columnName))
	{
	 listOfCountryColumns.add(columnName);
	 db.execSQL("alter table " + table + " add column " + columnName + " " + type);
	} 
   }
   else if (table.equals(Constants.tblData))
   {
	if (!listOfDataColumns.contains(columnName))
	{
	 listOfDataColumns.add(columnName);
	 db.execSQL("alter table " + table + " add column " + columnName + " " + type);
	}
   }
  }
  return false;
 }
}
 
 
