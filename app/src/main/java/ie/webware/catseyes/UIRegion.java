package ie.webware.catseyes;
import android.content.*;
import java.text.*;
import java.util.*;
import android.database.*;
import android.os.*;

public class UIRegion extends UI
 {
  Context context = null;
  long regionId = 0;
  DecimalFormat formatter = null;
  private String continent = null;

  public UIRegion(Context _context, long _regionId) {
    super(_context, _regionId);
    context = _context;
    regionId = _regionId;
    formatter = new DecimalFormat("#,###.##");

    uiHandler();
   }

  private void uiHandler() {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
       @Override
       public void run() {
         populateTerra();
         setHeader(continent, "Cases/Million");
         setFooter(continent + " : Cases/Million");
         registerOnStack(Constants.UIRegion, context, regionId);
        }
      });
   }
  private void populateTerra() {
    ArrayList<TableKeyValue> tkvs = new ArrayList<TableKeyValue>();
    String sql = "select id, continent, location from country where fk_region = # order by location".replace("#", String.valueOf(regionId));
    Cursor cCountry = db.rawQuery(sql, null);
    cCountry.moveToFirst();
    continent = cCountry.getString(cCountry.getColumnIndex("continent"));
    TableKeyValue tkv = new TableKeyValue();
    do {
      tkv.key = cCountry.getString(cCountry.getColumnIndex("location"));
      long currentCountryId = cCountry.getInt(cCountry.getColumnIndex("ID"));
      String populationSql = "select sum(population) as population from country where id = #".replace("#", String.valueOf(currentCountryId));
      Cursor cPopulation = db.rawQuery(populationSql, null);
      cPopulation.moveToFirst();
      Long population = cPopulation.getLong(cPopulation.getColumnIndex("population"));
      String casesSql = "select sum(new_cases) as total_cases from data join country on data.fk_country = country.id where country.id = #".replace("#", String.valueOf(currentCountryId));
      Cursor cCases = db.rawQuery(casesSql, null);
      cCases.moveToFirst();
      Long cases = cCases.getLong(cCases.getColumnIndex("total_cases"));
      tkv.value = String.valueOf(formatter.format(cases.doubleValue() / population * Constants.oneMillion));
      tkv.tableId = currentCountryId;
      tkv.field = tkv.key;
      tkv.subClass = Constants.UIRegion;
      tkvs.add(tkv); 
      tkv = new TableKeyValue();
     } while(cCountry.moveToNext());
     tkvs.sort(new sortStats());
     setTableLayout(getTableRows(tkvs));
   }
   
  class sortStats implements Comparator<TableKeyValue>
   {
    @Override
    public int compare(TableKeyValue tkvA, TableKeyValue tkvB) {
      // TODO: Implement this method
      Double dA = Double.parseDouble(tkvA.value.replace(",", ""));
      Double dB = Double.parseDouble(tkvB.value.replace(",", ""));
      return dA.compareTo(dB);
     }
   }
 }
