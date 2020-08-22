package ie.webware.catseyes;
import android.database.sqlite.*;
import android.content.*;
import android.app.*;
import android.database.*;

public class SQL extends SQLiteOpenHelper
	{
		// Creating table sql
		private String createTableRegion = "create table " + Constants.tblRegion + 
			"(" + Constants.tblPrimaryKey + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
			Constants.colCountry + " TEXT NOT NULL);";
		// "create table Region(ID INTEGER PRIMARY KEY AUTOINCREMENT, Country TEXT NOT NULL);"
		
		private String createTableCountry = "create table " + Constants.tblCountry + 
			"(" + Constants.tblPrimaryKey + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"FOREIGN KEY (" + Constants.tblFKRegion + ") REFERENCES " + Constants.tblRegion + 
			" (" + Constants.tblPrimaryKey +"));";
		// "create table Country(ID INTEGER PRIMARY KEY AUTOINCREMENT, FOREIGN KEY (FK_REGION) REFERENCES Region (ID));"
		//FOREIGN KEY(customer_id) REFERENCES customers(id)
		
		private SQL(Context context) {
			// passing null for the database name causes it to be created in memory
			super(context, /*Constants.dbName*/null, null, /*DB Version*/1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) { // issue, this function is been called twice
			try {
				db.execSQL(createTableRegion);
				db.execSQL(createTableCountry); // not working...
			} catch (SQLException e) {
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
		
		private static SQL instance = null;
		public static SQL getInstance(Context context){
				if(instance == null)
					instance = new SQL(context);
				return instance;
			}

	}
