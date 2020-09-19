package ie.webware.catseyes;

import android.app.*;
import android.database.sqlite.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;


public class MainActivity extends Activity  
 {
  SQLiteDatabase db = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    // Ok, only the first widget in main is available, right now its ELV xor LV
    //new ELV(MainActivity.this);
    //new LV(MainActivity.this, new String[]{"Terra"});
    final TextView vInformation = findViewById(R.id.mainTextID);
    vInformation.setText("Checking for new data...");
    
    DatabaseStatus.addStatusListener(new StatusChangedListener() {
       @Override
       public void onStatusChanged() {
         vInformation.setText(DatabaseStatus.getStatus());
        }
      });
    //if(!Database.databaseExists(MainActivity.this))
   buildDatabase();
  
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

    thread.start();
//    try {
//      thread.join(); // wait for thread to finish
//     } catch(InterruptedException e) {
//      Log.d(MainActivity.this.toString(), e.toString());
//      return false;
//     } finally {
//      Log.d("General", "Thread complete");
//     }
    return true;
   }
 }


