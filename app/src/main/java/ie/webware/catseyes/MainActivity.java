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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    activity = this;
    setContentView(R.layout.main);
    view = findViewById(R.id.mainTextID);
    view.setText("SARS-COV-2 Statistical Analysis");
    
//    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
//     new BusyBee().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//    else
//     new BusyBee().execute();
//    
    //Delay, to allow the ui to draw it self first
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
       public void run() {
         buildDatabase(listener);
        }
      }, 500);
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
   
  public interface WorldOmeterDatabaseListener{
    public void WorldOmeterDatabasethreadFinished();
   }

  public void buildDatabase(final WorldOmeterDatabaseListener listener){
    new Thread(new Runnable() {
       @Override 
       public void run() {
         try {
           new WorldOmeterDatabase(MainActivity.this);
          } catch(Exception e) {
           Log.d("MainActivity", e.toString());
          }
         listener.WorldOmeterDatabasethreadFinished();
        }
      }).start();
   }
  WorldOmeterDatabaseListener listener = new WorldOmeterDatabaseListener() {

    @Override
    public void WorldOmeterDatabasethreadFinished() {
      openTerra();
     }
   };
 }
 
//class BusyBee extends AsyncTask<Void, Void, Void>
// {
//  private ProgressDialog pd;
//
//  public BusyBee() {
//    pd = new  ProgressDialog(MainActivity.activity);
//   }
//
//  @Override
//  protected void onPreExecute() {
//    pd.setMessage("Generating database...");
//    pd.show();
//    super.onPreExecute();
//   }
//
//  @Override
//  protected void onPostExecute(Void result) {
//    pd.hide();
//    super.onPostExecute(result);
//   }
//
//  @Override
//  protected Void doInBackground(Void[] p1) {
//    try {
//      Thread.sleep(500);
//     } catch(InterruptedException e) {}
//    return null;
//   }
// }
// 

