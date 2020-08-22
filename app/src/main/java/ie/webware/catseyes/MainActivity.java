package ie.webware.catseyes;

import android.app.*;
import android.os.*;
import android.widget.*;
import java.util.*;


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
					new WorldOmeterDatabase().populateLocalDatabase();
				} catch(Exception e){
					Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
				}
			}
		});
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
		} finally {
			Toast.makeText(MainActivity.this, "Building Database", Toast.LENGTH_LONG).show();
		}		
    }
}


