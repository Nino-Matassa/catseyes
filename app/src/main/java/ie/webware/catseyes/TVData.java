package ie.webware.catseyes;

import android.content.*;
import android.database.*;
import android.util.*;
import java.text.*;
import java.util.*;

public class TVData extends TV
 {
  Context context = null;
  long id = 0;
  String field = null;
  DecimalFormat formatter = null;

  public TVData(Context _context, long _id, String _field) {
    super(_context, _id);
    context = _context;
    id = _id;
    field = _field;
    formatter = new DecimalFormat("#,###.##");

    populateTableData();
    String fieldDescription = null;
    if(field.equals("new_cases"))
     fieldDescription = "New Cases";
	 else
	  fieldDescription = field;
    setHeader("Recent History Of...", fieldDescription);
   }

  private void populateTableData() {
    ArrayList<TableKeyValue> tkvs = new ArrayList<TableKeyValue>();
    String sql = "select date, $ from data where fk_country = # order by date desc".replace("$", field).replace("#", String.valueOf(id));
    Cursor cursor = db.rawQuery(sql, null);
    cursor.moveToFirst();
    do {
      TableKeyValue tkv = new TableKeyValue();
      tkv.subClass = "TVData";
      tkv.key = cursor.getString(cursor.getColumnIndex("date"));
      //try { 
        //tkv.key = new SimpleDateFormat("yyyy-MM-dd").parse(tkv.key).toString();
        //String[] arrDate = tkv.key.split(" ");
        //tkv.key = arrDate[0] + " " + arrDate[1] + " " + arrDate[2] + " " + arrDate[5];
       //} catch(ParseException e) {
        //Log.d("TVData", e.toString());
       //}
      tkv.value = String.valueOf(formatter.format(cursor.getLong(cursor.getColumnIndex(field))));
      tkvs.add(tkv);
     } while(cursor.moveToNext());
    setTableLayout(getTableRows(tkvs));
   }
 }
