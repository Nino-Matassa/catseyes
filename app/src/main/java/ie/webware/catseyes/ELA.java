package ie.webware.catseyes;
import android.content.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import android.widget.ExpandableListView.*;

public class ELA extends BaseExpandableListAdapter
 {
  private Context context = null;
  private List<String> lstParent = null;
  private HashMap<String, List<String>> hLstChild = null;
  
  public ELA(Context _context, List<String> _lstParent, HashMap<String, List<String>> _hLstChild) {
   context = _context;
   lstParent = _lstParent;
   hLstChild = _hLstChild;
  }

  @Override
  public int getGroupCount() {
    return lstParent.size();
   }

  @Override
  public int getChildrenCount(int index) { // called for each group... use this method for database populating?
    return hLstChild.get(lstParent.get(index)).size();
   }

  @Override
  public Object getGroup(int index) {
    return lstParent.get(index);
   }

  @Override
  public Object getChild(int iGroup, int iChild) {
    return this.hLstChild.get(this.lstParent.get(iGroup)).get(iChild);
   }

  @Override
  public long getGroupId(int index) {
    return index;
   }

  @Override
  public long getChildId(int iGroup, int iChild) {
    return iChild;
   }

  @Override
  public boolean hasStableIds() {
    return false;
   }

  @Override
  public View getGroupView(int position, boolean isExpanded, View vPopulate, ViewGroup vGroup) {
    // populate group view at position, called for each position
    if(vPopulate == null) {
     LayoutInflater lInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
     vPopulate = lInflater.inflate(R.layout.elv_group, null);
    }
    TextView vTxtParent = (TextView)vPopulate.findViewById(R.id.vTxtParent);
    vTxtParent.setText((String)getGroup(position));
    return vPopulate;
   }

  @Override
  public View getChildView(int iParent, int iChild, boolean isLastChild, View vPopulate, ViewGroup vParent) {
    // Called for each child in the hash list
    if(vPopulate == null) {
     LayoutInflater lInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
     vPopulate = lInflater.inflate(R.layout.elv_item, null);
    }
    TextView vTxtChild = (TextView)vPopulate.findViewById(R.id.vTxtChild);
    vTxtChild.setText((String)getChild(iParent, iChild));
    return vPopulate;
   }

  @Override
  public boolean isChildSelectable(int iGroup, int iChild) {
    return true;
   }
 }
