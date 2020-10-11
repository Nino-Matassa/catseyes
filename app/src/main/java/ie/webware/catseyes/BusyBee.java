package ie.webware.catseyes;

import android.app.*;
import android.content.*;
import android.os.*;

//https://www.journaldev.com/9708/android-asynctask-example-tutorial

public class BusyBee extends AsyncTask<Void,Void,UI >
 {

  private Context context = null;
  private ProgressDialog pd = null;

  public BusyBee(Context _context) {
   context = _context;
   pd = new ProgressDialog(context);
   pd.setMessage("Building");
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
    pd.show();
   }

  @Override
  protected void onPostExecute(UI result) {
    // TODO: Implement this method
    super.onPostExecute(result);
    pd.dismiss();
   }
 }
 
//    context = _context;
//    ((Activity)context).setContentView(R.layout.busy_view);
//    progressSymbol = ((Activity)context).findViewById(R.id.busyViewId);
//    progressSymbol.setVisibility(View.VISIBLE);

// https://stackoverflow.com/questions/3343490/progressdialog-working-in-thread-on-android
