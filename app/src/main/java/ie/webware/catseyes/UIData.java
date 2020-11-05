package ie.webware.catseyes;

import android.content.*;
import android.database.*;
import android.util.*;
import java.text.*;
import java.util.*;
import android.widget.*;
import android.os.*;

public class UIData extends UI
 {
  Context context = null;
  long idData = 0;
  String field = null;
  String country = null;
  DecimalFormat formatter = null;

  public UIData(Context _context, long _idData, String _field) {
    super(_context, _idData);
    context = _context;
    idData = _idData;
    field = _field;
    formatter = new DecimalFormat("#,###.##");

    uiHandler();
    
   }

  private void uiHandler() {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
       @Override
       public void run() {
         String fieldDescription = null;
         switch(field) {
           case "new_cases":
            fieldDescription = "New Cases";
            populateTableData();
            break;
           case "new_deaths":
            fieldDescription = "New Deaths";
            populateTableData();
            break;
           case "new_tests":
            fieldDescription = "New Tests";
            populateTableData();
            break;
           case "total_cases_per_million":
            fieldDescription = "Case/Million";
            populateTableData();
            break;
           case "total_deaths_per_million":
            fieldDescription = "Death/Million";
            populateTableData();
            break;
           case "total_tests":
            fieldDescription = "Test/Million";
            field = "total_tests_per_thousand*1000";
            populateTableData();
            break;
           case "positive_rate":
            fieldDescription = "Positivity Rate";
            populateTableData();
            break;
           case "R0":
            fieldDescription = "R0";
            populateTableDataR0();
            break;
           default:
          }
         String sql = "select location from country where id = #".replace("#", String.valueOf(idData));
         Cursor cursor = db.rawQuery(sql, null);
         cursor.moveToFirst();
         country = cursor.getString(cursor.getColumnIndex("location"));

         setHeader("Date", fieldDescription);
         setFooter(country + " : " + fieldDescription);
         registerOnStack(Constants.UIData, context, idData);
        }
      });
   }
   
  private void populateTableDataR0() {
    ArrayList<TableKeyValue> tkvs = new ArrayList<TableKeyValue>();
    String sqlNewCases = "select date, sum(new_cases) as sumNewCases from data where fk_country = # group by date order by date desc".replace("#", String.valueOf(idData));
    Cursor cSumNewCases = db.rawQuery(sqlNewCases, null);
    Long dayX = 1L;
    Long prevX = 1L;
    cSumNewCases.moveToFirst();
    do {
      TableKeyValue tkv = new TableKeyValue();
      tkv.subClass = Constants.UITerraData;
      tkv.key = cSumNewCases.getString(cSumNewCases.getColumnIndex("date"));
      tkv.value = String.valueOf(cSumNewCases.getLong(cSumNewCases.getColumnIndex("sumNewCases")));
      tkvs.add(tkv);
     } while(cSumNewCases.moveToNext());
    double delay = 0.0;
    for(int i = 1; i < tkvs.size() - 2; i++) {
      prevX = Long.parseLong(tkvs.get(i - 1).value);
      dayX = Long.parseLong(tkvs.get(i).value);
      if(prevX == 0) prevX = 1L;
      if(dayX == 0) dayX = 1L;
      delay = prevX.doubleValue() / dayX + 1;
      if(i > 2)
       tkvs.get(i - 2).value = String.valueOf(formatter.format(delay));
     }
    tkvs.remove(0); // ignore the first value
    tkvs.remove(tkvs.size() - 1); // ignore the initial date
    setTableLayout(getTableRows(tkvs));
   }
   
  private void populateTableData() {
    ArrayList<TableKeyValue> tkvs = new ArrayList<TableKeyValue>();
    String sql = "select date, $ from data where fk_country = # order by date desc".replace("$", field).replace("#", String.valueOf(idData));
    Cursor cursor = db.rawQuery(sql, null);
    cursor.moveToFirst();
    do {
      TableKeyValue tkv = new TableKeyValue();
      tkv.subClass = Constants.UIData;
      tkv.key = cursor.getString(cursor.getColumnIndex("date"));
      tkv.value = String.valueOf(formatter.format(cursor.getDouble(cursor.getColumnIndex(field))));
      tkvs.add(tkv);
     } while(cursor.moveToNext());
    setTableLayout(getTableRows(tkvs));
   }
 }
