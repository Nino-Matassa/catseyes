package ie.webware.catseyes;

import android.app.*;
import android.os.*;
import android.widget.*;
import java.util.*;
import android.database.sqlite.*;
import android.util.*;
import java.io.*;
import android.database.*;


public class MainActivity extends Activity 
 {
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    ELARegion elaRegion = new ELARegion(MainActivity.this);
    //buildDatabase();
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


