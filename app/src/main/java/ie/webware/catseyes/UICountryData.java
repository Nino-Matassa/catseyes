package ie.webware.catseyes;

import android.content.*;
import android.database.*;
import android.util.*;
import java.text.*;
import java.util.*;
import android.widget.*;
import android.os.*;

public class UICountryData extends UI
 {
  Context context = null;
  long idData = 0;
  String field = null;
  String country = null;
  DecimalFormat formatter = null;

  public UICountryData(Context _context, long _idData, String _field) {
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
            fieldDescription = "Positivity Rate%";
            populatePositivityDetails();
            break;
           case "R0":
            fieldDescription = "R0"; //"∄";//
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
         registerOnStack(Constants.UICountryData, context, idData);
        }
      });
   }
   
  private void populateTableData() {
    ArrayList<TableKeyValue> tkvs = new ArrayList<TableKeyValue>();
    String sql = "select date, $ from data where fk_country = # order by date desc".replace("$", field).replace("#", String.valueOf(idData));
    Cursor cursor = db.rawQuery(sql, null);
    cursor.moveToFirst();
    do {
      TableKeyValue tkv = new TableKeyValue();
      tkv.subClass = Constants.UICountryData;
      tkv.key = cursor.getString(cursor.getColumnIndex("date"));
      tkv.value = String.valueOf(formatter.format(cursor.getDouble(cursor.getColumnIndex(field))));
      tkvs.add(tkv);
     } while(cursor.moveToNext());
    setTableLayout(getTableRows(tkvs));
   }
   
  private void populateTableDataR0() {
    ArrayList<TableKeyValue> tkvs = new ArrayList<TableKeyValue>();
    String sqlNewCases = "select date, sum(new_cases) as sumNewCases from data where fk_country = # group by date order by date desc".replace("#", String.valueOf(idData));
    Cursor cSumNewCases = db.rawQuery(sqlNewCases, null);
    Long dayX = 1L;
    Long prevX = 1L;
    cSumNewCases.moveToLast();
    do {
      TableKeyValue tkv = new TableKeyValue();
      tkv.subClass = Constants.UITerraData;
      tkv.key = cSumNewCases.getString(cSumNewCases.getColumnIndex("date"));
      dayX += cSumNewCases.getLong(cSumNewCases.getColumnIndex("sumNewCases"));
      tkv.value = String.valueOf(formatter.format(dayX.doubleValue() / prevX));
      tkvs.add(tkv);
      prevX = dayX;
     } while(cSumNewCases.moveToPrevious());
    Collections.reverse(tkvs);
    setTableLayout(getTableRows(tkvs));
   }
   
  private void populatePositivityDetails() {
    ArrayList<TableKeyValue> tkvs = new ArrayList<TableKeyValue>();
    String sqlPositivityRate = "select date, sum(positive_rate) as positive_rate from data where fk_country = # group by date order by date".replace("#", String.valueOf(idData));
    Cursor cPositivityRate = db.rawQuery(sqlPositivityRate, null);
    Double dayX = 0.0;
    int nDay = 0;
    cPositivityRate.moveToLast();
    do {
      TableKeyValue tkv = new TableKeyValue();
      tkv.subClass = Constants.UITerraData;
      tkv.key = cPositivityRate.getString(cPositivityRate.getColumnIndex("date"));
      dayX += cPositivityRate.getDouble(cPositivityRate.getColumnIndex("positive_rate"));
      tkv.value = String.valueOf(formatter.format((dayX/nDay++)*100));
      tkvs.add(tkv);
     } while(cPositivityRate.moveToPrevious());
    setTableLayout(getTableRows(tkvs));
   }
 }
