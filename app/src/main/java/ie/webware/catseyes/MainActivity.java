package ie.webware.catseyes;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.util.*;


public class MainActivity extends Activity
 {
  static Stack<UIStackInfo> stack = new Stack<UIStackInfo>();
  TextView view = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    view = findViewById(R.id.mainTextID);
    view.setText("SARS-COV-2 Statistical Analysis");
    buildDatabase();
    try {
      new UITerra(MainActivity.this, 0);
     } catch(Exception e) {
      Log.d("MainActivity", e.toString());
     }
   }

  @Override
  public void onBackPressed() {
    if(stack.size() == 2)
     WorldOmeterDatabase.toast("Hit back button again to exit.", Toast.LENGTH_LONG, MainActivity.this);
    if(stack.empty()) {
      super.onBackPressed();
     } else {
      stack.pop();
      UIStackInfo info = stack.pop();
      switch(info.UI) {
         case Constants.UITerra:
         new UITerra(info.context, info.id);
         break;
        case Constants.UIContinent:
         new UIContinents(info.context, info.id);
         break;
        case Constants.UIRegion:
         new UIRegion(info.context, info.id);
         break;
        case Constants.UICountry:
         new UICountry(info.context, info.id);
         break;
        case Constants.UIData:
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
