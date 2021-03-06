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
  int nCountry = 0;

  public UITerraData(Context _context, long _idData, String _field) {
    super(_context, _idData);
    context = _context;
    idData = _idData;
    field = _field;
    formatter = new DecimalFormat("#,###.##");

    uiHandler();
   }


  private void uiHandler() {
    sql = "select count(id) as nCountry from country";
    Cursor cursor = db.rawQuery(sql, null);
    cursor.moveToFirst();
    nCountry = cursor.getInt(cursor.getColumnIndex("nCountry"));
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
            fieldDescription = "Test Positivie Rate %";
            //sql = "select date, sum(positive_rate) as positive_rate from data group by date order by date desc";
            populatePositivityDetails();
            break;
           case "R0":
            fieldDescription = "Estimated Growth";//
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

  private void populatePositivityDetails() {
    ArrayList<TableKeyValue> tkvs = new ArrayList<TableKeyValue>();
    String sqlPositivityRate = "select date, sum(new_cases) as cases, sum(new_tests) as tests from data where new_cases > 0 and new_tests > 0 group by date order by date";
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
   
  private void populateTerraDetailsR0() {
    ArrayList<TableKeyValue> tkvs = new ArrayList<TableKeyValue>();
    String sqlNewCases = "select date, sum(new_cases) as sumNewCases from data group by date order by date asc";
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
      if(dayX == 0 && r0avg == 0) continue;
      if(dayX > 0) {
        r0avg += dayX.doubleValue() / prevX.doubleValue();
        tkv.value = String.valueOf(formatter.format(r0avg/nDays));
        tkvs.add(tkv);
        prevX = dayX;
      }
      nDays++;
     } while(cSumNewCases.moveToNext());
    Collections.reverse(tkvs);
    setTableLayout(getTableRows(tkvs));
   }
 }
