package ie.webware.catseyes;

import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.util.*;
import android.widget.*;
import java.text.*;
import java.util.*;
import android.view.View.*;
import android.view.*;
import android.text.style.*;
import android.graphics.*;

class TV
 {
  private Context context = null;
  private TableLayout tableLayout = null;
  private TableLayout tableLayoutHeader = null;
  private long id = 0;
  SQLiteDatabase db = null;

  public TV(Context _context, long _id) {
    context = _context;
    id = _id;
    db = Database.getInstance(context);
    ((Activity)context).setContentView(R.layout.table_layout);
    tableLayout = (TableLayout) ((Activity)context).findViewById(R.id.layoutTable);
    tableLayoutHeader = (TableLayout)((Activity)context).findViewById(R.id.layoutTableHeader);
   }

  protected ArrayList<TableRow> getTableRows(ArrayList<TableKeyValue> tkvs) {
    ArrayList<TableRow> tableRows = new ArrayList<TableRow>();
    for(final TableKeyValue tkv: tkvs) {
      TableRow tableRow = new TableRow(context);
      LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
      tableRow.setLayoutParams(tableRowParams);

      TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
      cellParams.weight = 9;
      TextView textViewKey = new TextView(context);
      textViewKey.setTextSize(18);
      TextView textViewValue = new TextView(context);
      textViewKey.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View p1) {
           try {
             //Double.parseDouble(tkv.value.replace(",", ""));
             if(tkv.subClass.equals("TVTerra")) {
               new TVRegion(context, tkv.tableId);
              } else if(tkv.subClass.equals("TVRegion")) {
               new TVCountry(context, tkv.tableId);
              } else if(tkv.subClass.equals("TVCountry")) {
               new TVData(context, tkv.tableId, tkv.field); 
              }
            } catch(Exception e) {}
          }
        });
      textViewValue.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View p1) {
           try {
             //Double.parseDouble(tkv.value.replace(",", ""));
             if(tkv.subClass.equals("TVTerra")) {
               new TVRegion(context, tkv.tableId);
             } else if(tkv.subClass.equals("TVRegion")) {
               new TVCountry(context, tkv.tableId);
              } else if(tkv.subClass.equals("TVCountry")) {
               new TVData(context, tkv.tableId, tkv.field); 
             }
            } catch(Exception e) {}
          }
        });
      textViewValue.setTextSize(18);
      textViewKey.setLayoutParams(cellParams);
      textViewValue.setLayoutParams(cellParams);
      textViewKey.setText(tkv.key);
      textViewValue.setText(tkv.value);
      //textViewKey.setTypeface(Typeface.MONOSPACE);
      //textViewValue.setTypeface(Typeface.MONOSPACE);
      tableRow.addView(textViewKey);
      tableRow.addView(textViewValue);
      tableRows.add(tableRow);
     }

    return tableRows;
   }
   
  protected void setHeader(String keyDescription, String valueDescription) {
    TableRow tableRow = new TableRow(context);
    LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    tableRow.setLayoutParams(tableRowParams);

    TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
    cellParams.weight = 9;
    TextView textViewL = new TextView(context);
    textViewL.setTextSize(18);
    TextView textViewR = new TextView(context);
    textViewR.setTextSize(18);
    textViewL.setLayoutParams(cellParams);
    textViewR.setLayoutParams(cellParams);
    textViewL.setText(keyDescription);
    textViewL.setTypeface(null, Typeface.BOLD);
    textViewR.setText(valueDescription);
    textViewR.setTypeface(null, Typeface.BOLD);
    tableRow.addView(textViewL);
    tableRow.addView(textViewR);
    tableLayoutHeader.addView(tableRow);
   }

  protected void setTableLayout(ArrayList<TableRow> tableRows) {
    for(TableRow tableRow: tableRows) {
      tableLayout.addView(tableRow);
     }
   }
 }
