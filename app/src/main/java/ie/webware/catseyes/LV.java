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
    
    values = new String[] { "Android List View", 
      "Adapter implementation",
      "Simple List View In Android",
      "Create List View Android", 
      "Android Example", 
      "List View Source Code", 
      "List View Array Adapter", 
      "Android Example List View" 
     };
    // First parameter - Context
    // Second parameter - Layout for the row
    // Third parameter - ID of the TextView to which the data is written
    // Forth - the Array of data
    //ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.list_row, R.id.lstView, values);
    ArrayAdapter adapter = new ArrayAdapter<String>(context, R.layout.list_row, values);
    
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(new OnItemClickListener() {
       @Override
       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         // TODO: Implement this method
        }
    });
  } catch(Exception e) {
   String s = e.toString();
  }
 }
}
