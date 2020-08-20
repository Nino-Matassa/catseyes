package ie.webware.catseyes;

import android.app.*;
import android.os.*;
import android.widget.*;
import ie.webware.catseyes.*;


public class MainActivity extends Activity 
{
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
					
				}
			}
		});
		thread.start();
			//Toast.makeText(MainActivity.this, "Json array built", Toast.LENGTH_LONG).show();
    }	
}


