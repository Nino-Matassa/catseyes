package ie.webware.catseyes;

import android.content.*;
import java.text.*;
import java.util.*;
import android.database.*;
import android.util.*;

public class TVTerra extends TV {
  Context context = null;
  long id = 0;
  DecimalFormat formatter = null;

  public TVTerra(Context _context, long _id) {
    super(_context, _id);
    context = _context;
    id = _id;
    formatter = new DecimalFormat("#,###.##");

    populateTerra();
   setHeader("Region", "Population");
   }

  private void populateTerra() {
    ArrayList<TableKeyValue> tkvs = new ArrayList<TableKeyValue>();
    String sql = "select id, continent from region order by continent";
    Cursor cRegion = db.rawQuery(sql, null);
    cRegion.moveToFirst();
    TableKeyValue tkv = new TableKeyValue();
    do {
      tkv.key = cRegion.getString(cRegion.getColumnIndex("CONTINENT"));
      long currentRegionId = cRegion.getInt(cRegion.getColumnIndex("ID"));
      String populationSql = "select sum(population) as population from country where fk_region = #".replace("#", String.valueOf(currentRegionId));
      Cursor cPopulation = db.rawQuery(populationSql, null);
      cPopulation.moveToFirst();
      tkv.value = String.valueOf(formatter.format(cPopulation.getLong(cPopulation.getColumnIndex("population"))));
      tkv.tableId = currentRegionId;
      tkv.field = tkv.key;
      tkv.subClass = "TVTerra";
      tkvs.add(tkv);
      tkv = new TableKeyValue();
     } while(cRegion.moveToNext());

    setTableLayout(getTableRows(tkvs));
   }
 }
