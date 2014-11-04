/**
 * This file is part of MythTV Android Frontend
 *
 * MythTV Android Frontend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MythTV Android Frontend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MythTV Android Frontend.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This software can be found at <https://github.com/MythTV-Clients/MythTV-Android-Frontend/>
 */
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
	
	public VersionRow( Context context, int nameResId, String version ) {
	    this(context, context.getString(nameResId), version);
	}
	
	private VersionRow( Context context, String name, String version ) {
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
	
	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.navigationDrawer.Row#getTitle()
	 */
	@Override
	public String getTitle() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.navigationDrawer.Row#getFragment()
	 */
	@Override
	public String getFragment() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.navigationDrawer.Row#isImplemented()
	 */
	@Override
	public boolean isImplemented() {
		return true;
	}

	private static class ViewHolder {
		
		TextView title;
		TextView version;
		
	}
	
}
