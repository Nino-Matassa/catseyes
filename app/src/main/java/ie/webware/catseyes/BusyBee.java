package ie.webware.catseyes;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.opengl.*;
import android.util.*;

public class BusyBee extends AsyncTask<Void, Void, Void>
 {

  @Override
  private Context context = null;

  private ProgressDialog pd = null;
  public BusyBee(Context _context) {
    context = _context;
    pd = new ProgressDialog(MainActivity.activity);
   }

  @Override
  protected Void doInBackground(Void[] p1) {
     try {
      Thread.sleep(500);
     } catch(InterruptedException e) {}
    return null;
   }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    pd.show();
    pd.setMessage("Building");
   }

  @Override
  protected void onPostExecute(Void result) {
    super.onPostExecute(result);
    pd.dismiss();
   }
 }
 
 

  //View progressSymbol = null;
//    ((Activity)context).setContentView(R.layout.busy_view);
//    progressSymbol = ((Activity)context).findViewById(R.id.busyViewId);
//    progressSymbol.setVisibility(View.VISIBLE);
    //progressSymbol.setVisibility(View.GONE);

    //((Activity)context).findViewById(R.id.busyViewId).setVisibility(View.VISIBLE);
