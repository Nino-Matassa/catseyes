package ie.webware.catseyes;

import android.app.*;
import android.content.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import java.util.*;

//public class TVStatus
// {
//
//  private Context context = null;
//  private Timer timer = null;
//  TVStatusTask tvStatusTask = null;
//
//  public TVStatus(Context _context) {
//    context = _context;
//    tvStatusTask = new TVStatusTask(context);
//    timer = new Timer();
//    timer.schedule(tvStatusTask,
//                   1 * 1000,   //initial delay
//                   1 * 10000);  //subsequent rate
//   }
// }
//
class TVStatus//Task extends TimerTask
 {
  private Context context = null;
  private TableLayout tableLayout = null;

  public TVStatus(Context _context) {
    context = _context;

    ((Activity)context).setContentView(R.layout.table_layout);
    tableLayout = (TableLayout) ((Activity)context).findViewById(R.id.layoutTable);

    dbStatusTable();
   }

//  @Override
//  public void run() {
//    dbStatusTable();
//   }

  public void dbStatusTable() {

    LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                             LinearLayout.LayoutParams.WRAP_CONTENT);

    /* create a table row */
    TableRow tableRow = new TableRow(context);
    tableRow.setLayoutParams(tableRowParams);

    /* create cell element - textview */
    TextView tv = new TextView(context);
    //tv.setBackgroundColor(0xff12dd12);
    tv.setText("dynamic textview");
    tv.setOnClickListener(new OnClickListener() {
       @Override
       public void onClick(View p1) {
         Toast.makeText(context, "Cell Clicked", Toast.LENGTH_LONG).show();
        }
      });
    //tv.setVisibility(View.GONE); // hide column

    /* create cell element - button */
    Button btn = new Button(context);
    btn.setText("dynamic btn");
    //btn.setBackgroundColor(0xff12dd12);

    /* set params for cell elements */
    TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
    cellParams.weight = 3;
    tv.setLayoutParams(cellParams);
    cellParams.weight = 2;
    cellParams.rightMargin = 10;
    btn.setLayoutParams(cellParams);

    btn.setOnClickListener(new OnClickListener() {

       @Override
       public void onClick(View p1) {
         Toast.makeText(context, "Button Clicked", Toast.LENGTH_LONG).show();
        }
      });

    /* add views to the row */
    tableRow.addView(tv);
    tableRow.addView(btn);

    /* add the row to the table */
    try {
      tableLayout.addView(tableRow);
     } catch(Exception e) {
      Log.d("TVStatus", e.toString());
     }
   }
 }
