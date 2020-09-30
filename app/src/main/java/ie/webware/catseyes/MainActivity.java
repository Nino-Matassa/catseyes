package ie.webware.catseyes;

import android.app.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import java.util.*;


public class MainActivity extends Activity
 {
  static Stack<TVStackInfo> stack = new Stack<TVStackInfo>();
  TextView view = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    view = findViewById(R.id.mainTextID);
    buildDatabase();
    try {
      new TVTerra(MainActivity.this, 0);
     } catch(Exception e) {
      Log.d("MainActivity", e.toString());
     }
   }

  @Override
  public void onBackPressed() {
    if(stack.empty()) {
      super.onBackPressed();
     } else {
      TVStackInfo info = stack.pop();
      switch(info.TV) {
        case "TVTerra":
         new TVTerra(info.context, info.id);
         break;
        case "TVRegion":
          new TVRegion(info.context, info.id);
         break;
        case "TVCountry":
          new TVCountry(info.context, info.id);
         break;
        case "TVData":
         // Error
         break;
        default:
       }
     }
    // Simulate object stack
    //new ELV(MainActivity.this);
    //new TVTerra(MainActivity.this, 0);
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


