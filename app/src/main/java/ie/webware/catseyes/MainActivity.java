package ie.webware.catseyes;

import android.app.*;
import android.os.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import org.json.*;
import org.apache.http.*;


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
					new InternetAccess().populateDatabase();
				} catch(Exception e){
					
				}
			}
		});
		thread.start();
    }	
}


