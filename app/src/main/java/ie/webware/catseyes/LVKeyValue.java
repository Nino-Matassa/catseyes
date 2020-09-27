package ie.webware.catseyes;
import java.util.*;

public class LVKeyValue
{
 ArrayList<Long> key = new ArrayList<Long>();
 ArrayList<String> value = new ArrayList<String>();
  
 public Long[] toListKey() {
   return  key.toArray(new Long[0]);
 }
 
 public String[] toListValue() {
  return value.toArray(new String[0]);
 }
}
