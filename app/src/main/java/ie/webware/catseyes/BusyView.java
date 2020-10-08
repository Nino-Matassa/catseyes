package ie.webware.catseyes;

import android.app.*;
import android.content.*;
import android.view.*;

public class BusyView
 {
  private Context context = null;
  public View progressSymbol = null;
  public BusyView(Context _context) {
    context = _context;
    ((Activity)context).setContentView(R.layout.busy_view);
    progressSymbol = ((Activity)context).findViewById(R.id.busyViewId);
    progressSymbol.setVisibility(View.VISIBLE);
    //progressSymbol.setVisibility(View.GONE);
   }
 }
