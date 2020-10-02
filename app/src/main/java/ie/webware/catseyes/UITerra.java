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
    TableKeyValue tkv = new TableKeyValue(); tkv.key = "Population"; tkv.value = String.valueOf(formatter.format(cursor.getLong(cursor.getColumnIndex("population")))); tkvs.add(tkv); tkv.tableId = 0L; tkv.field = "population"; tkv.subClass = "TVTerra"; tkv = new TableKeyValue();
    
    sql = "select sum(new_cases) as total_cases, sum(new_deaths) as total_deaths, date from data order by date desc";
    cursor = db.rawQuery(sql, null);
    cursor.moveToFirst();
    tkv.key = "Last Updated"; tkv.value = cursor.getString(cursor.getColumnIndex("date")); try { tkv.value = new SimpleDateFormat("yyyy-MM-dd").parse(tkv.value).toString(); String[] arrDate = tkv.value.split(" "); tkv.value = arrDate[0] + " " + arrDate[1] + " " + arrDate[2] + " " + arrDate[5]; } catch(ParseException e) { Log.d("TVCountry", e.toString()); } finally { tkvs.add(tkv);} tkv.tableId = 0l; tkv.field = "date"; tkv.subClass = "TVTerra"; headerKey = "Terra"; headerValue = tkv.value; tkv = new TableKeyValue();
    tkv.key = "Total Cases"; tkv.value = String.valueOf(formatter.format(cursor.getLong(cursor.getColumnIndex("total_cases")))); tkvs.add(tkv); tkv.tableId = 0L; tkv.field = "total_cases"; tkv.subClass = "TVTerra"; tkv = new TableKeyValue();
    tkv.key = "Total Deaths"; tkv.value = String.valueOf(formatter.format(cursor.getLong(cursor.getColumnIndex("total_deaths")))); tkvs.add(tkv); tkv.tableId = 0L; tkv.field = "total_deaths"; tkv.subClass = "TVTerra"; tkv = new TableKeyValue();
    
    sql = "select sum(population) as population from country";
    cursor = db.rawQuery(sql, null);
    cursor.moveToFirst();
    Double population = cursor.getDouble(cursor.getColumnIndex("population"));
    sql = "select sum(new_cases) as total_cases from data";
    cursor = db.rawQuery(sql, null);
    cursor.moveToFirst();
    Double totalCases = cursor.getDouble(cursor.getColumnIndex("total_cases"));
    sql = "select sum(new_deaths) as total_deaths from data";
    cursor = db.rawQuery(sql, null);
    cursor.moveToFirst();
    Double totalDeaths = cursor.getDouble(cursor.getColumnIndex("total_deaths"));
    Double casePerMillion = (totalCases/population)*1000000;
    Double deathPerMillion = (totalDeaths/population)*1000000;
    tkv.key = "Case/Million"; tkv.value = String.valueOf(formatter.format(casePerMillion)); tkvs.add(tkv); tkv.tableId = 0l; tkv.field = "total_cases_per_million"; tkv.subClass = "TVTerra"; tkv = new TableKeyValue();
    tkv.key = "Death/Million"; tkv.value = String.valueOf(formatter.format(deathPerMillion)); tkvs.add(tkv); tkv.tableId = 0l; tkv.field = "total_deaths_per_million"; tkv.subClass = "TVTerra"; tkv = new TableKeyValue();
    setTableLayout(getTableRows(tkvs));
   }

 }
