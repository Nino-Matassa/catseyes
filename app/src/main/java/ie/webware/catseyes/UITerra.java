package ie.webware.catseyes;

import android.content.*;
import android.database.*;
import android.os.*;
import android.util.*;
import java.text.*;
import java.util.*;

public class UITerra extends UI
 {
  Context context = null;
  long id = 0;
  DecimalFormat formatter = null;
  String headerKey = null;
  String headerValue = null;
  // DB values
  String lastUpdated = null;
  Long sumNewCases = 0L;
  Long sumNewDeaths = 0L;
  Long sumPopulation = 0L;
  Long sumNewTests = 0L;
  Double positivityRate = 0.0;
  Integer nCountry = 0;

  public UITerra(Context _context, long _id) {
    super(_context, _id);
    context = _context;
    id = _id;
    formatter = new DecimalFormat("#,###.##");

    uiHandler();
   }

  private void uiHandler() {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
       @Override
       public void run() {
         populateTerra();
         setHeader(headerKey, headerValue);
         setFooter("Terra");
         registerOnStack(Constants.UITerra, context, id);             
        }
      });
   }

  private void populateTerra() {
    ArrayList<TableKeyValue> tkvs = new ArrayList<TableKeyValue>();
    String sql = "select count(id) as ncountry, sum(population) as population from country";
    Cursor cursor = db.rawQuery(sql, null);
    cursor.moveToFirst();
    sumPopulation = cursor.getLong(cursor.getColumnIndex("population"));
    nCountry = cursor.getInt(cursor.getColumnIndex("ncountry"));
    TableKeyValue tkv = new TableKeyValue();
    tkv.key = "Population"; tkv.value = String.valueOf(formatter.format(sumPopulation)); tkvs.add(tkv); tkv.tableId = 0L; tkv.field = "population"; tkv.subClass = Constants.UITerra; tkv = new TableKeyValue();

    sql = "select sum(positive_rate) as positivity_rate, sum(new_cases) as total_cases, sum(new_deaths) as total_deaths, sum(new_tests) as total_tests, date from data order by date desc";
    cursor = db.rawQuery(sql, null);
    cursor.moveToFirst();
    lastUpdated = cursor.getString(cursor.getColumnIndex("date"));
    try { 
      lastUpdated = new SimpleDateFormat("yyyy-MM-dd").parse(lastUpdated).toString();
      String[] arrDate = lastUpdated.split(" ");
      lastUpdated = arrDate[0] + " " + arrDate[1] + " " + arrDate[2] + " " + arrDate[5];
     } catch(ParseException e) {
      Log.d(Constants.UITerra, e.toString());
     }
    sumNewCases = cursor.getLong(cursor.getColumnIndex("total_cases"));
    sumNewDeaths = cursor.getLong(cursor.getColumnIndex("total_deaths"));
    sumNewTests = cursor.getLong(cursor.getColumnIndex("total_tests"));
    positivityRate = cursor.getDouble(cursor.getColumnIndex("positivity_rate"));

    tkv.key = "Last Updated";
    tkv.value =  lastUpdated; 
    tkvs.add(tkv); 
    tkv.tableId = 0l; 
    tkv.field = "date"; 
    tkv.subClass = Constants.UITerra; 
    headerKey = "Terra"; 
    headerValue = tkv.value; 
    tkv = new TableKeyValue();

    tkv.key = "Cases";
    tkv.value = String.valueOf(formatter.format(sumNewCases));
    tkvs.add(tkv);
    tkv.tableId = 0L;
    tkv.field = "new_cases";
    tkv.subClass = Constants.UITerra;
    tkv = new TableKeyValue();

    tkv.key = "Deaths";
    tkv.value = String.valueOf(formatter.format(sumNewDeaths));
    tkvs.add(tkv);
    tkv.tableId = 0L;
    tkv.field = "new_deaths";
    tkv.subClass = Constants.UITerra;
    tkv = new TableKeyValue();

    Double population = sumPopulation.doubleValue();
    Double totalCases = sumNewCases.doubleValue();
    Double totalDeaths = sumNewDeaths.doubleValue();
    Double casePerMillion = totalCases / population * Constants.oneMillion;
    Double deathPerMillion = totalDeaths / population * Constants.oneMillion;
    Double testPerMillion = sumNewTests / population * Constants.oneMillion;

    tkv.key = "Tests";
    tkv.value = String.valueOf(formatter.format(sumNewTests));
    tkvs.add(tkv);
    tkv.tableId = 0l;
    tkv.field = "new_tests";
    tkv.subClass = Constants.UITerra;
    tkv = new TableKeyValue();

    tkv.key = "Case/Million";
    tkv.value = String.valueOf(formatter.format(casePerMillion));
    tkvs.add(tkv);
    tkv.tableId = 0l;
    tkv.field = "total_cases_per_million";
    tkv.subClass = Constants.UITerra;
    tkv = new TableKeyValue();

    tkv.key = "Death/Million";
    tkv.value = String.valueOf(formatter.format(deathPerMillion));
    tkvs.add(tkv);
    tkv.tableId = 0l;
    tkv.field = "total_deaths_per_million";
    tkv.subClass = Constants.UITerra;
    tkv = new TableKeyValue();

    tkv.key = "Test/Million";
    tkv.value = String.valueOf(formatter.format(testPerMillion));
    tkvs.add(tkv);
    tkv.tableId = 0l;
    tkv.field = "total_tests_per_million";
    tkv.subClass = Constants.UITerra;
    tkv = new TableKeyValue();

    positivityRate = positivityRate.doubleValue() / nCountry;

    tkv.key = "Test Positive Rate";
    tkv.value = String.valueOf(formatter.format(positivityRate)) + "%";
    tkvs.add(tkv);
    tkv.tableId = 0l;
    tkv.field = "positive_rate";
    tkv.subClass = Constants.UITerra;
    tkv = new TableKeyValue();

    tkv.key = "R0"; //"∄";//
    tkv.value = String.valueOf(formatter.format(populateR0Average(nCountry)));
    tkvs.add(tkv);
    tkv.tableId = 0l;
    tkv.field = "R0";
    tkv.subClass = Constants.UITerra;
    tkv = new TableKeyValue();

    setTableLayout(getTableRows(tkvs));
   }

  private double populateR0Average(int nCountry) {
    Double R0 = 0.0;
    String sqlNewCases = "select date, sum(new_cases) as sumNewCases from data group by date";
    Cursor cSumNewCases = db.rawQuery(sqlNewCases, null);
    Long dayX = 0L;
    Long prevX = 1L;
    cSumNewCases.moveToFirst();
    do {
      try {
        prevX = dayX;
        dayX += cSumNewCases.getLong(cSumNewCases.getColumnIndex("sumNewCases"));
       } catch(Exception e) {
        Log.d("UITerra", e.toString());
       }
     } while(cSumNewCases.moveToNext());
    R0 = dayX.doubleValue() / prevX.doubleValue();
    return R0;
   }
 }
