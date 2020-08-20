package ie.webware.catseyes;
import android.database.sqlite.*;

public class SQL
{
	private SQL(){}
    private static final SQL instance = new SQL();
    public static SQL getInstance(){
        return instance;
    }
};
