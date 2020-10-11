package ie.webware.catseyes;

import android.app.*;
import android.content.*;
import android.os.*;

public class BusyBee extends AsyncTask<Void,Void,UI >
 {

  private Context context = null;
  private static ProgressDialog pd = null;

  public BusyBee(Context _context) {
   context = _context;
   pd = new ProgressDialog(context);
   pd.show();
  }

  @Override
  protected UI doInBackground(Void[] p1) {
    // TODO: Implement this method
    return null;
   }

  @Override
  protected void onPreExecute() {
    // TODO: Implement this method
    super.onPreExecute();
    pd.setMessage("Building");
   }

  @Override
  protected void onPostExecute(UI result) {
    // TODO: Implement this method
    super.onPostExecute(result);
    pd.dismiss();
   }
 }
 
//    ((Activity)context).setContentView(R.layout.busy_view);
//    progressSymbol = ((Activity)context).findViewById(R.id.busyViewId);
//    progressSymbol.setVisibility(View.VISIBLE);

// https://stackoverflow.com/questions/3343490/progressdialog-working-in-thread-on-android
