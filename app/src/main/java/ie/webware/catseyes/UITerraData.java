package ie.webware.catseyes;
import android.content.*;
import android.database.*;
import android.widget.*;
import java.text.*;
import java.util.*;

public class UITerraData extends UI
 {
  Context context = null;
  long idData = 0;
  String field = null;
  DecimalFormat formatter = null;
  String sql = null;
  boolean bPerMillion = false;
  Long sumPopulation = 0L;

  public UITerraData(Context _context, long _idData, String _field) {
    super(_context, _idData);
    context = _context;
    idData = _idData;
    field = _field;
    formatter = new DecimalFormat("#,###.##");

    String fieldDescription = null;
    switch(field) {
      case "new_cases":
       fieldDescription = "New Cases";
       sql = "select date, sum(new_cases) as new_cases from data group by date order by date desc";
       populateTerraDetails();
       break;
      case "new_deaths":
       fieldDescription = "New Deaths";
       sql = "select date, sum(new_deaths) as new_deaths from data group by date order by date desc";
       populateTerraDetails();
       break;
      case "new_tests":
       fieldDescription = "Total Tests";
       sql = "select date, sum(new_tests) as new_tests from data group by date order by date desc";
       field = "new_tests";
       populateTerraDetails();
       break;
      case "total_cases_per_million":
       fieldDescription = "Cases/Million";
       sql = "select date, sum(total_cases) as total_cases from data group by date order by date desc";
       field = "total_cases";
       bPerMillion = true;
       populateTerraDetails();
       break;
      case "total_deaths_per_million":
       fieldDescription = "Deaths/Million";
       sql = "select date, sum(total_deaths) as total_deaths from data group by date order by date desc";
       field = "total_deaths";
       bPerMillion = true;
       populateTerraDetails();
       break;
      case "total_tests_per_million":
       fieldDescription = "Total Tests/Million";
       sql = "select date, sum(total_tests) as total_tests from data group by date order by date desc";
       field = "total_tests";
       bPerMillion = true;
       populateTerraDetails();
       break;
      case "positive_rate":
       fieldDescription = "Positivity Rate";
       sql = "select date, sum(positive_rate) as positive_rate from data group by date order by date desc";
       populateTerraDetails();
       break;
      case "R0":
       fieldDescription = "R0";
       populateTerraDetailsR0();
       break;
     }

    setHeader("Terra", fieldDescription);
    registerOnStack(Constants.UITerraData, context, idData);
   }
   
  private void populateTerraDetailsR0() {
    ArrayList<TableKeyValue> tkvs = new ArrayList<TableKeyValue>();
    String sqlNewCasesToday = "select date, sum(new_cases) as sumNewCasesToday from data group by date order by date desc";
    String sqlSumNewCasesYesterday = "select date, sum(new_cases) as sumNewCasesYesterday from data group by date order by date desc";
    Cursor cSumNewCasesToday = db.rawQuery(sqlNewCasesToday, null);
    Cursor cSumNewCasesYesterday = db.rawQuery(sqlSumNewCasesYesterday, null);
    cSumNewCasesYesterday.moveToFirst();
    cSumNewCasesYesterday.moveToNext();
    cSumNewCasesToday.moveToFirst();
    do {
      TableKeyValue tkv = new TableKeyValue();
      tkv.subClass = Constants.UITerraData;
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

  private void populateTerraDetails() {
    ArrayList<TableKeyValue> tkvs = new ArrayList<TableKeyValue>();

    if(bPerMillion) {
      String sqlPopulation = "select sum(population) as population from country";
      Cursor cursor = db.rawQuery(sqlPopulation, null);
      cursor.moveToFirst();
      sumPopulation = cursor.getLong(cursor.getColumnIndex("population"));
     }

    Cursor cursor = db.rawQuery(sql, null);
    cursor.moveToFirst();
    do {
      TableKeyValue tkv = new TableKeyValue();
      tkv.subClass = Constants.UITerraData;
      tkv.key = cursor.getString(cursor.getColumnIndex("date"));
      tkv.value = String.valueOf(formatter.format(cursor.getDouble(cursor.getColumnIndex(field))));
      if(bPerMillion) {
        Double l = cursor.getDouble(cursor.getColumnIndex(field));
        l = l / sumPopulation * Constants.oneMillion;
        tkv.value = String.valueOf(formatter.format(l));
       }
      tkvs.add(tkv);
     } while(cursor.moveToNext());
    setTableLayout(getTableRows(tkvs));
   }
 }
