//package ie.webware.catseyes;
//import android.app.*;
//import android.content.*;
//import android.database.*;
//import android.database.sqlite.*;
//import android.util.*;
//import android.view.*;
//import android.widget.*;
//import android.widget.AdapterView.*;
//import java.text.*;
//
//public class LVData
// {
//  Context context = null;
//  Long countryId = 0L;
//  String field = null;
//  DecimalFormat formatter = null;
//  ListView listView = null;
//  LVKeyValue lvKeyValue = null;
//  
//  public LVData(Context _context, long _countryId, String _field) {
//    context = _context;
//    countryId = _countryId;
//    field = _field;
//    formatter = new DecimalFormat("#,###.##");
//    lvKeyValue = new LVKeyValue();
//    
//    populateLVKeyValue();
//
//    try {
//      ((Activity)context).setContentView(R.layout.lv_main);
//      listView = (ListView) ((Activity)context).findViewById(R.id.lstView);
//      ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.lv_row, R.id.lstRow, lvKeyValue.toListValue()); // view & view id = of list_row.xml
//      listView.setAdapter(adapter);
//
//      listView.setOnItemClickListener(new OnItemClickListener() {
//         @Override
//         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//           String item = (String)listView.getItemAtPosition(position);
//           Long dataId = lvKeyValue.toListKey()[position];
//           Toast.makeText(context, dataId + " " + item, Toast.LENGTH_SHORT).show();
//          }
//        });
//
//     } catch(Exception e) {
//      String s = e.toString();
//     }
//   }
//
//  private String[] populateLVKeyValue() throws SQLiteFullException {
//    SQLiteDatabase db = Database.getInstance(context);
//    String sql = "select data.id, date, $ from data where fk_country = # order by date desc".replace("$", field).replace("#", String.valueOf(countryId));
//    sql = sql.replace("#", countryId.toString());
//    Cursor cData = db.rawQuery(sql, null);
//    cData.moveToFirst();
//    long id = 0;
//    String date = null;
//    Double nCases = 0.0;
//    for(int i = 0; i < cData.getCount(); i++) {
//      id = cData.getLong(cData.getColumnIndex("ID"));
//      date = cData.getString(cData.getColumnIndex("date"));
//      try { 
//        date = new SimpleDateFormat("yyyy-MM-dd").parse(date).toString();
//        String[] arrDate = date.split(" ");
//        date = arrDate[0] + " " + arrDate[1] + " " + arrDate[2] + " " + arrDate[5];
//       } catch(ParseException e) {
//        Log.d("LV", e.toString());
//       }
//      nCases = cData.getDouble(cData.getColumnIndex(field));
//      lvKeyValue.key.add(id);
//      String row = date + ":\t #\t ".replace("#", field + "#\t") + nCases.toString();
//      lvKeyValue.value.add(row);
//      cData.moveToNext();
//     }
//    cData.close();
//    return lvKeyValue.toListValue();
//   }
// }
// 
//
