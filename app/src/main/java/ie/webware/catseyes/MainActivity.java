package ie.webware.catseyes;

import android.app.*;
import android.os.*;
import android.widget.*;


public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		
    }
	
	public void my_method(TextView tview)
	{
		tview = findViewById(R.id.view_text);
	}
}
