package ie.webware.catseyes;
import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.sql.*;
import java.text.*;
import java.util.*;

import java.util.Date;

public class WorldOmeterDatabase
 {
  private SQLiteDatabase db = null;
  private Context context = null;
  ArrayList<String> listOfCountryColumns = new ArrayList<String>(); // complete list of column names
  ArrayList<String> listOfDataColumns = new ArrayList<String>();  // complete list of column names
  TextView view = null;

  public WorldOmeterDatabase(Context _context) throws IOException {
    context = _context;
    db = Database.getInstance(context);
    view = ((Activity)context).findViewById(R.id.mainTextID);
    // If the database already exists populate the country and data column name lists
    if(Database.isExistingDatabase) {
      populateTableColumnNames();
     }

     if(readJSONfromURL()) {
       try {
         notificationMessage("Updating the database.");
         speedReadJSON();
        } catch(Exception e) {}
      }
   }
   
   private boolean readJSONfromURL() {
   boolean downloaded = false;
    // If there is no json file or it's old read it
    String jsonFilePath = context.getFilesDir().getPath().toString() + Constants.jsonPath;
    File jsonFile = new File(jsonFilePath);
    try {
      if(!jsonFile.exists()) {
        downloadJSON();
        downloaded = true;
       } else {
        URL url = new URL(Constants.worldOmeterURL);
        URLConnection urlConnection = url.openConnection();
        urlConnection.connect();
        Long urlTimeStamp = urlConnection.getDate();
        Long jsonTimeStamp = jsonFile.lastModified();

        Date urlTS = new SimpleDateFormat("yyyy-MM-dd").parse(new Timestamp(urlTimeStamp).toString());
        Date jsonTS = new SimpleDateFormat("yyyy-MM-dd").parse(new Timestamp(jsonTimeStamp).toString());
        if(urlTS.after(jsonTS)) {
          downloadJSON();
          downloaded = true;
         }
       }
     } catch(Exception e) {
      Log.d("WorldOmeterDatabase", e.toString());
     }
   return downloaded;
  }

  private void populateTableColumnNames() {
    Cursor cCountryCols = db.query(Constants.tblCountry, null, null, null, null, null, null);
    String[] lstCountryCols = cCountryCols.getColumnNames();
    Cursor cDataCols = db.query(Constants.tblData, null, null, null, null, null, null);
    String[] lstDataCols = cDataCols.getColumnNames();
    listOfCountryColumns = new ArrayList<String>(Arrays.asList(lstCountryCols));
    listOfDataColumns = new ArrayList<String>(Arrays.asList(lstDataCols));
   }

  private void downloadJSON() throws IOException {
    notificationMessage("Downloading " + Constants.worldOmeterURL);
    String filePath = context.getFilesDir().getPath().toString() + Constants.jsonPath;
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
    notificationMessage("Speedreading JSON");
    String filePath = context.getFilesDir().getPath().toString() + Constants.jsonPath;
    BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
    ArrayList<String> rows = new ArrayList<String>();
    String line = null;
    String countryCode = null;
    Date lastDate = new SimpleDateFormat("yyyy-MM-dd").parse("2000-01-01");
    String table = null;
    String row = "";
    boolean isFirstLine = true;

    while((line = bufferedReader.readLine()) != null) {
      if(isFirstLine) {
        isFirstLine = false; 
        continue;
       }
      if(line == null || line.isEmpty()) continue;
      line = line.replaceAll("\"", "").trim();
      if(isCountryCode(line)) {
        if(countryCode == null) {
          countryCode = getCountryCode(line);
          table = Constants.tblCountry;
         } else {
          if(rows.size() > 1) {
            serializeCountry(rows, countryCode);
            toast("Serializing " + countryCode, Toast.LENGTH_SHORT, context);
           } else {
            toast("No update for " + countryCode, Toast.LENGTH_SHORT, context);
           }

          rows = new ArrayList<String>();
          row = "";
          countryCode = getCountryCode(line);
          table = Constants.tblCountry;
         }
        lastDate = setLastDateForThisCountry(countryCode);
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
        if(Database.isExistingDatabase && rowDateIsGreaterThanLastDate(row, lastDate)) {
          rows.add(row); 
         } else if(!Database.isExistingDatabase) {
          rows.add(row); 
         }
        row = "";
        continue;
       }
      row += line.replace("{", "").replace("}", ""); // json formatting unaccounted for
     }
    bufferedReader.close();
    // Copy json & db to application download because tablet and phone not rooted
    copyDBtoDownload();
    notificationMessage("Finished: Touch the grey area to continue");
   }

  private boolean serializeCountry(ArrayList<String> rows, String countryCode) {
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
    boolean isFirstRow = true;
    for(String  row: rows) {
      if(isFirstRow) {
        isFirstRow = false;
        continue; // ignore the first row, it for the country table
       }
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
      idData = db.insert(Constants.tblData, null, values);
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
  private boolean rowDateIsGreaterThanLastDate(String row, Date lastDate) throws Exception {
    if(!row.contains("date:"))
     return false;
    if(!Database.isExistingDatabase)
     return false; // if so save some cycles
    String[] keyValueA = row.split(",");
    String[] keyValueB = keyValueA[1].split(":");
    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(keyValueB[1].toString().trim());
    if(date.after(lastDate))
     return true;
    return false;
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
  private void copyDBtoDownload() throws IOException {
     {
      String srcPath = context.getDatabasePath(Constants.dbName).getPath();
      String dstPath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" +  Constants.dbName;
      copyFile(srcPath, dstPath);
      srcPath = context.getFilesDir().getPath().toString() + Constants.jsonPath;
      dstPath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + Constants.jsonPath;
      copyFile(srcPath, dstPath);
     }
   }

  public void copyFile(String srcPath, String dstPath) throws IOException {
    File srcFile = new File(srcPath);
    File dstFile = new File(dstPath);

    if(dstFile.exists())
     dstFile.delete();

    if(srcFile.exists()) {
      FileInputStream iStream = new FileInputStream(srcFile);
      FileOutputStream oStream = new FileOutputStream(dstFile);
      FileChannel iChannel = iStream.getChannel();
      FileChannel oChannel = oStream.getChannel();
      iChannel.transferTo(0, iChannel.size(), oChannel);
      iStream.close();
      oStream.close();
     }
   }
  public static void toast(final String text, final int length, final Context context) {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
       @Override
       public void run() {
         Toast.makeText(context, text, length).show();
        }
      });
   }
  public void notificationMessage(final String msg) {
    MainActivity.activity.runOnUiThread(new Runnable() {
       @Override
       public void run() {
         new AlertDialog.Builder(context).setMessage(msg).show();
        }
      });
   }
 }
 
 
