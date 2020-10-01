package ie.webware.catseyes;

import android.content.*;
import android.database.*;
import java.text.*;
import java.util.*;
import android.util.*;

public class TVTerra extends TV
 {
  Context context = null;
  long id = 0;
  DecimalFormat formatter = null;
  String headerKey = null;
  String headerValue = null;

  public TVTerra(Context _context, long _id) {
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
    //tkv.key = "Case/Million"; tkv.value = String.valueOf(formatter.format(cursor.getLong(cursor.getColumnIndex("total_cases_per_million")))); tkvs.add(tkv); tkv.tableId = 0l; tkv.field = "total_cases_per_million"; tkv.subClass = "TVTerra"; tkv = new TableKeyValue();
    //tkv.key = "Death/Million"; tkv.value = String.valueOf(formatter.format(cursor.getLong(cursor.getColumnIndex("total_deaths_per_million")))); tkvs.add(tkv); tkv.tableId = 0l; tkv.field = "total_deaths_per_million"; tkv.subClass = "TVTerra"; tkv = new TableKeyValue();
    
//    tkv.key = "New Cases"; tkv.value = String.valueOf(formatter.format(cursor.getLong(cursor.getColumnIndex("new_cases")))); tkvs.add(tkv); tkv.tableId = idCountry; tkv.field = "new_cases"; tkv.subClass = "TVCountry"; tkv = new TableKeyValue();
//    tkv.key = "New Deaths"; tkv.value = String.valueOf(formatter.format(cursor.getLong(cursor.getColumnIndex("new_deaths")))); tkvs.add(tkv); tkv.tableId = idCountry; tkv.field = "new_deaths"; tkv.subClass = "TVCountry"; tkv = new TableKeyValue();
//    tkv.key = "New Case/Million"; tkv.value = String.valueOf(formatter.format(cursor.getLong(cursor.getColumnIndex("new_cases_per_million")))); tkvs.add(tkv); tkv.tableId = idCountry; tkv.field = "new_cases_per_million"; tkv.subClass = "TVCountry"; tkv = new TableKeyValue();
//    tkv.key = "New Death/Million"; tkv.value = String.valueOf(formatter.format(cursor.getLong(cursor.getColumnIndex("new_deaths_per_million")))); tkvs.add(tkv); tkv.tableId = idCountry; tkv.field = "new_deaths_per_million"; tkv.subClass = "TVCountry"; tkv = new TableKeyValue();
//
    setTableLayout(getTableRows(tkvs));

//    ArrayList<TableKeyValue> tkvs = new ArrayList<TableKeyValue>();
//    String sql = "select COUNTRY_CODE, continent, location, population from country where ID = #".replace("#", String.valueOf(idCountry));
//    Cursor cursor = db.rawQuery(sql, null);
//    cursor.moveToFirst();
//    TableKeyValue tkv = new TableKeyValue();
//    tkv.key = "Country Code"; tkv.value = (cursor.getString(cursor.getColumnIndex("COUNTRY_CODE"))); tkvs.add(tkv); tkv.tableId = idCountry; tkv.field = "COUNTRY_CODE"; tkv.subClass = "TVCountry"; tkv = new TableKeyValue();
//    tkv.key = "Region"; tkv.value = cursor.getString(cursor.getColumnIndex("continent")); tkvs.add(tkv); tkv.tableId = idCountry; tkv.field = "continent"; tkv.subClass = "TVCountry"; tkv = new TableKeyValue();
//    tkv.key = "Country"; tkv.value = cursor.getString(cursor.getColumnIndex("location"));  tkvs.add(tkv); tkv.tableId = idCountry; tkv.field = "location"; tkv.subClass = "TVCountry"; tkv = new TableKeyValue();
//    tkv.key = "Population"; tkv.value = String.valueOf(formatter.format(cursor.getLong(cursor.getColumnIndex("population"))));tkvs.add(tkv); tkv.tableId = idCountry; tkv.field = "population"; tkv.subClass = "TVCountry"; tkv = new TableKeyValue();
//    sql = "select new_cases, new_deaths, sum(new_cases) as total_cases, total_deaths, date, total_cases_per_million, new_cases_per_million, total_deaths_per_million, new_deaths_per_million " + 
//     "from data where fk_country = # order by total_cases desc limit 1".replace("#", String.valueOf(idCountry));
//    cursor = db.rawQuery(sql, null);
//    cursor.moveToFirst();
//    tkv.key = "Last Updated"; tkv.value = cursor.getString(cursor.getColumnIndex("date")); try { tkv.value = new SimpleDateFormat("yyyy-MM-dd").parse(tkv.value).toString(); String[] arrDate = tkv.value.split(" "); tkv.value = arrDate[0] + " " + arrDate[1] + " " + arrDate[2] + " " + arrDate[5]; } catch(ParseException e) { Log.d("TVCountry", e.toString()); } finally { tkvs.add(tkv);} tkv.tableId = idCountry; tkv.field = "date"; tkv.subClass = "TVCountry"; tkv = new TableKeyValue();
//    tkv.key = "New Cases"; tkv.value = String.valueOf(formatter.format(cursor.getLong(cursor.getColumnIndex("new_cases")))); tkvs.add(tkv); tkv.tableId = idCountry; tkv.field = "new_cases"; tkv.subClass = "TVCountry"; tkv = new TableKeyValue();
//    tkv.key = "New Deaths"; tkv.value = String.valueOf(formatter.format(cursor.getLong(cursor.getColumnIndex("new_deaths")))); tkvs.add(tkv); tkv.tableId = idCountry; tkv.field = "new_deaths"; tkv.subClass = "TVCountry"; tkv = new TableKeyValue();
//    tkv.key = "Total Cases"; tkv.value = String.valueOf(formatter.format(cursor.getLong(cursor.getColumnIndex("total_cases")))); tkvs.add(tkv); tkv.tableId = idCountry; tkv.field = "total_cases"; tkv.subClass = "TVCountry"; tkv = new TableKeyValue();
//    tkv.key = "Total Deaths"; tkv.value = String.valueOf(formatter.format(cursor.getLong(cursor.getColumnIndex("total_deaths")))); tkvs.add(tkv); tkv.tableId = idCountry; tkv.field = "total_deaths"; tkv.subClass = "TVCountry"; tkv = new TableKeyValue();
//    tkv.key = "Case/Million"; tkv.value = String.valueOf(formatter.format(cursor.getLong(cursor.getColumnIndex("total_cases_per_million")))); tkvs.add(tkv); tkv.tableId = idCountry; tkv.field = "total_cases_per_million"; tkv.subClass = "TVCountry"; tkv = new TableKeyValue();
//    tkv.key = "Death/Million"; tkv.value = String.valueOf(formatter.format(cursor.getLong(cursor.getColumnIndex("total_deaths_per_million")))); tkvs.add(tkv); tkv.tableId = idCountry; tkv.field = "total_deaths_per_million"; tkv.subClass = "TVCountry"; tkv = new TableKeyValue();
//    tkv.key = "New Case/Million"; tkv.value = String.valueOf(formatter.format(cursor.getLong(cursor.getColumnIndex("new_cases_per_million")))); tkvs.add(tkv); tkv.tableId = idCountry; tkv.field = "new_cases_per_million"; tkv.subClass = "TVCountry"; tkv = new TableKeyValue();
//    tkv.key = "New Death/Million"; tkv.value = String.valueOf(formatter.format(cursor.getLong(cursor.getColumnIndex("new_deaths_per_million")))); tkvs.add(tkv); tkv.tableId = idCountry; tkv.field = "new_deaths_per_million"; tkv.subClass = "TVCountry"; tkv = new TableKeyValue();
//
//    setTableLayout(getTableRows(tkvs));
   }

 }
