package ie.webware.catseyes;

import android.content.*;
import android.database.*;
import android.util.*;
import java.text.*;
import java.util.*;
import android.os.*;

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
  Long sumNewTests = 0L;
  Double testPerMillion = 0.0;
  Double positivityRate = 0.0;

  public UICountry(Context _context, long _idCountry) {
    super(_context, _idCountry);
    context = _context;
    idCountry = _idCountry;
    formatter = new DecimalFormat("#,###.##");

    uiHandler();
   }

  private void uiHandler() {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
       @Override
       public void run() {
         populateTableCountry();
         setHeader(country, "Details");
         setFooter(country + ": Details");
         registerOnStack(Constants.UICountry, context, idCountry);
        }
      });
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

    sql = "select date, sum(positive_rate) as positivity_rate, sum(new_cases) as total_cases, sum(new_deaths) as total_deaths, sum(new_tests) as total_tests " +
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
    sumNewCases = cursor.getLong(cursor.getColumnIndex("total_cases"));
    sumNewDeaths = cursor.getLong(cursor.getColumnIndex("total_deaths"));
    casePerMillion = sumNewCases.doubleValue() / population * Constants.oneMillion;
    deathPerMillion = sumNewDeaths.doubleValue() / population * Constants.oneMillion;
    sumNewTests = cursor.getLong(cursor.getColumnIndex("total_tests"));
    testPerMillion = sumNewTests.doubleValue() / population * Constants.oneMillion;
    positivityRate = cursor.getDouble(cursor.getColumnIndex("positivity_rate"));

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

    tkv.key = "Cases";
    tkv.value = String.valueOf(formatter.format(sumNewCases));
    tkvs.add(tkv);
    tkv.tableId = idCountry;
    tkv.field = "new_cases";
    tkv.subClass = Constants.UICountry;
    tkv = new TableKeyValue();

    tkv.key = "Deaths";
    tkv.value = String.valueOf(formatter.format(sumNewDeaths));
    tkvs.add(tkv);
    tkv.tableId = idCountry;
    tkv.field = "new_deaths";
    tkv.subClass = Constants.UICountry;
    tkv = new TableKeyValue();

    tkv.key = "Tests";
    tkv.value = String.valueOf(formatter.format(sumNewTests));
    tkvs.add(tkv);
    tkv.tableId = idCountry;
    tkv.field = "new_tests";
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

    tkv.key = "Test/Million";
    tkv.value = String.valueOf(formatter.format(testPerMillion));
    tkvs.add(tkv);
    tkv.tableId = idCountry;
    tkv.field = "total_tests";
    tkv.subClass = Constants.UICountry;
    tkv = new TableKeyValue();

    tkv.key = "Test Positive Rate";
    tkv.value = String.valueOf(formatter.format(positivityRate)) + "%";
    tkvs.add(tkv);
    tkv.tableId = idCountry;
    tkv.field = "positive_rate";
    tkv.subClass = Constants.UICountry;
    tkv = new TableKeyValue();

    sql = "select date, sum(new_cases) as newCasesToday from data where fk_country = # and new_cases > 0 group by date order by date desc limit 1".replace("#", String.valueOf(idCountry));
    cursor = db.rawQuery(sql, null);
    cursor.moveToFirst();
    Long newCasesToday = cursor.getLong(cursor.getColumnIndex("newCasesToday"));
    Long existingCases = sumNewCases - newCasesToday;

    Double R0 = existingCases.doubleValue() / sumNewCases;

    tkv.key = "∄";//
    tkv.value = String.valueOf(formatter.format(populateR0Average()));
    tkvs.add(tkv);
    tkv.tableId = idCountry;
    tkv.field = "R0";
    tkv.subClass = Constants.UICountry;
    tkv = new TableKeyValue();


    setTableLayout(getTableRows(tkvs));
   }

  private double populateR0Average() {
    String sqlNewCases = "select date, sum(new_cases) as sumNewCases from data where new_cases > 0 and fk_country = # group by date order by date asc".replace("#", String.valueOf(idCountry));
    Cursor cSumNewCases = db.rawQuery(sqlNewCases, null);
    Long dayX = 0L;
    Long prevX = 1L;
    int nDays = 1;
    Double r0avg = 0.0;
    cSumNewCases.moveToFirst();
    do {
      dayX = cSumNewCases.getLong(cSumNewCases.getColumnIndex("sumNewCases"));
      r0avg += dayX.doubleValue() / prevX.doubleValue();
      prevX = dayX;
      nDays++;
     } while(cSumNewCases.moveToNext());
    return r0avg/nDays;
   }
 }
