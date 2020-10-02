package ie.webware.catseyes;
import android.content.*;
import java.util.*;

public class UIStackInfo extends Stack<String>
{
  String UI = null;
  Context context = null;
  long id = 0; 
  
  public UIStackInfo(String _UI, Context _context, long _id) {
   UI = _UI;
   context = _context;
   id = _id;
  }
}


