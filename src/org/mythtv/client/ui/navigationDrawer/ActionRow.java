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
public class ActionRow implements Row {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	
	private String mAction;
	
	public ActionRow( Context context, String action ) {
		this.mContext = context;
		this.mLayoutInflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
		this.mAction = action;
	}
	
	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.NavigationDrawerActivity.Row#getView(android.view.View)
	 */
	@Override
	public View getView( View convertView ) {
		
		ViewHolder holder = null;
		
        if( null == convertView ) {

    		convertView = mLayoutInflater.inflate( R.layout.navigation_drawer_action, null );

    		holder = new ViewHolder();
    		holder.action = (TextView) convertView.findViewById( R.id.navigation_drawer_action );
    		
    		convertView.setTag( holder );
     			
        } else {

    		holder = (ViewHolder) convertView.getTag();
        }
            
        holder.action.setText( mAction );
        
        return convertView;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.NavigationDrawerActivity.Row#getViewType()
	 */
	@Override
	public int getViewType() {
		return TopLevelRowType.ACTION_ROW.ordinal();
	}
	
	private static class ViewHolder {
		
		TextView action;
		
	}
	
}
