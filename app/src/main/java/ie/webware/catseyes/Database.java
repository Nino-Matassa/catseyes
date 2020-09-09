package ie.webware.catseyes;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.os.*;

class SQL extends SQLiteOpenHelper
 {
  // Creating table sql
  private String createTableRegion = "create table " + Constants.tblRegion + 
  "(" + Constants.pkId + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
  Constants.colContinent + " TEXT NOT NULL);";

  private String createTableCountry = "create table " + Constants.tblCountry + 
  "(" + Constants.pkId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
  Constants.fkRegion + " INT NOT NULL, " +
  Constants.CountryCode + " TEXT, " +
  "FOREIGN KEY (" + Constants.fkRegion + ") REFERENCES " + Constants.tblRegion + 
  " (" + Constants.pkId + "));";

  private String createTableData = "create table " + Constants.tblData + 
  "(" + Constants.pkId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
  Constants.fkCountry + " INT NOT NULL, " +
  "FOREIGN KEY (" + Constants.fkCountry + ") REFERENCES " + Constants.tblCountry + 
  " (" + Constants.pkId + "));";

  public SQL(Context context) {
    super(context, Constants.dbName, null, /*DB Version*/1);
   }

  @Override
  public void onCreate(SQLiteDatabase db) throws SQLiteFullException {
    db.execSQL(createTableRegion);
    db.execSQL(createTableCountry);
    db.execSQL(createTableData);
   }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) throws SQLiteFullException {
//    db.execSQL("DROP TABLE IF EXISTS " + Constants.tblRegion);
//    db.execSQL("DROP TABLE IF EXISTS " + Constants.tblCountry);
//    db.execSQL("DROP TABLE IF EXISTS " + Constants.tblData);
//    onCreate(db);
   }
 }

class Database
 {
  private Database() {}
  private static SQLiteDatabase instance = null;

  public static SQLiteDatabase getInstance(Context context) {
    if(instance != null)
     return instance;
    SQLiteDatabase instance = new SQL(context).getWritableDatabase();
    return instance;
   }

  public static SQLiteDatabase getTestInstance() {
    if(instance == null) {
      String pathDB = Environment.getExternalStorageDirectory().toString() + "/AppProjects/catseyesDB/DBTest";
      instance = SQLiteDatabase.openDatabase(pathDB.toString(), null, SQLiteDatabase.OPEN_READWRITE);
     }
    return instance;
   }
 }
