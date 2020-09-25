package ie.webware.catseyes;

import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.util.*;
import android.widget.*;
import java.text.*;
import java.util.*;

class TV
 {
  private Context context = null;
  private TableLayout tableLayout = null;
  private long id = 0;
  SQLiteDatabase db = null;

  public TV(Context _context, long _id) {
    context = _context;
    id = _id;
    db = Database.getInstance(context);
    ((Activity)context).setContentView(R.layout.table_layout);
    tableLayout = (TableLayout) ((Activity)context).findViewById(R.id.layoutTable);
   }
   
  public ArrayList<TableRow> getTableRows(ArrayList<TableKeyValue> tkvs) {
    ArrayList<TableRow> tableRows = new ArrayList<TableRow>();
    for(TableKeyValue tkv: tkvs) {
      TableRow tableRow = new TableRow(context);
      LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
      tableRow.setLayoutParams(tableRowParams);

      TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
      cellParams.weight = 9;
      TextView textViewKey = new TextView(context);
      textViewKey.setTextSize(18);
      TextView textViewValue = new TextView(context);
      textViewValue.setTextSize(18);
      textViewKey.setLayoutParams(cellParams);
      textViewValue.setLayoutParams(cellParams);
      textViewKey.setText(tkv.key);
      textViewValue.setText(tkv.value);
      tableRow.addView(textViewKey);
      tableRow.addView(textViewValue);
      tableRows.add(tableRow);
     }

    return tableRows;
   }

  public void setTableLayout(ArrayList<TableRow> tableRows) {
    for(TableRow tableRow: tableRows) {
      tableLayout.addView(tableRow);
     }
   }
 }
