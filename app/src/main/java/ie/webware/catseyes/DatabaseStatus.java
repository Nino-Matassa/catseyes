package ie.webware.catseyes;
import java.util.*;

interface StatusChangedListener
 {
  public void onStatusChanged();
 }

public class DatabaseStatus extends Observable
 {
  private static List<StatusChangedListener> listeners = new ArrayList<StatusChangedListener>();
  private static String status = null;
  public static String getStatus() { return status; }

  public static void setStatus(String _status) { 
    status = _status;
    
    for(StatusChangedListener l : listeners) {
      l.onStatusChanged();
     }
   }
  
   public static void addStatusListener(StatusChangedListener l) {
    listeners.add(l);
   }
   
  }
