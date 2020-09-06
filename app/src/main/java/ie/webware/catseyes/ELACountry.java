package ie.webware.catseyes;

import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.widget.*;
import ie.webware.catseyes.*;
import java.util.*;

public class ELACountry
{
  Context context = null;
  SQLiteDatabase db = Database.getTestInstance();

  ELA elaByCountry;
  ExpandableListView elvByCountry;
  List<String> lstCountry;
  HashMap<String, List<String>> hLstData;
  long country = 0;

  public ELACountry(Context _context, long _country) {
    country = _country;
    context = _context;
    elvByCountry = (ExpandableListView) ((Activity)context).findViewById(R.id.elvParent);
    populateELV();
    elaByCountry = new ELA(context, lstCountry, hLstData);
    elvByCountry.setAdapter(elaByCountry);
   }

  private void populateELV() {
    lstCountry = new ArrayList<String>();
    hLstData = new HashMap<String, List<String>>();
   }
}
