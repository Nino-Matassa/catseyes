package ie.webware.catseyes;
import android.content.*;
import java.util.*;

public class TVStackInfo extends Stack<String>
{
  String TV = null;
  Context context = null;
  long id = 0; 
  
  public TVStackInfo(String _TV, Context _context, long _id) {
   TV = _TV;
   context = _context;
   id = _id;
  }
}


