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
import android.os.*;

class UI extends AsyncTask<Void, Void, Void>
 {
  private Context context = null;
  private TableLayout tableLayout = null;
  private TableLayout tableLayoutHeader = null;
  private TableLayout tableLayoutFooter = null;
  private long id = 0;
  protected SQLiteDatabase db = null;
  private ProgressDialog pd = null;
  Vibrator vibrator = null;
  
  protected UI(Context _context, long _id) {
    context = _context;
    id = _id;
    pd = new ProgressDialog(MainActivity.activity);
    
    vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE) ;
    
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
     this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    else
     this.execute();

    db = Database.getInstance(context);
    ((Activity)context).setContentView(R.layout.table_layout);
    tableLayout = (TableLayout) ((Activity)context).findViewById(R.id.layoutTable);
    tableLayoutHeader = (TableLayout)((Activity)context).findViewById(R.id.layoutTableHeader);
    tableLayoutFooter = (TableLayout)((Activity)context).findViewById(R.id.layoutTableFooter);
   }
   
  @Override
  protected Void doInBackground(Void[] p1) {
    try {
      Thread.sleep(1000);
     } catch(InterruptedException e) {}
    return null;
   }

  @Override
  protected void onPreExecute() {
    pd.setMessage("Generating...");
    pd.show();
    super.onPreExecute();
   }

  @Override
  protected void onPostExecute(Void result) {
    pd.hide();
    super.onPostExecute(result);
   }

  protected ArrayList<TableRow> getTableRows(ArrayList<TableKeyValue> tkvs) {
    ArrayList<TableRow> tableRows = new ArrayList<TableRow>();
    boolean bColourSwitch = true;
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
           onClickListenerFired(p1, tkv);
          }
        });
      textViewValue.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View p1) {
           onClickListenerFired(p1, tkv);
          }
        });
      textViewValue.setTextSize(18);
      textViewKey.setLayoutParams(cellParams);
      textViewValue.setLayoutParams(cellParams);
      textViewKey.setText(tkv.key);
      textViewValue.setText(tkv.value);
      tableRow.addView(textViewKey);
      tableRow.addView(textViewValue);
      if(bColourSwitch) {
        bColourSwitch = !bColourSwitch; 
        tableRow.setBackgroundColor(Color.parseColor("#F7FAFD"));
       } else {
        bColourSwitch = !bColourSwitch;
         tableRow.setBackgroundColor(Color.parseColor("#ECF8F6"));
       }

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
    TextView textViewR = new TextView(context);
    textViewL.setTextSize(18);
    textViewR.setTextSize(18);
    textViewL.setLayoutParams(cellParams);
    textViewR.setLayoutParams(cellParams);
    textViewL.setText(keyDescription);
    textViewL.setTypeface(null, Typeface.BOLD);
    textViewR.setText(valueDescription);
    textViewR.setTypeface(null, Typeface.BOLD);
    tableRow.addView(textViewL);
    tableRow.addView(textViewR);
    tableRow.setBackgroundColor(Color.parseColor("#E6E6CA"));
    tableLayoutHeader.addView(tableRow);
   }

  protected void setTableLayout(ArrayList<TableRow> tableRows) {
    for(TableRow tableRow: tableRows) {
      tableLayout.addView(tableRow);
     }
   }

  protected void registerOnStack(String _TV, Context _context, long _id) {
    MainActivity.stack.push(new UIStackInfo(_TV, _context, _id));
   }

  private void onClickListenerFired(View p1, TableKeyValue tkv) {
    try {
      if(tkv.subClass.equals(Constants.UICountry) && tkv.key.equals("Population"))
       return;
      Double.parseDouble(tkv.value.replace(",", "").replace("%", ""));
      vibrator.vibrate(100);
      if(tkv.subClass.equals(Constants.UITerra)) {
        if(tkv.key.equals(Constants.UITerraPopulation)) {
          new UIContinents(context, tkv.tableId);
         } else {
          new UITerraData(context, tkv.tableId, tkv.field);
         }
       } else if(tkv.subClass.equals(Constants.UIContinent)) {
        new UIRegion(context, tkv.tableId);
       } else if(tkv.subClass.equals(Constants.UIRegion)) {
        new UICountry(context, tkv.tableId);
       } else if(tkv.subClass.equals(Constants.UICountry)) {
        new UIData(context, tkv.tableId, tkv.field); 
       }} catch(Exception e) {
      Log.d("UI", e.toString());
     } finally {
     }
   }

  protected void setFooter(String description) {
    TableRow tableRow = new TableRow(context);
    LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    tableRow.setLayoutParams(tableRowParams);

    TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
    cellParams.weight = 9;
    TextView textView = new TextView(context);
    textView.setTextSize(18);
    textView.setLayoutParams(cellParams);
    textView.setText(description);
    textView.setTypeface(null, Typeface.BOLD);
    textView.setGravity(Gravity.CENTER);
    tableRow.addView(textView);
    tableRow.setBackgroundColor(Color.parseColor("#E6E6CA"));
    tableLayoutFooter.addView(tableRow);
   }

 }
