package ie.webware.catseyes;

import android.app.*;
import android.os.*;
import android.widget.*;
import java.util.*;
import android.database.sqlite.*;
import android.util.*;
import java.io.*;
import android.database.*;


public class MainActivity extends Activity 
 {
  SQLiteDatabase db = Database.getTestInstance();

  ExpandableListAdapter listAdapter;
  ExpandableListView expListView;
  List<String> lstParent;
  HashMap<String, List<String>> lstChild;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    //playWithDatabase();
    //buildDatabase();

    // get the listview
    expListView = (ExpandableListView) findViewById(R.id.lvParent);

    // preparing list data
    prepareListData();

    listAdapter = new ExpandableListAdapter(this, lstParent, lstChild);

    // setting list adapter
    expListView.setAdapter(listAdapter);
   }

  /*
   * Preparing the list data
   */
  private void prepareListData() {
    lstParent = new ArrayList<String>();
    lstChild = new HashMap<String, List<String>>();

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
            List<String> lstCountry = new ArrayList<String>();
            String country = null;
            cCountry.moveToFirst();
            for(int i = 0; i < cCountry.getCount(); i++) {
              country = cCountry.getString(cCountry.getColumnIndex("location"));
              lstCountry.add(country);
              cCountry.moveToNext();
             }
            lstChild.put(lstParent.get(o), lstCountry);
          }
         }
       } catch(Exception e) {
        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
       } finally {
        //db.close();    
       }
     }
   }

  public void playWithDatabase() throws SQLiteFullException {
    SQLiteDatabase db = Database.getTestInstance();
    String continent = null;
    try {
      Cursor c = db.rawQuery("select continent from region", null);
      long l = c.getCount();
      c.moveToFirst();
      for(int i = 0; i < c.getCount() && c.getCount() > 0; i++) {
        continent = c.getString(c.getColumnIndex("Continent"));
        c.moveToNext();
       }

     } catch(Exception e) {
      Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
     } finally {
      db.close();    
     }
   }

  public boolean buildDatabase() {

    Thread thread = new Thread(new Runnable() {
       @Override 
       public void run() {
         try {
           new WorldOmeterDatabase(MainActivity.this);
          } catch(Exception e) {
           Log.d("MainActivity", e.toString());
          }
        }
      });

    Toast.makeText(MainActivity.this, "Initialising Worldometer Data", Toast.LENGTH_LONG).show();
    thread.start();
    try {
      thread.join(); // wait for thread to finish
     } catch(InterruptedException e) {
      Log.d(MainActivity.this.toString(), e.toString());
      return false;
     } finally {
      Log.d("General", "Thread complete");
     }
    Toast.makeText(MainActivity.this, "All done", Toast.LENGTH_LONG).show();
    return true;
   }
 }


