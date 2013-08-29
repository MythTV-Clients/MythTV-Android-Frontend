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
public class FrontendsHeaderRow implements Row {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	
	private String mHeader;
	
	public FrontendsHeaderRow( Context context, String header ) {
		this.mContext = context;
		this.mLayoutInflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
		this.mHeader = header;
	}
	
	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.NavigationDrawerActivity.Row#getView(android.view.View)
	 */
	@Override
	public View getView( View convertView ) {
		
		ViewHolder holder = null;
		
        if( null == convertView ) {

    		convertView = mLayoutInflater.inflate( R.layout.navigation_drawer_header, null );

    		holder = new ViewHolder();
    		holder.header = (TextView) convertView.findViewById( R.id.navigation_drawer_header );
    		
    		convertView.setTag( holder );
     			
        } else {

   			holder = (ViewHolder) convertView.getTag();
        }
            
        holder.header.setText( mHeader );
        
        return convertView;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.NavigationDrawerActivity.Row#getViewType()
	 */
	@Override
	public int getViewType() {
		return TopLevelRowType.FRONTENDS_HEADER_ROW.ordinal();
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
		
		TextView header;
		
	}
	
}
