package ie.webware.catseyes;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.opengl.*;

//http://www.briandolhansky.com/blog/2013/7/11/snippets-android-async-progress
public class BusyBee extends AsyncTask<Void,Void,Void>
 {
  //View progressSymbol = null;
  private Context context = null;
  
  private ProgressDialog pd = null;
  public BusyBee(Context _context) {
    context = _context;
    pd = new ProgressDialog(context);
    pd.setMessage("Building");
    pd.show();
    
   }

  @Override
  protected Void doInBackground(Void[] p1) {
    try {
      Thread.sleep(1000);
     } catch (InterruptedException e) {
      e.printStackTrace();
     }
    return null;
   }

  @Override
  protected void onPreExecute() {
    // TODO: Implement this method
    //UI.progressSymbol.setVisibility(View.VISIBLE);
    //((Activity)context).findViewById(R.id.busyViewId).setVisibility(View.VISIBLE);
    //progressSymbol.setVisibility(View.VISIBLE);
   }

  @Override
  protected void onPostExecute(Void result) {
    // TODO: Implement this method
    //UI.progressSymbol.setVisibility(View.GONE);
    //((Activity)context).findViewById(R.id.busyViewId).setVisibility(View.GONE);
    pd.dismiss();
    //UI.progressSymbol.setVisibility(View.GONE);
   }
 }
 

  //View progressSymbol = null;
//    ((Activity)context).setContentView(R.layout.busy_view);
//    progressSymbol = ((Activity)context).findViewById(R.id.busyViewId);
//    progressSymbol.setVisibility(View.VISIBLE);
    //progressSymbol.setVisibility(View.GONE);

    //((Activity)context).findViewById(R.id.busyViewId).setVisibility(View.VISIBLE);
