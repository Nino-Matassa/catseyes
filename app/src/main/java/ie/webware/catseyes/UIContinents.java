package ie.webware.catseyes;

import android.content.*;
import java.text.*;
import java.util.*;
import android.database.*;
import android.util.*;

public class UIContinents extends UI
 {
  Context context = null;
  long id = 0;
  DecimalFormat formatter = null;

  public UIContinents(Context _context, long _id) {
    super(_context, _id);
    context = _context;
    id = _id;
    formatter = new DecimalFormat("#,###.##");

    populateContinents();
    setHeader("Region", "Cases/Million");
    setFooter("Region : Cases/Million");
    registerOnStack(Constants.UIContinent, context, id);
   }

  private void populateContinents() {
    ArrayList<TableKeyValue> tkvs = new ArrayList<TableKeyValue>();
    String sql = "select id, continent from region order by continent";
    Cursor cRegion = db.rawQuery(sql, null);
    cRegion.moveToFirst();
    TableKeyValue tkv = new TableKeyValue();
    do {
      tkv.key = cRegion.getString(cRegion.getColumnIndex("CONTINENT"));
      long currentRegionId = cRegion.getInt(cRegion.getColumnIndex("ID"));
      String populationSql = "select sum(population) as population from country where fk_region = #".replace("#", String.valueOf(currentRegionId));
      String casesSql = "select sum(new_cases) as total_cases from data join country on data.fk_country = country.id join region on country.fk_region = region.id where region.id = #".replace("#", String.valueOf(currentRegionId));
      Cursor cPopulation = db.rawQuery(populationSql, null);
      cPopulation.moveToFirst();
      Cursor cCases = db.rawQuery(casesSql, null);
      cCases.moveToFirst();
      Long population = cPopulation.getLong(cPopulation.getColumnIndex("population"));
      Long cases = cCases.getLong(cCases.getColumnIndex("total_cases"));
      tkv.value = String.valueOf(formatter.format(cases.doubleValue()/population*Constants.oneMillion));
      tkv.tableId = currentRegionId;
      tkv.field = tkv.key;
      tkv.subClass = Constants.UIContinent;
      tkvs.add(tkv);
      tkv = new TableKeyValue();
     } while(cRegion.moveToNext());
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
