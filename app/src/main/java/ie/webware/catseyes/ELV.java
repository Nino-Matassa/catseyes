//package ie.webware.catseyes;
//
//import android.app.*;
//import android.content.*;
//import android.database.*;
//import android.database.sqlite.*;
//import android.widget.*;
//import java.util.*;
//import android.view.*;
//
//public class ELV
// {
//  Context context = null;
//  SQLiteDatabase db = null;
//
//  ELA ela;
//  ExpandableListView elv;
//  List<String> lstParent;
//  HashMap<String, List<String>> hLstChild;
//
//  public ELV(Context _context) {
//    context = _context;
//    db = Database.getInstance(context);
//    ((Activity)context).setContentView(R.layout.elv_main);
//    elv = (ExpandableListView) ((Activity)context).findViewById(R.id.elvParent);
//    populateELV();
//    ela = new ELA(context, lstParent, hLstChild);
//    elv.setAdapter(ela);
//
//    elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//       @Override
//       public boolean onChildClick(ExpandableListView elv, View view, int iGroup, int iChild, long id) {
//         String continent = lstParent.get(iGroup);
//         String country = hLstChild.get(continent).get(iChild);
//         Cursor cParent = db.rawQuery("select id, continent from region where continent = '" + continent + "'", null);
//         Cursor cChild = db.rawQuery("select id, location, country_code from country where location = '" + country + "'", null);
//         cParent.moveToFirst(); 
//         cChild.moveToFirst();
//         long parentId = cParent.getInt(cParent.getColumnIndex("ID"));
//         long childId = cChild.getInt(cChild.getColumnIndex("ID"));
//         continent = cParent.getString(cParent.getColumnIndex("CONTINENT"));
//         country = cChild.getString(cChild.getColumnIndex("location"));
//         String countryCode = cChild.getString(cChild.getColumnIndex("COUNTRY_CODE"));
//
////         LVKeyValue lvKeyValue = new LVKeyValue();
////         lvKeyValue.key.add(parentId);
////         lvKeyValue.value.add(continent);
////         lvKeyValue.key.add(childId);
////         lvKeyValue.value.add(country);
////         lvKeyValue.key.add(childId);
////         lvKeyValue.value.add(countryCode);
////
////         new LV(context, lvKeyValue);
//         new TVCountry(context, childId);
//         //}
//         return false; // default as false?
//        }
//      });
//   }
//
//  private void populateELV() {
//    lstParent = new ArrayList<String>();
//    hLstChild = new HashMap<String, List<String>>();
//
//    Cursor cRegion = db.rawQuery("select continent from region", null);
//     {
//      try {       
//        long l = cRegion.getCount();
//        cRegion.moveToFirst(); // add region to parent
//        for(int I = 0; I < cRegion.getCount(); I++) {
//          String continent = cRegion.getString(cRegion.getColumnIndex("CONTINENT"));
//          lstParent.add(continent);
//          cRegion.moveToNext();
//         }
//
//         { // add country to region
//          for(int o = 0; o < lstParent.size(); o++) {
//            String continent = lstParent.get(o).toString();
//            Cursor cCountry =  
//             db.rawQuery("select location from country join region on country.fk_region = region.id where region.continent = '" + 
//                         continent + "' order by location asc", null);
//            List<String> lstRegionCountry = new ArrayList<String>();
//            String country = null;
//            cCountry.moveToFirst();
//            for(int i = 0; i < cCountry.getCount(); i++) {
//              country = cCountry.getString(cCountry.getColumnIndex("location"));
//              lstRegionCountry.add(country);
//              cCountry.moveToNext();
//             }
//            hLstChild.put(lstParent.get(o), lstRegionCountry);
//           }
//         }
//       } catch(Exception e) {
//        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
//       } finally {
//        //db.close();    
//       }
//     }
//   }
// }
