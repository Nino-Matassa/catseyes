package ie.webware.catseyes;

import android.content.*;
import android.database.*;
import android.util.*;
import java.text.*;
import java.util.*;

public class UICountry extends UI
 {
  Context context = null;
  long idCountry = 0;
  DecimalFormat formatter = null;
  // DB values
  String countryCode = null;
  String region = null;
  String country = null;
  Long population = 0L;
  String lastUpdated = null;
  Long newCases = 0L;
  Long newDeaths = 0L;
  Long sumNewCases = 0L;
  Long sumNewDeaths = 0L;
  Double casePerMillion = 0.0;
  Double deathPerMillion = 0.0;
  Double newCasePerMillion = 0.0;
  Double newDeathPerMillion = 0.0;
  
  public UICountry(Context _context, long _idCountry) {
    super(_context, _idCountry);
    context = _context;
    idCountry = _idCountry;
    formatter = new DecimalFormat("#,###.##");
    
    populateTableCountry();
   setHeader("Country", "Details");
   registerOnStack(Constants.UICountry, context, idCountry);
   }
   
  public void populateTableCountry() {
    ArrayList<TableKeyValue> tkvs = new ArrayList<TableKeyValue>();
    String sql = "select COUNTRY_CODE, continent, location, population from country where ID = #".replace("#", String.valueOf(idCountry));
    Cursor cursor = db.rawQuery(sql, null);
    cursor.moveToFirst();
    countryCode = cursor.getString(cursor.getColumnIndex("COUNTRY_CODE"));
    region = cursor.getString(cursor.getColumnIndex("continent"));
    country = cursor.getString(cursor.getColumnIndex("location"));
    population = cursor.getLong(cursor.getColumnIndex("population"));
    
    sql = "select new_cases, new_deaths, sum(new_cases) as total_cases, sum(new_deaths) as total_deaths, date, total_cases_per_million, new_cases_per_million, total_deaths_per_million, new_deaths_per_million " + 
    "from data where fk_country = # order by total_cases desc limit 1".replace("#", String.valueOf(idCountry));
    cursor = db.rawQuery(sql, null);
    cursor.moveToFirst();
    
    lastUpdated = cursor.getString(cursor.getColumnIndex("date"));
    try {
     lastUpdated = new SimpleDateFormat("yyyy-MM-dd").parse(lastUpdated).toString();
     String[] arrDate = lastUpdated.split(" ");
     lastUpdated = arrDate[0] + " " + arrDate[1] + " " + arrDate[2] + " " + arrDate[5];
     } catch(ParseException e) {
       Log.d(Constants.UICountry, e.toString());
     }
    newCases = cursor.getLong(cursor.getColumnIndex("new_cases"));
    newDeaths = cursor.getLong(cursor.getColumnIndex("new_deaths"));
    sumNewCases = cursor.getLong(cursor.getColumnIndex("total_cases"));
    sumNewDeaths = cursor.getLong(cursor.getColumnIndex("total_deaths"));
    casePerMillion = cursor.getDouble(cursor.getColumnIndex("total_cases_per_million"));
    deathPerMillion = cursor.getDouble(cursor.getColumnIndex("total_deaths_per_million"));
    newCasePerMillion = cursor.getDouble(cursor.getColumnIndex("new_cases_per_million"));
    newDeathPerMillion = cursor.getDouble(cursor.getColumnIndex("new_deaths_per_million"));
    
    TableKeyValue tkv = new TableKeyValue();
    tkv.key = "Country Code";
    tkv.value = countryCode;
    tkvs.add(tkv);
    tkv.tableId = idCountry;
    tkv.field = "COUNTRY_CODE";
    tkv.subClass = Constants.UICountry;
    tkv = new TableKeyValue();
    
    tkv.key = "Region";
    tkv.value = region;
    tkvs.add(tkv);
    tkv.tableId = idCountry;
    tkv.field = "continent";
    tkv.subClass = Constants.UICountry;
    tkv = new TableKeyValue();
    
    tkv.key = "Country";
    tkv.value = country; 
    tkvs.add(tkv);
    tkv.tableId = idCountry;
    tkv.field = "location";
    tkv.subClass = Constants.UICountry;
    tkv = new TableKeyValue();
    
    tkv.key = "Population";
    tkv.value = String.valueOf(formatter.format(population));
    tkvs.add(tkv);
    tkv.tableId = idCountry;
    tkv.field = "population";
    tkv.subClass = Constants.UICountry;
    tkv = new TableKeyValue();
    
    tkv.key = "Last Updated";
    tkv.value = lastUpdated;
    tkvs.add(tkv);
    tkv.tableId = idCountry;
    tkv.field = "date";
    tkv.subClass = Constants.UICountry;
    tkv = new TableKeyValue();
    
    tkv.key = "New Cases";
    tkv.value = String.valueOf(formatter.format(newCases));
    tkvs.add(tkv);
    tkv.tableId = idCountry;
    tkv.field = "new_cases";
    tkv.subClass = Constants.UICountry;
    tkv = new TableKeyValue();
    
    tkv.key = "New Deaths";
    tkv.value = String.valueOf(formatter.format(newDeaths));
    tkvs.add(tkv);
    tkv.tableId = idCountry;
    tkv.field = "new_deaths";
    tkv.subClass = Constants.UICountry;
    tkv = new TableKeyValue();
    
    tkv.key = "Total Cases";
    tkv.value = String.valueOf(formatter.format(sumNewCases));
    tkvs.add(tkv);
    tkv.tableId = idCountry;
    tkv.field = "total_cases";
    tkv.subClass = Constants.UICountry;
    tkv = new TableKeyValue();
    
    tkv.key = "Total Deaths";
    tkv.value = String.valueOf(formatter.format(sumNewDeaths));
    tkvs.add(tkv);
    tkv.tableId = idCountry;
    tkv.field = "total_deaths";
    tkv.subClass = Constants.UICountry;
    tkv = new TableKeyValue();
    
    tkv.key = "Case/Million";
    tkv.value = String.valueOf(formatter.format(casePerMillion));
    tkvs.add(tkv);
    tkv.tableId = idCountry;
    tkv.field = "total_cases_per_million";
    tkv.subClass = Constants.UICountry;
    tkv = new TableKeyValue();
    
    tkv.key = "Death/Million";
    tkv.value = String.valueOf(formatter.format(deathPerMillion));
    tkvs.add(tkv);
    tkv.tableId = idCountry;
    tkv.field = "total_deaths_per_million";
    tkv.subClass = Constants.UICountry;
    tkv = new TableKeyValue();
    
    tkv.key = "New Case/Million";
    tkv.value = String.valueOf(formatter.format(newCasePerMillion));
    tkvs.add(tkv);
    tkv.tableId = idCountry;
    tkv.field = "new_cases_per_million";
    tkv.subClass = Constants.UICountry;
    tkv = new TableKeyValue();
    
    tkv.key = "New Death/Million";
    tkv.value = String.valueOf(formatter.format(newDeathPerMillion));
    tkvs.add(tkv);
    tkv.tableId = idCountry;
    tkv.field = "new_deaths_per_million";
    tkv.subClass = Constants.UICountry;
    tkv = new TableKeyValue();
    
    setTableLayout(getTableRows(tkvs));
   }
 }
