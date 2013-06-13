/**
 * 
 */
package org.mythtv.client.ui.navigationDrawer;

import org.mythtv.R;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * @author dmfrey
 *
 */
public class FrontendsRow implements Row {

	private LayoutInflater mLayoutInflater;
	
	public FrontendsRow( LayoutInflater layoutInflater ) {
		this.mLayoutInflater = layoutInflater;
	}
	
	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.NavigationDrawerActivity.Row#getView(android.view.View)
	 */
	@Override
	public View getView( View convertView ) {
		
		ViewHolder holder = null;
		
        if( null == convertView ) {

    		// TODO: Change this to the right view
    		convertView = mLayoutInflater.inflate( R.layout.navigation_drawer_connected_profile, null );

    		holder = new ViewHolder();
    		holder.header = (TextView) convertView.findViewById( R.id.navigation_drawer_connected_profile_hostname );
    		
    		convertView.setTag( holder );
     			
        } else {

    			holder = (ViewHolder) convertView.getTag();
        }
            
        holder.header.setText( "" );
//        holder.url.setText( mLocationProfile.getUrl() );
        
        return convertView;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.NavigationDrawerActivity.Row#getViewType()
	 */
	@Override
	public int getViewType() {
		return TopLevelRowType.FRONTENDS_ROW.ordinal();
	}
	
	private static class ViewHolder {
		
		TextView header;
		
	}
	
}
