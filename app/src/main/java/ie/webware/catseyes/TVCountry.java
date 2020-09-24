package ie.webware.catseyes;

import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.util.*;
import android.widget.*;
import java.text.*;
import java.util.*;

class TVCountry
 {
  private Context context = null;
  private TableLayout tableLayout = null;
  private long countryId = 0;
  SQLiteDatabase db = null;
  
  public TVCountry(Context _context, long _countryId) {
    context = _context;
    countryId = _countryId;
    db = Database.getInstance(context);

    ((Activity)context).setContentView(R.layout.table_layout);
    tableLayout = (TableLayout) ((Activity)context).findViewById(R.id.layoutTable);

    populateTableCountry();
   }
  class TableKeyValue {
   String key = null;
   String value = null;
  }
  
  public ArrayList<TableRow> getTableRows(ArrayList<TableKeyValue> tkvs) {
    ArrayList<TableRow> tableRows = new ArrayList<TableRow>();
    for(TableKeyValue tkv: tkvs) {
      TableRow tableRow = new TableRow(context);
      LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
      tableRow.setLayoutParams(tableRowParams);

      TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
      cellParams.weight = 9;
      TextView textViewKey = new TextView(context);
      textViewKey.setTextSize(18);
      TextView textViewValue = new TextView(context);
      textViewValue.setTextSize(18);
      textViewKey.setLayoutParams(cellParams);
      textViewValue.setLayoutParams(cellParams);
      textViewKey.setText(tkv.key);
      textViewValue.setText(tkv.value);
      tableRow.addView(textViewKey);
      tableRow.addView(textViewValue);
      tableRows.add(tableRow);
    }
    
    return tableRows;
   }
   
   public void setTableLayout(ArrayList<TableRow> tableRows) {
    for(TableRow tableRow: tableRows) {
       tableLayout.addView(tableRow);
    }
   }

  public void populateTableCountry() {
    ArrayList<TableKeyValue> tkvs = new ArrayList<TableKeyValue>();
    String sql = "select COUNTRY_CODE, continent, location from country where ID = #".replace("#", String.valueOf(countryId));
    Cursor cursor = db.rawQuery(sql, null);
    cursor.moveToFirst();
    TableKeyValue tkv = new TableKeyValue();
    tkv.key = "Country Code"; tkv.value = (cursor.getString(cursor.getColumnIndex("COUNTRY_CODE"))); tkvs.add(tkv); tkv = new TableKeyValue();
    tkv.key = "Region"; tkv.value = cursor.getString(cursor.getColumnIndex("continent")); tkvs.add(tkv); tkv = new TableKeyValue();
    tkv.key = "Country"; tkv.value = cursor.getString(cursor.getColumnIndex("location")); tkvs.add(tkv); tkv = new TableKeyValue();
    sql = "select sum(new_cases) as total_cases, total_deaths, date from data where fk_country = # order by total_cases desc limit 1".replace("#", String.valueOf(countryId));
    cursor = db.rawQuery(sql, null);
    cursor.moveToFirst();
    tkv.key = "Total Cases"; tkv.value = String.valueOf(cursor.getLong(cursor.getColumnIndex("total_cases"))); tkvs.add(tkv); tkv = new TableKeyValue();
    tkv.key = "Total Deaths"; tkv.value = String.valueOf(cursor.getLong(cursor.getColumnIndex("total_deaths"))); tkvs.add(tkv); tkv = new TableKeyValue();
    tkv.key = "Last Updated"; tkv.value = cursor.getString(cursor.getColumnIndex("date"));
    try {
      Date d = new SimpleDateFormat("yyyy-MM-dd").parse(tkv.value);
      tkv.value = d.toString();
      String[] arrDate = tkv.value.split(" ");
      tkv.value = arrDate[0] + " " + arrDate[1] + " " + arrDate[2] + " " + arrDate[5];
     } catch(ParseException e) {
      Log.d("TVCountry", e.toString());
     } finally {
       tkvs.add(tkv);
     }
     setTableLayout(getTableRows(tkvs));
   }
 }
