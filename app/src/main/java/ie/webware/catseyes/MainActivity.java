package ie.webware.catseyes;

import android.app.*;
import android.os.*;
import android.widget.*;
import java.util.*;
import android.database.sqlite.*;


public class MainActivity extends Activity 
{
	static ArrayList<TableKeyValue> tableKeyValue = new ArrayList<TableKeyValue>();
		
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		Thread thread = new Thread(new Runnable() {
			@Override 
			public void run(){
				try{
						new WorldOmeterDatabase(MainActivity.this);
				} catch(Exception e){
					Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
				}
			}
		});
		Toast.makeText(MainActivity.this, "Initialising Worldometer Data", Toast.LENGTH_LONG).show();
		thread.start();
		try {
			thread.join(); // wait for thread to finish
		} catch (InterruptedException e) {
			Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
		} finally {
			Toast.makeText(MainActivity.this, "Building Database", Toast.LENGTH_LONG).show();
		}
//		for(int i = 0; i < tableKeyValue.size(); i++){
//				String kv = tableKeyValue.get(i).table + " " + tableKeyValue.get(i).key + " " + tableKeyValue.get(i).value;
//				Toast.makeText(MainActivity.this, kv, Toast.LENGTH_LONG);
//		}
    }
}

 
