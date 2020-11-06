package ie.webware.catseyes;
import android.app.*;
import android.content.*;
import android.database.*;
import android.view.*;
import java.text.*;
import java.util.*;
import android.os.*;
import android.util.*;

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
            fieldDescription = "∄";//"R0";
            populateTerraDetailsR0();
            break;
          }

         setHeader("Date", fieldDescription);
         setFooter("Terra : " + fieldDescription);
         registerOnStack(Constants.UITerraData, context, idData);
        }
      });
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

//  private void populateTerraDetailsR0() {
//    ArrayList<TableKeyValue> tkvs = new ArrayList<TableKeyValue>();
//    String sqlNewCases = "select date, sum(new_cases) as sumNewCases from data group by date order by date desc";
//    Cursor cSumNewCases = db.rawQuery(sqlNewCases, null);
//    Long dayX = 1L;
//    Long prevX = 1L;
//    cSumNewCases.moveToFirst();
//    do {
//      TableKeyValue tkv = new TableKeyValue();
//      tkv.subClass = Constants.UITerraData;
//      tkv.key = cSumNewCases.getString(cSumNewCases.getColumnIndex("date"));
//      tkv.value = String.valueOf(cSumNewCases.getLong(cSumNewCases.getColumnIndex("sumNewCases")));
//      tkvs.add(tkv);
//     } while(cSumNewCases.moveToNext());
//    double delay = 0.0;
//    for(int i = 1; i < tkvs.size() - 2; i++) {
//      prevX = Long.parseLong(tkvs.get(i - 1).value);
//      dayX = Long.parseLong(tkvs.get(i).value);
//      if(prevX == 0) prevX = 1L;
//      if(dayX == 0) dayX = 1L;
//      delay = prevX.doubleValue() / dayX + 1;
//      if(i > 2)
//       tkvs.get(i - 2).value = String.valueOf(formatter.format(delay));
//     }
//    tkvs.remove(0); // ignore the first value
//    tkvs.remove(tkvs.size() - 1); // ignore the initial date
//    setTableLayout(getTableRows(tkvs));
//   }
  private void populateTerraDetailsR0() {
    ArrayList<TableKeyValue> tkvs = new ArrayList<TableKeyValue>();
    String sqlNewCases = "select date, sum(new_cases) as sumNewCases from data group by date order by date desc";
    Cursor cSumNewCases = db.rawQuery(sqlNewCases, null);
    Long dayX = 0L;
    Long prevX = 0L;
    cSumNewCases.moveToLast();
    do {
      TableKeyValue tkv = new TableKeyValue();
      tkv.subClass = Constants.UITerraData;
      tkv.key = cSumNewCases.getString(cSumNewCases.getColumnIndex("date"));
      dayX += cSumNewCases.getLong(cSumNewCases.getColumnIndex("sumNewCases"));
      tkv.value = String.valueOf(dayX);
      tkvs.add(tkv);
     } while(cSumNewCases.moveToPrevious());
    Collections.reverse(tkvs);
    double rNought = 0.0;
    for(int i = 1; i < tkvs.size() - 1; i++) {
      prevX = Long.parseLong(tkvs.get(i - 1).value);
      dayX = Long.parseLong(tkvs.get(i).value);
      rNought = dayX.doubleValue() / prevX + 1;
      if(i > 1)
       tkvs.get(i - 2).value = String.valueOf(formatter.format(rNought));
     }
    tkvs.remove(0); // ignore the first value
    tkvs.remove(tkvs.size() - 1); // ignore the initial date
    setTableLayout(getTableRows(tkvs));
   }
 }
