package ie.webware.catseyes;

import android.app.*;
import android.database.sqlite.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import java.util.*;


public class MainActivity extends Activity {
  SQLiteDatabase db = null;
  TextView view = null;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    view = findViewById(R.id.mainTextID);
    
    DBStatus.addStatusListener(new StatusChangedListener() {
       @Override
       public void onStatusChanged() {
         view.setText(DBStatus.getStatus());
        }
      });
      
    buildDatabase();
    //if(Database.isExistingDatabase)
    //new ELV(MainActivity.this);
    //new LV(MainActivity.this, new String[]{"Terra"});
    //view = findViewById(R.id.mainTextID);
    //if(!Database.databaseExists(MainActivity.this))
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

  public void buildDatabase() {

    Thread thread = new Thread(new Runnable() {
     //runOnUiThread(new Runnable() {
       @Override 
       public void run() {
         try {
           new WorldOmeterDatabase(MainActivity.this);
          } catch(Exception e) {
           Log.d("MainActivity", e.toString());
          }
        }
      });

    thread.start();
//    try {
//      thread.join(); // wait for thread to finish
//     } catch(InterruptedException e) {
//      Log.d(MainActivity.this.toString(), e.toString());
//     } finally {
//      Log.d("General", "Thread complete");
//     }
   }
 }


