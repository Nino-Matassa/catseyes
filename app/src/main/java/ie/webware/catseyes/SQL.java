package ie.webware.catseyes;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;

public class SQL extends SQLiteOpenHelper
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
	super(context, /*Constants.dbName*/null, null, /*DB Version*/1);
   }

  @Override
  public void onCreate(SQLiteDatabase db) {
    try {
      db.execSQL(createTableRegion);
      db.execSQL(createTableCountry);
      db.execSQL(createTableData);
     } catch(SQLException e) {
      String s = e.toString();				
     }
   }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	db.execSQL("DROP TABLE IF EXISTS " + Constants.tblRegion);
	db.execSQL("DROP TABLE IF EXISTS " + Constants.tblCountry);
	db.execSQL("DROP TABLE IF EXISTS " + Constants.tblData);
	onCreate(db);
   }

  private static SQLiteDatabase instance = null;

  public SQLiteDatabase getInstance() {
	if(instance != null)
	 return instance;
	SQLiteDatabase instance = getWritableDatabase();
	return instance;
   }
 }
		
