/**
 * 
 */
package org.mythtv.client.ui.navigationDrawer;

import org.mythtv.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * @author dmfrey
 *
 */
public class VersionRow implements Row {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	
	private String mName = "", mVersion = "";
	
	public VersionRow( Context context, String name, String version ) {
		this.mContext = context;
		this.mLayoutInflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
		this.mName = name;
		this.mVersion = version;
	}
	
	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.NavigationDrawerActivity.Row#getView(android.view.View)
	 */
	@Override
	public View getView( View convertView ) {
		
		ViewHolder holder = null;
		
        if( null == convertView ) {

    		convertView = mLayoutInflater.inflate( R.layout.navigation_drawer_version, null );

    		holder = new ViewHolder();
    		holder.title = (TextView) convertView.findViewById( R.id.navigation_drawer_version_title );
    		holder.version = (TextView) convertView.findViewById( R.id.navigation_drawer_version_version );
    		
    		convertView.setTag( holder );
     			
        } else {

   			holder = (ViewHolder) convertView.getTag();
        }
            
        return convertView;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.NavigationDrawerActivity.Row#getViewType()
	 */
	@Override
	public int getViewType() {
		return TopLevelRowType.VERSION_ROW.ordinal();
	}
	
	private static class ViewHolder {
		
		TextView title;
		TextView version;
		
	}
	
}
