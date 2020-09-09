package ie.webware.catseyes;

import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.widget.*;
import java.util.*;
import android.view.*;

public class ELV
{
  Context context = null;
  SQLiteDatabase db = null;

  ELA ela;
  ExpandableListView elv;
  List<String> lstParent;
  HashMap<String, List<String>> hLstChild;
  
  public ELV(Context _context) {
    context = _context;
    db = Database.getInstance(context);
    elv = (ExpandableListView) ((Activity)context).findViewById(R.id.elvParent);
    populateELV();
    ela = new ELA(context, lstParent, hLstChild);
    elv.setAdapter(ela);
    
    elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
       @Override
       public boolean onChildClick(ExpandableListView elv, View view, int iGroup, int iChild, long id) {
         String continent = lstParent.get(iGroup);
         String country = hLstChild.get(continent).get(iChild);
         Cursor p = db.rawQuery("select id, continent from region where continent = '" + continent + "'", null);
         Cursor c = db.rawQuery("select id, location, country_code from country where location = '" + country + "'", null);
         p.moveToFirst(); 
         c.moveToFirst();
         long lp = p.getInt(p.getColumnIndex("ID"));
         long lc = c.getInt(c.getColumnIndex("ID"));
         continent = p.getString(p.getColumnIndex("Continent"));
         country = c.getString(c.getColumnIndex("location"));
         String countryCode = c.getString(c.getColumnIndex("Country_Code"));
         Toast.makeText(context, continent + " + " + country, Toast.LENGTH_LONG).show();
         { // Populate list view
          String[] values = new String[] {
           continent,
           country,
           countryCode
          };
//          ((Activity)context).setContentView(R.layout.list_data);
//          ListView lstData = (ListView)((Activity)context).findViewById(R.id.dlData);
//          ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.list_data, R.id.dlData, values);
//          lstData.setAdapter(adapter);
         }
         return false; // default as false?
        }
    });
  }
  
  private void populateELV() {
    lstParent = new ArrayList<String>();
    hLstChild = new HashMap<String, List<String>>();

    Cursor cRegion = db.rawQuery("select continent from region", null);
     {
      try {       
        long l = cRegion.getCount();
        cRegion.moveToFirst(); // add region to parent
        for(int I = 0; I < cRegion.getCount(); I++) {
          String continent = cRegion.getString(cRegion.getColumnIndex("Continent"));
          lstParent.add(continent);
          cRegion.moveToNext();
         }

         { // add country to region
          for(int o = 0; o < lstParent.size(); o++) {
            String continent = lstParent.get(o).toString();
            Cursor cCountry =  
             db.rawQuery("select location from country join region on country.fk_region = region.id where region.continent = '" + 
                         continent + "' order by location asc", null);
            List<String> lstRegionCountry = new ArrayList<String>();
            String country = null;
            cCountry.moveToFirst();
            for(int i = 0; i < cCountry.getCount(); i++) {
              country = cCountry.getString(cCountry.getColumnIndex("location"));
              lstRegionCountry.add(country);
              cCountry.moveToNext();
             }
            hLstChild.put(lstParent.get(o), lstRegionCountry);
           }
         }
       } catch(Exception e) {
        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
       } finally {
        //db.close();    
       }
     }
   }
}
