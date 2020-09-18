package ie.webware.catseyes;

import android.app.*;
import android.os.*;
import android.widget.*;
import java.util.*;
import android.database.sqlite.*;
import android.util.*;
import java.io.*;
import android.database.*;
import android.widget.SearchView.*;


public class MainActivity extends Activity  
 {
  SQLiteDatabase db = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    //if(!Database.databaseExists(MainActivity.this))
     buildDatabase();
    // Ok, only the first widget in main is available, right now its ELV xor LV
    new ELV(MainActivity.this);
    //new LV(MainActivity.this, new String[]{"Terra"});
   }

  @Override
  public void onBackPressed() {
    // if not empty pull from stack otherwise call super.onBackPressed();
    new ELV(MainActivity.this);
    // TODO: Implement this method
    //super.onBackPressed();
   }

  @Override
  protected void onDestroy() {
    //db.close();
    super.onDestroy();
   }

  public boolean buildDatabase() {

    Thread thread = new Thread(new Runnable() {
       @Override 
       public void run() {
         try {
           new WorldOmeterDatabase(MainActivity.this);
          } catch(Exception e) {
           Log.d("MainActivity", e.toString());
          }
        }
      });

    Toast.makeText(MainActivity.this, "Initialising Worldometer Data", Toast.LENGTH_LONG).show();
    thread.start();
    try {
      thread.join(); // wait for thread to finish
     } catch(InterruptedException e) {
      Log.d(MainActivity.this.toString(), e.toString());
      return false;
     } finally {
      Log.d("General", "Thread complete");
     }
    Toast.makeText(MainActivity.this, "All done", Toast.LENGTH_LONG).show();
    return true;
   }
 }


