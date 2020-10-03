package ie.webware.catseyes;
import android.content.*;
import android.database.*;
import android.widget.*;
import java.text.*;
import java.util.*;

public class UITerraData extends UI {
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
       break;
      case "new_deaths":
       fieldDescription = "New Deaths";
       sql = "select date, sum(new_deaths) as new_deaths from data group by date order by date desc";
       break;
      case "total_cases_per_million":
       fieldDescription = "Cases/Million";
       sql = "select date, sum(new_cases) as new_cases from data group by date order by date desc";
       field = "new_cases";
       bPerMillion = true;
       break;
      case "total_deaths_per_million":
       fieldDescription = "Deaths/Million";
       sql = "select date, sum(new_deaths) as new_deaths from data group by date order by date desc";
       field = "new_deaths";
       bPerMillion = true;
       break;
      case "total_tests_per_million":
       fieldDescription = "Total Tests/Million";
       sql = "select date, sum(new_tests) as new_tests from data where new_tests > 0 group by date order by date desc";
       field = "new_tests";
       bPerMillion = true;
       break;
      case "new_tests":
       fieldDescription = "Total Tests";
       sql = "select date, sum(new_tests) as new_tests from data where new_tests > 0 group by date order by date desc";
       field = "new_tests";
       break;
     }
     
    Toast.makeText(context, "Terra + " + fieldDescription, Toast.LENGTH_SHORT).show();
    populateTerraDetails();
    setHeader("Terra", fieldDescription);
    registerOnStack(Constants.UITerraData, context, idData);
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
      tkv.value = String.valueOf(formatter.format(cursor.getLong(cursor.getColumnIndex(field))));
      if(bPerMillion) {
        Double l = cursor.getDouble(cursor.getColumnIndex(field));
        l = l/sumPopulation*Constants.oneMillion;
        tkv.value = String.valueOf(formatter.format(l));
      }
      tkvs.add(tkv);
     } while(cursor.moveToNext());
    setTableLayout(getTableRows(tkvs));
   }
}
