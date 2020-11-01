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
  public static Stack<UIStackInfo> stack = new Stack<UIStackInfo>();
  private TextView view = null;
  public static Activity activity = null;
  private Thread thread = null;
  private boolean bUpdateDatabase = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    activity = this;
    setContentView(R.layout.main);
    view = findViewById(R.id.mainTextID);
    view.setText("SARS-COV-2 Statistical Analysis, Aug 7, 2020");
    
    // Check for new data
    //Handler jsonHandler = new Handler();
    //jsonHandler.postDelayed(new Runnable() {
       //public void run() {
         Thread jsonThread = new Thread(new Runnable() {
            @Override 
            public void run() {
              try {
                bUpdateDatabase = new WorldOmeterDatabase(MainActivity.this).readJSONfromURL();
               } catch(Exception e) {
                Log.d("MainActivity", e.toString());
               }
             }
           });
         jsonThread.start();
         try {
           jsonThread.join();
          } catch(InterruptedException e) {}
        //}
      //}, 500);

      if(bUpdateDatabase) {
        //Delay, to allow the ui to draw it self first
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
           public void run() {
             listener.WorldOmeterDatabasethreadFinished();
             buildDatabase(listener);
            }
          }, 500);
      } else {
        openTerra();
      }
    
   }

  private void openTerra() {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
       @Override
       public void run() {
         try {
           new UITerra(MainActivity.this, 0);
          } catch(Exception e) {
           Log.d("MainActivity", e.toString());
          }
        }     
      });
   }

  public interface WorldOmeterDatabaseListener
   {
    public void WorldOmeterDatabasethreadFinished();
   }

  public void buildDatabase(final WorldOmeterDatabaseListener listener) {
    if(!(thread == null))
    {
     openTerra();
     return;
    }
    thread = new Thread(new Runnable() {
       @Override 
       public void run() {
         try {
           new WorldOmeterDatabase(MainActivity.this).speedReadJSON();
          } catch(Exception e) {
           Log.d("MainActivity", e.toString());
          }
         listener.WorldOmeterDatabasethreadFinished();
        }
      });
    thread.start();
   }

  WorldOmeterDatabaseListener listener = new WorldOmeterDatabaseListener() {
    @Override
    public void WorldOmeterDatabasethreadFinished() {
      openTerra();
     }
   };

  @Override
  public void onBackPressed() {
    UIStackInfo infoPeek = stack.peek();
    if(stack.size() == 2)
     Toast.makeText(MainActivity.this, "Hit back button again to exit.", Toast.LENGTH_LONG);
    if(stack.empty() || stack.size() == 1 || infoPeek.UI == Constants.UITerra) {
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
 }
 

