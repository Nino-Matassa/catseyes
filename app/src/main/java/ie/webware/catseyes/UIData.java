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
    String sqlNewCasesToday = "select date, sum(new_cases) as sumNewCasesToday from data where fk_country = # group by date order by date desc".replace("#", String.valueOf(idData));
    String sqlSumNewCasesYesterday = "select date, sum(new_cases) as sumNewCasesYesterday from data where fk_country = # group by date order by date desc".replace("#", String.valueOf(idData));
    Cursor cSumNewCasesToday = db.rawQuery(sqlNewCasesToday, null);
    Cursor cSumNewCasesYesterday = db.rawQuery(sqlSumNewCasesYesterday, null);
    cSumNewCasesYesterday.moveToFirst();
    cSumNewCasesYesterday.moveToNext();
    cSumNewCasesToday.moveToFirst();
    do {
      TableKeyValue tkv = new TableKeyValue();
      tkv.subClass = Constants.UIData;
      Long sumNewCasesToday = cSumNewCasesToday.getLong(cSumNewCasesToday.getColumnIndex("sumNewCasesToday"));
      Long sumNewCasesYesterday = cSumNewCasesYesterday.getLong(cSumNewCasesYesterday.getColumnIndex("sumNewCasesYesterday"));
      tkv.key = cSumNewCasesYesterday.getString(cSumNewCasesYesterday.getColumnIndex("date"));
      if(sumNewCasesToday == 0) {
        tkv.value = String.valueOf(0);
       } else {
        tkv.value = String.valueOf(formatter.format(sumNewCasesYesterday.doubleValue()/sumNewCasesToday));
       }
      tkvs.add(tkv);
      cSumNewCasesToday.moveToNext();
     } while(cSumNewCasesYesterday.moveToNext());
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
