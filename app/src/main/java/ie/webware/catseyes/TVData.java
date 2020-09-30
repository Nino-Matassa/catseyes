package ie.webware.catseyes;

import android.content.*;
import android.database.*;
import android.util.*;
import java.text.*;
import java.util.*;

public class TVData extends TV
 {
  Context context = null;
  long idData = 0;
  String field = null;
  DecimalFormat formatter = null;

  public TVData(Context _context, long _idData, String _field) {
    super(_context, _idData);
    context = _context;
    idData = _idData;
    field = _field;
    formatter = new DecimalFormat("#,###.##");

    populateTableData();
    String fieldDescription = null;
    switch(field) {
      case "new_cases":
       fieldDescription = "New Cases";
       break;
      case "new_deaths":
       fieldDescription = "New Deaths";
       break;
      case "total_cases":
       fieldDescription = "Total Cases";
       break;
      case "total_deaths":
       fieldDescription = "Total Deaths";
       break;
      case "total_cases_per_million":
       fieldDescription = "Cases/Million";
       break;
      case "total_deaths_per_million":
       fieldDescription = "Deaths/Million";
       break;
      case "new_cases_per_million":
       fieldDescription = "Total Cases/Million";
       break;
      case "new_deaths_per_million":
       fieldDescription = "Total Deaths/Million";
       break;
      default:
     }
    setHeader("Recent History Of...", fieldDescription);
    //stackRegister("TVData", context, idData);
   }

  private void populateTableData() {
    ArrayList<TableKeyValue> tkvs = new ArrayList<TableKeyValue>();
    String sql = "select date, $ from data where fk_country = # order by date desc".replace("$", field).replace("#", String.valueOf(idData));
    Cursor cursor = db.rawQuery(sql, null);
    cursor.moveToFirst();
    do {
      TableKeyValue tkv = new TableKeyValue();
      tkv.subClass = "TVData";
      tkv.key = cursor.getString(cursor.getColumnIndex("date"));
      tkv.value = String.valueOf(formatter.format(cursor.getLong(cursor.getColumnIndex(field))));
      tkvs.add(tkv);
     } while(cursor.moveToNext());
    setTableLayout(getTableRows(tkvs));
   }
 }
