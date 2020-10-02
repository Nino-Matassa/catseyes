package ie.webware.catseyes;

import android.content.*;
import android.database.*;
import java.text.*;
import java.util.*;
import android.util.*;
import java.util.function.*;

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

  public UITerra(Context _context, long _id) {
    super(_context, _id);
    context = _context;
    id = _id;
    formatter = new DecimalFormat("#,###.##");

    populateTerra();
    setHeader(headerKey, headerValue);
    registerOnStack("TVTerra", context, id);
   }

  private void populateTerra() {
    ArrayList<TableKeyValue> tkvs = new ArrayList<TableKeyValue>();
    String sql = "select sum(population) as population from country";
    Cursor cursor = db.rawQuery(sql, null);
    cursor.moveToFirst();
    sumPopulation = cursor.getLong(cursor.getColumnIndex("population"));
    TableKeyValue tkv = new TableKeyValue();
    tkv.key = "Population"; tkv.value = String.valueOf(formatter.format(sumPopulation)); tkvs.add(tkv); tkv.tableId = 0L; tkv.field = "population"; tkv.subClass = "TVTerra"; tkv = new TableKeyValue();

    sql = "select sum(new_cases) as total_cases, sum(new_deaths) as total_deaths, sum(new_tests) as total_tests, date from data order by date desc";
    cursor = db.rawQuery(sql, null);
    cursor.moveToFirst();
    lastUpdated = cursor.getString(cursor.getColumnIndex("date"));
    try { 
      lastUpdated = new SimpleDateFormat("yyyy-MM-dd").parse(lastUpdated).toString();
      String[] arrDate = lastUpdated.split(" ");
      lastUpdated = arrDate[0] + " " + arrDate[1] + " " + arrDate[2] + " " + arrDate[5];
     } catch(ParseException e) {
      Log.d("UITerra", e.toString());
     }
    sumNewCases = cursor.getLong(cursor.getColumnIndex("total_cases"));
    sumNewDeaths = cursor.getLong(cursor.getColumnIndex("total_deaths"));
    sumNewTests = cursor.getLong(cursor.getColumnIndex("total_tests"));
    
    tkv.key = "Last Updated";
    tkv.value =  lastUpdated; 
    tkvs.add(tkv); 
    tkv.tableId = 0l; 
    tkv.field = "date"; 
    tkv.subClass = "TVTerra"; 
    headerKey = "Terra"; 
    headerValue = tkv.value; 
    tkv = new TableKeyValue();
    
    tkv.key = "Total Cases";
    tkv.value = String.valueOf(formatter.format(sumNewCases));
    tkvs.add(tkv);
    tkv.tableId = 0L;
    tkv.field = "total_cases";
    tkv.subClass = "TVTerra";
    tkv = new TableKeyValue();
    
    tkv.key = "Total Deaths";
    tkv.value = String.valueOf(formatter.format(sumNewDeaths));
    tkvs.add(tkv);
    tkv.tableId = 0L;
    tkv.field = "total_deaths";
    tkv.subClass = "TVTerra";
    tkv = new TableKeyValue();

    Double population = sumPopulation.doubleValue();
    Double totalCases = sumNewCases.doubleValue();
    Double totalDeaths = sumNewDeaths.doubleValue();
    Double casePerMillion = totalCases / population * Constants.oneMillion;
    Double deathPerMillion = totalDeaths / population * Constants.oneMillion;
    Double testPerMillion = sumNewTests / population * Constants.oneMillion;

    tkv.key = "Case/Million";
    tkv.value = String.valueOf(formatter.format(casePerMillion));
    tkvs.add(tkv);
    tkv.tableId = 0l;
    tkv.field = "total_cases_per_million";
    tkv.subClass = "TVTerra";
    tkv = new TableKeyValue();
    
    tkv.key = "Death/Million";
    tkv.value = String.valueOf(formatter.format(deathPerMillion));
    tkvs.add(tkv);
    tkv.tableId = 0l;
    tkv.field = "total_deaths_per_million";
    tkv.subClass = "TVTerra";
    tkv = new TableKeyValue();
    
    tkv.key = "Test/Million";
    tkv.value = String.valueOf(formatter.format(testPerMillion));
    tkvs.add(tkv);
    tkv.tableId = 0l;
    tkv.field = "total_tests_per_million";
    tkv.subClass = "TVTerra";
    tkv = new TableKeyValue();
    
    setTableLayout(getTableRows(tkvs));
   }
 }
