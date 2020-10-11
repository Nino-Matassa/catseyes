package ie.webware.catseyes;

import android.app.*;
import android.content.*;
import android.view.*;

public class BusyView
 {
//    context = _context;
//    ((Activity)context).setContentView(R.layout.busy_view);
//    progressSymbol = ((Activity)context).findViewById(R.id.busyViewId);
//    progressSymbol.setVisibility(View.VISIBLE);
  
  private static ProgressDialog pd = null;

  public static ProgressDialog busyBee(Context context, String msg, Boolean bShow) {
    if(pd == null) {
      pd = new ProgressDialog(context);
     }
    pd.setMessage(msg);
    if(bShow)
     pd.show();
    else
     pd.hide();
    return pd;
   }
 }
 


