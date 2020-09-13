package ie.webware.catseyes;
import android.content.*;
import android.widget.*;
import android.app.*;
import android.widget.AdapterView.*;
import android.view.*;

public class LV
 {
  Context context = null;
  String[] values = null;
  ListView listView = null;
  LVKeyValue lvKeyValue = null;

  public LV(Context _context, LVKeyValue _lvKeyValue) {
    context = _context;
    values = _lvKeyValue.saValue();
    lvKeyValue = _lvKeyValue;
    try {
      ((Activity)context).setContentView(R.layout.lv_main);
      listView = (ListView) ((Activity)context).findViewById(R.id.lstView);
      ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.lv_row, R.id.lstRow, values); // view & view id = of list_row.xml
      listView.setAdapter(adapter);

      listView.setOnItemClickListener(new OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           String item = (String)listView.getItemAtPosition(position);
           Long lId = lvKeyValue.slKey()[position];
           Toast.makeText(context, lId + " " + item, Toast.LENGTH_SHORT).show();
          }
        });

        } catch(Exception e) {
      String s = e.toString();
     }
   }
 }
