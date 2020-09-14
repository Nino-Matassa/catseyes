package ie.webware.catseyes;
import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import java.sql.*;

public class LV
 {
  Context context = null;
  String[] values = null;
  ListView listView = null;
  LVKeyValue lvKeyValue = null;

  public LV(Context _context, LVKeyValue _lvKeyValue) {
    context = _context;
    values = _lvKeyValue.saValue();
    lvKeyValue = _lvKeyValue;
    
    values = expandLVKeyValue(); // LVKeyValue already contains Region, Country & CountryCode.
    
    try {
      ((Activity)context).setContentView(R.layout.lv_main);
      listView = (ListView) ((Activity)context).findViewById(R.id.lstView);
      ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.lv_row, R.id.lstRow, values); // view & view id = of list_row.xml
      listView.setAdapter(adapter);

      listView.setOnItemClickListener(new OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           String item = (String)listView.getItemAtPosition(position);
           Long lId = lvKeyValue.slKey()[position];
           Toast.makeText(context, lId + " " + item, Toast.LENGTH_SHORT).show();
          }
        });

        } catch(Exception e) {
      String s = e.toString();
     }
   }
   
   private String[] expandLVKeyValue() throws SQLiteFullException {
    SQLiteDatabase db = Database.getInstance(context);
     String sql = "select data.id, date, new_cases from data join country on data.fk_country = country.id where fk_country = # and new_cases > 0 order by date desc";
     Long countryId = lvKeyValue.key.get(1); // gauranteed to be the country id
     sql = sql.replace("#", countryId.toString());
     Cursor cData = db.rawQuery(sql, null);
     cData.moveToFirst();
     long id = 0;
     String date = null;
     Double nCases = 0.0;
     for(int i = 0; i < cData.getCount(); i++) {
      id = cData.getLong(cData.getColumnIndex("ID"));
      date = cData.getString(cData.getColumnIndex("date"));
      nCases = cData.getDouble(cData.getColumnIndex("new_cases"));
      lvKeyValue.key.add(id);
      String row = date + ": New Cases# " + nCases.toString();
      lvKeyValue.value.add(row);
      cData.moveToNext();
     }
     cData.close();
     return lvKeyValue.saValue();
   }
 }
