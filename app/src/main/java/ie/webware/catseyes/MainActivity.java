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
    String displayText = "\n\n\n\n\n\n\n\n\nSARS-COV-2 Statistical Analysis, Aug 7, 2020\n" + 
     "Nino Matassa MBCS\n" +
     "https://github.com/Nino-Matassa/catseyes\n";
    view.setText(displayText);
    //Delay, to allow the ui to draw it self first
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
       public void run() {
         buildDatabase(owidListener);
        }
      }, 500);
   }

  public interface OWIDListener { public void OWIDThreadFinished(); }
  OWIDListener owidListener = new OWIDListener() {
    @Override
    public void OWIDThreadFinished() {
      openTerra();
     }
   };

  private Thread thread = null;
  public void buildDatabase(final OWIDListener listener) {
    if(!(thread == null)) {
      return;
     }
    thread = new Thread(new Runnable() {
       @Override 
       public void run() {
         try {
           new OWIDDatabase(MainActivity.this);
          } catch(Exception e) {
           Log.d("MainActivity", e.toString());
          }
         listener.OWIDThreadFinished();
        }
      });
    thread.start();
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
        case Constants.UICountryData:
         // Error
         break;
        default:
       }
     }
   }
 }
 

