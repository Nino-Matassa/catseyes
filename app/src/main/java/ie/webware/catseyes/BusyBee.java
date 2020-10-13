package ie.webware.catseyes;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.opengl.*;

public class BusyBee extends AsyncTask<Void,Void,Void>
 {
  //View progressSymbol = null;
  private Context context = null;
  
  private ProgressDialog pd = null;
  public BusyBee(Context _context) {
    context = _context;
   }

  @Override
  protected Void doInBackground(Void[] p1) {
    // TODO: Implement this method
    return null;
   }

  @Override
  protected void onPreExecute() {
    // TODO: Implement this method
    //UI.progressSymbol.setVisibility(View.VISIBLE);
    super.onPreExecute();
    //((Activity)context).findViewById(R.id.busyViewId).setVisibility(View.VISIBLE);
    pd = new ProgressDialog(context);
    pd.setMessage("Building");
    pd.show();
    //progressSymbol.setVisibility(View.VISIBLE);
   }

  @Override
  protected void onPostExecute(Void result) {
    // TODO: Implement this method
    //UI.progressSymbol.setVisibility(View.GONE);
    super.onPostExecute(result);
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
