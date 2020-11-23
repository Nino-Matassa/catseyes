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
            fieldDescription = "Estimated Growth";//
            populateCountryDetailsR0();
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
   
  private void populatePositivityDetails() {
    ArrayList<TableKeyValue> tkvs = new ArrayList<TableKeyValue>();
    String sqlPositivityRate = "select date, sum(new_cases) as cases, sum(new_tests) as tests from data where new_tests > 0 and new_cases > 0 and fk_country = # group by date order by date".replace("#", String.valueOf(idData));
    Cursor cPositivityRate = db.rawQuery(sqlPositivityRate, null);
    Double dayX = 0.0;
    Long cases = 1L;
    Long tests = 1L;
    cPositivityRate.moveToLast();
    do {
      TableKeyValue tkv = new TableKeyValue();
      tkv.subClass = Constants.UITerraData;
      tkv.key = cPositivityRate.getString(cPositivityRate.getColumnIndex("date"));
      cases += cPositivityRate.getLong(cPositivityRate.getColumnIndex("cases"));
      tests += cPositivityRate.getLong(cPositivityRate.getColumnIndex("tests"));
      dayX = cases.doubleValue()/tests.doubleValue()*100;
      tkv.value = String.valueOf(formatter.format(dayX));
      tkvs.add(tkv);
     } while(cPositivityRate.moveToPrevious());
    setTableLayout(getTableRows(tkvs));
   }
   
  private void populateCountryDetailsR0() {
    ArrayList<TableKeyValue> tkvs = new ArrayList<TableKeyValue>();
    String sqlNewCases = "select date, sum(new_cases) as sumNewCases from data where fk_country = # group by date order by date asc".replace("#", String.valueOf(idData));
    Cursor cSumNewCases = db.rawQuery(sqlNewCases, null);
    Long dayX = 0L;
    Long prevX = 1L;
    int nDays = 1;
    Double r0avg = 0.0;
    cSumNewCases.moveToFirst();
    do {
      TableKeyValue tkv = new TableKeyValue();
      tkv.subClass = Constants.UITerraData;
      tkv.key = cSumNewCases.getString(cSumNewCases.getColumnIndex("date"));
      dayX = cSumNewCases.getLong(cSumNewCases.getColumnIndex("sumNewCases"));
      if(dayX > 0) {
        r0avg += dayX.doubleValue() / prevX.doubleValue() * Constants.lossModifier;
        tkv.value = String.valueOf(formatter.format(r0avg/nDays++));
        tkvs.add(tkv);
        prevX = dayX;
      }
     } while(cSumNewCases.moveToNext());
    Collections.reverse(tkvs);
    setTableLayout(getTableRows(tkvs));
   }
 }
