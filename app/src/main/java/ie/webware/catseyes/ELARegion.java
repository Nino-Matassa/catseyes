package ie.webware.catseyes;

import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.widget.*;
import java.util.*;
import android.view.*;

public class ELARegion
{
  Context context = null;
  SQLiteDatabase db = Database.getTestInstance();

  ELA elaByRegion;
  ExpandableListView elvByRegion;
  List<String> lstRegion;
  HashMap<String, List<String>> hLstCountry;
  
  public ELARegion(Context _context) {
    context = _context;
    elvByRegion = (ExpandableListView) ((Activity)context).findViewById(R.id.elvParent);
    populateELV();
    elaByRegion = new ELA(context, lstRegion, hLstCountry);
    elvByRegion.setAdapter(elaByRegion);
    
    elvByRegion.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
       @Override
       public boolean onChildClick(ExpandableListView elv, View view, int iGroup, int iChild, long id) {
         String continent = lstRegion.get(iGroup);
         String country = hLstCountry.get(continent).get(iChild);
         Cursor p = db.rawQuery("select id, continent from region where continent = '" + continent + "'", null);
         Cursor c = db.rawQuery("select id, location from country where location = '" + country + "'", null);
         p.moveToFirst(); 
         c.moveToFirst();
         long lp = p.getInt(p.getColumnIndex("ID"));
         long lc = c.getInt(c.getColumnIndex("ID"));
         continent = p.getString(p.getColumnIndex("Continent"));
         country = c.getString(c.getColumnIndex("location"));
         return true; // default as false?
        }
    });
  }
  
  private void populateELV() {
    lstRegion = new ArrayList<String>();
    hLstCountry = new HashMap<String, List<String>>();

    Cursor cRegion = db.rawQuery("select continent from region", null);
     {
      try {       
        long l = cRegion.getCount();
        cRegion.moveToFirst(); // add region to parent
        for(int I = 0; I < cRegion.getCount(); I++) {
          String continent = cRegion.getString(cRegion.getColumnIndex("Continent"));
          lstRegion.add(continent);
          cRegion.moveToNext();
         }

         { // add country to region
          for(int o = 0; o < lstRegion.size(); o++) {
            String continent = lstRegion.get(o).toString();
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
            hLstCountry.put(lstRegion.get(o), lstRegionCountry);
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
