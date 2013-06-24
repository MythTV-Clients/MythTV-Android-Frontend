/**
 * 
 */
package org.mythtv.client.ui.dvr.navigationDrawer;

import org.mythtv.R;
import org.mythtv.client.MainApplication;
import org.mythtv.client.ui.navigationDrawer.Row;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.service.util.DateUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * @author dmfrey
 *
 */
public abstract class DvrLastUpdateActionRow implements Row {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	
	private MainApplication mMainApplication;
	
	private EtagInfoDelegate mEtag;

	/**
	 * @param context
	 * @param action
	 */
	public DvrLastUpdateActionRow( Context context, EtagInfoDelegate etag ) {
		this.mContext = context;
		this.mLayoutInflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
		this.mMainApplication = (MainApplication) mContext.getApplicationContext();
		
		this.mEtag = etag;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.NavigationDrawerActivity.Row#getView(android.view.View)
	 */
	@Override
	public View getView( View convertView ) {
		
		ViewHolder holder = null;
		
        if( null == convertView ) {

    		convertView = mLayoutInflater.inflate( R.layout.navigation_drawer_action_last_update, null );

    		holder = new ViewHolder();
    		holder.date = (TextView) convertView.findViewById( R.id.navigation_drawer_action_last_update_date );
    		holder.refresh = (ImageButton) convertView.findViewById( R.id.navigation_drawer_action_last_update_refresh );
    		
    		convertView.setTag( holder );
     			
        } else {

    		holder = (ViewHolder) convertView.getTag();
        }
        
        if( null != mEtag.getLastModified() ) {
        	holder.date.setText( DateUtils.getDateTimeUsingLocaleFormattingPretty( mEtag.getLastModified(), mMainApplication.getDateFormat(), mMainApplication.getClockType() ) );
        } else {
            holder.date.setText( "" );
        }
        
        return convertView;
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

	private static class ViewHolder {
		
		TextView date;
		ImageButton refresh;
		
	}

}
