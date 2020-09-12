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

  public LV(Context _context, String[] _values) {
    context = _context;
    values = _values;
    try {
      listView = (ListView) ((Activity)context).findViewById(R.id.lstView);
      // view & view id = of list_row.xml
      ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.list_row, R.id.lstRow, values);  
      listView.setAdapter(adapter);

      listView.setOnItemClickListener(new OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           String item = (String)listView.getItemAtPosition(position);
          }
        });

        } catch(Exception e) {
      String s = e.toString();
     }
   }
 }
