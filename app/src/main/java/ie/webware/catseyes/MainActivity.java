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
    if(stack.size() == 2)
     Toast.makeText(MainActivity.this, "Hit back button again to exit", Toast.LENGTH_LONG).show();
    if(stack.empty()) {
      super.onBackPressed();
     } else {
      stack.pop();
      TVStackInfo info = stack.pop();
      switch(info.TV) {
        case "TVTerra":
         new TVTerra(info.context, info.id);
         break;
        case "TVContinents":
         new TVContinents(info.context, info.id);
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


