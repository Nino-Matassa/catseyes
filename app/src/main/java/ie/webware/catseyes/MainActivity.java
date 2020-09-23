package ie.webware.catseyes;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.widget.*;


public class MainActivity extends Activity {
  TextView view = null;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    view = findViewById(R.id.mainTextID);
    buildDatabase();
    //new TVStatus(MainActivity.this);
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
    new Thread(new Runnable() {
       @Override 
       public void run() {
        //getMainLooper().prepare();
         try {
           new WorldOmeterDatabase(MainActivity.this);
          } catch(Exception e) {
           Log.d("MainActivity", e.toString());
          }
        }
      }).start();
    //thread.start();
//    try {
//     thread.join();
//    } catch (Exception e) {
//     Log.d("MainActivity", e.toString());
//    }
   }
 }


