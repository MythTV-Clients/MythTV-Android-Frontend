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
package org.mythtv.client.ui.dvr.navigationDrawer;

import org.mythtv.R;
import org.mythtv.client.MainApplication;
import org.mythtv.client.ui.navigationDrawer.Row;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.service.util.DateUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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

	protected OnRefreshListener listener;
	
	private boolean mImplemented;

	public interface OnRefreshListener {
		
		void refresh( Row row );
		
	}

	/**
	 * @param context
	 * @param action
	 */
	public DvrLastUpdateActionRow( Context context, EtagInfoDelegate etag, boolean implemented ) {
		this.mContext = context;
		this.mLayoutInflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
		this.mMainApplication = (MainApplication) mContext.getApplicationContext();
		
		this.mEtag = etag;
		
		this.mImplemented = implemented;
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
        
        holder.refresh.setOnClickListener( new OnClickListener() {

			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( final View view ) {
				sendParent();
			}
        	
        });
        
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

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.navigationDrawer.Row#isImplemented()
	 */
	@Override
	public boolean isImplemented() {
		return mImplemented;
	}

	public void setOnRefreshListener( OnRefreshListener listener ) {
		this.listener = listener;
	}
	
	protected abstract void sendParent();
	
	private static class ViewHolder {
		
		TextView date;
		ImageButton refresh;
		
	}

}
