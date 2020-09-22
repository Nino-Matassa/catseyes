package ie.webware.catseyes;

import android.app.*;
import android.database.sqlite.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import java.util.*;


public class MainActivity extends Activity {
  TextView view = null;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    view = findViewById(R.id.mainTextID);
    buildDatabase();
    new TVStatus(MainActivity.this);
   }

  @Override
  public void onBackPressed() {
    // Simulate object stack
    new ELV(MainActivity.this);
    // TODO: Implement this method
    //super.onBackPressed(); // if no objects on stack exit app
   }

  @Override
  protected void onDestroy() {
    Database.getInstance(MainActivity.this).close();
    super.onDestroy();
   }

  public void buildDatabase() {
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
   }
 }


