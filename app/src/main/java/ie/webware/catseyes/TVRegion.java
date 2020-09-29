package ie.webware.catseyes;
import android.content.*;
import java.text.*;
import java.util.*;
import android.database.*;

public class TVRegion extends TV {
  Context context = null;
  long regionId = 0;
  DecimalFormat formatter = null;

  public TVRegion(Context _context, long _regionId) {
    super(_context, _regionId);
    context = _context;
    regionId = _regionId;
    formatter = new DecimalFormat("#,###.##");

    populateTerra();
   }
  private void populateTerra() {
    ArrayList<TableKeyValue> tkvs = new ArrayList<TableKeyValue>();
    String sql = "select id, location from country where fk_region = #".replace("#", String.valueOf(regionId));
    Cursor cCountry = db.rawQuery(sql, null);
    cCountry.moveToFirst();
    TableKeyValue tkv = new TableKeyValue();
    do {
      tkv.key = cCountry.getString(cCountry.getColumnIndex("location"));
      long currentCountryId = cCountry.getInt(cCountry.getColumnIndex("ID"));
      String populationSql = "select sum(population) as population from country where id = #".replace("#", String.valueOf(currentCountryId));
      Cursor cPopulation = db.rawQuery(populationSql, null);
      cPopulation.moveToFirst();
      tkv.value = String.valueOf(formatter.format(cPopulation.getLong(cPopulation.getColumnIndex("population"))));
      tkv.tableId = currentCountryId;
      tkv.field = tkv.key;
      tkv.subClass = "TVRegion";
      tkvs.add(tkv);
      tkv = new TableKeyValue();
     } while(cCountry.moveToNext());

    setTableLayout(getTableRows(tkvs));
    
  }
}
