package ie.webware.catseyes;
import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.widget.*;
import ie.webware.catseyes.*;
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

  public WorldOmeterDatabase(Context _context) throws IOException {
    context = _context;
    db = Database.getInstance(context);
    // Test code, read timestamp from json url
    
    String filePath = context.getFilesDir().getPath().toString() + Constants.dbPath;
    File file = new File(filePath);
    if(!file.exists( )) {
      DatabaseStatus.setStatus("Reading " + Constants.worldOmeterURL);
      readJSONfromURL(); 
    }
    if(Database.isExistingDatabase) {
     populateTableColumnNames();
    }
    try {
     DatabaseStatus.setStatus("Updating Database");
      speedReadJSON();
     } catch(Exception e) {
      String s = e.toString();
     }
   }
   
  private void populateTableColumnNames() {
    Cursor cCountryCols = db.query(Constants.tblCountry, null, null, null, null, null, null);
    String[] lstCountryCols = cCountryCols.getColumnNames();
    Cursor cDataCols = db.query(Constants.tblData, null, null, null, null, null, null);
    String[] lstDataCols = cDataCols.getColumnNames();
    listOfCountryColumns = new ArrayList<String>(Arrays.asList(lstCountryCols));
    listOfDataColumns = new ArrayList<String>(Arrays.asList(lstDataCols));
  }

  private void readJSONfromURL() throws IOException {
    String filePath = context.getFilesDir().getPath().toString() + Constants.dbPath;
    File file = new File(filePath);
    if(file.exists()) file.delete();

    ReadableByteChannel readChannel = Channels.newChannel(new URL(Constants.worldOmeterURL).openStream());
    FileOutputStream fileOS = new FileOutputStream(filePath);
    FileChannel writeChannel = fileOS.getChannel();
    writeChannel.transferFrom(readChannel, 0, Long.MAX_VALUE);
    writeChannel.close();
    readChannel.close();
   }

  private void speedReadJSON() throws Exception {
    String filePath = context.getFilesDir().getPath().toString() + Constants.dbPath;
    BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
    ArrayList<String> rows = new ArrayList<String>();
    String line = null;
    String countryCode = null;
    Date lastDate = new SimpleDateFormat("yyyy-MM-dd").parse("2000-01-01");
    String table = null;
    String row = "";
    boolean isFirstLine = true;

    while((line = bufferedReader.readLine()) != null) {
      if(isFirstLine) {isFirstLine = false; continue;}
      if(line == null || line.isEmpty()) continue;
      line = line.replaceAll("\"", "").trim();

      if(isCountryCode(line)) {
        if(countryCode == null) {
          countryCode = getCountryCode(line);
          table = Constants.tblCountry;
         } else {
          serializeCountry(rows, countryCode);
          rows = new ArrayList<String>();
          row = "";
          countryCode = getCountryCode(line);
          table = Constants.tblCountry;
         }
        lastDate = setLastDateForThisCountry(countryCode);
        //DatabaseStatus.setStatus("Updating " + countryCode); throws an exception, wrong thread
        continue;
       }
      if(isTableData(line)) {
        rows.add(table + ": " + countryCode + ", " + row);
        row = "";
        table = Constants.tblData;
        continue;
       }
      if(newDataRowMarker(line) && !row.isEmpty()) {
        row = table + ": " + countryCode + ", " + row;
        if(Database.isExistingDatabase && !rowAlreadyExists(row, lastDate)) {
          rows.add(row); 
        } else if(!Database.isExistingDatabase){
          rows.add(row); 
        }
        row = "";
        continue;
       }
      row += line.replace("{", "").replace("}", ""); // json formatting unaccounted for
     }
    bufferedReader.close();
   }
  private boolean serializeCountry(ArrayList<String> rows, String countryCode) {
    String continent = null;
    String country = null;
    long fkRegion = 0;
    long fkCountry = 0;
    long idData = 0;
    Date lastDate = null;
    // populate table region and country if not already populated. Row Zero is the region/country row
    String colCountry = rows.get(0);//rows.toArray(new String[0]);
    String[] columns = colCountry.split(",");
    String[] kvRegion = columns[1].split(":");
    String[] kvCountry = columns[2].split(":");
    continent = kvRegion[1].trim();
    country = kvCountry[1].trim();
    Cursor cId = db.rawQuery("select ID from region where continent = '" + continent + "'", null);
    if(cId.getCount() == 0) {
      ContentValues values = new ContentValues();
      values.put("continent", continent);
      fkRegion = db.insert(Constants.tblRegion, null, values);
     } else {
      cId.moveToFirst();
      fkRegion = cId.getLong(cId.getColumnIndex("ID"));
     }
    // populate country table if not already populated
    cId = db.rawQuery("select ID from country where country_code = '" + countryCode + "'", null);
    if(cId.getCount() == 0) {
      ContentValues values = new ContentValues();
      values.put(Constants.fkRegion, fkRegion);
      values.put(Constants.colCountryCode, countryCode);
      fkCountry = db.insert(Constants.tblCountry, null, values);
      ArrayList<String> colName = new ArrayList<String>();
      ArrayList<String> colData = new ArrayList<String>();
      values = new ContentValues();
      for(int i = 1; i < columns.length; i++) { // start at 1 because the first element is meta info
        String[] kv = columns[i].split(":");
        String key = kv[0].trim();
        String value = kv[1].replace(",", "").trim();
        colName.add(key);
        colData.add(value);
        values.put(key, value);
       }
      addColumnIfNotExists(Constants.tblCountry, colName, colData);
      idData = db.update(Constants.tblCountry, values, "ID = " + fkCountry, null);
     } else {
      cId.moveToFirst();
      fkCountry = cId.getLong(cId.getColumnIndex("ID"));
     }
    // populate data if not already populated check for lastDate
    for(int o = 1; o < rows.size(); o++) { // the first row is for country table, ignore it
      String row = rows.get(o);
      columns = row.split(",");
      ArrayList<String> colName = new ArrayList<String>();
      ArrayList<String> colData = new ArrayList<String>();
      ContentValues values = new ContentValues();
      values.put(Constants.fkCountry, fkCountry);
      for(int i = 1; i < columns.length; i++) { // start at 1 because the first element is meta info
        String[] kv = columns[i].split(":");
        String key = kv[0].trim();
        String value = kv[1].replace(",", "").trim();
        colName.add(key);
        colData.add(value);
        values.put(key, value);
       }
      addColumnIfNotExists(Constants.tblData, colName, colData);
      try {
        if(Database.isExistingDatabase && !rowAlreadyExists(rows.get(o), lastDate)) {
          idData = db.insert(Constants.tblData, null, values);
         }
       } catch(Exception e) {
        String s = e.toString();
       }
       if(!Database.isExistingDatabase) {
         idData = db.insert(Constants.tblData, null, values);
       }
     }
    return true;
   }
  private Date setLastDateForThisCountry(String countryCode) {
    Date lastDate = null;
    if(Database.isExistingDatabase) {
      try {
        String sql = "select date from data join country on data.fk_country = country.id where country.country_code = '#' order by date desc limit 1";
        sql = sql.replace("#", countryCode.toString());
        Cursor cDate = db.rawQuery(sql, null);
        cDate.moveToFirst();
        String sDate = cDate.getString(cDate.getColumnIndex("date"));
        lastDate = new SimpleDateFormat("yyyy-MM-dd").parse(sDate);
       } catch(ParseException e) {
        String s = e.toString();
       } 
     }
     return lastDate;
  }
  private boolean rowAlreadyExists(String row, Date lastDate) throws Exception {
    if(!row.contains("date:"))
     return false;
    if(!Database.isExistingDatabase)
     return false; // if so save some cycles
    String[] keyValueA = row.split(",");
    String[] keyValueB = keyValueA[1].split(":");
    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(keyValueB[1].toString().trim());
    if(date.after(lastDate))
     return false;
    return true;
   }
  private boolean newDataRowMarker(String line) {
    if(line.matches("\\{"))
     return true;
    else
     return false;
   }
  private boolean isCountryCode(String line) {
    if(line.matches("[A-Z][A-Z][A-Z]: \\{")) {
      return true;
     } else {
      return false;
     }
   }
  public String getCountryCode(String line) {
    String[] array = line.split("[:]");
    return array[0].trim();
   }
  private boolean isTableData(String line) {
    if(line.matches("data: \\[")) {
      return true;
     } else {
      return false;
     }
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
        if(!listOfCountryColumns.contains(columnName)) {
          listOfCountryColumns.add(columnName);
          db.execSQL("alter table " + table + " add column " + columnName + " " + type);
         } 
       } else if(table.equals(Constants.tblData)) {
        if(!listOfDataColumns.contains(columnName)) {
          listOfDataColumns.add(columnName);
          db.execSQL("alter table " + table + " add column " + columnName + " " + type);
         }
       }
     }
    return false;
   }
 }
 
 
